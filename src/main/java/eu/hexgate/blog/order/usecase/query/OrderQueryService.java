package eu.hexgate.blog.order.usecase.query;

import eu.hexgate.blog.externalmodules.ShippingService;
import eu.hexgate.blog.externalmodules.TaxService;
import eu.hexgate.blog.order.domain.Price;
import eu.hexgate.blog.order.domain.PriceWithTax;
import eu.hexgate.blog.order.domain.Tax;
import eu.hexgate.blog.order.dto.OrderDto;
import eu.hexgate.blog.order.dto.OrderPositionDto;
import eu.hexgate.blog.order.dto.ProductDto;
import eu.hexgate.blog.order.usecase.process.OrderStatus;
import io.vavr.control.Option;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static eu.hexgate.blog.order.usecase.query.JdbcUtils.selectSingle;

@Service
@Transactional(readOnly = true)
public class OrderQueryService {

    private final JdbcTemplate jdbcTemplate;
    private final TaxService taxService;
    private final ShippingService shippingService;

    public OrderQueryService(JdbcTemplate jdbcTemplate, TaxService taxService, ShippingService shippingService) {
        this.jdbcTemplate = jdbcTemplate;
        this.taxService = taxService;
        this.shippingService = shippingService;
    }

    public Option<OrderDto> findByOrderId(String orderId) {
        return findOrderProcess(orderId)
                .map(orderProcessRow -> loadConnectedData(orderProcessRow));
    }

    private OrderDto loadConnectedData(OrderProcessRow orderProcessRow) {
        final List<OrderPositionRow> orderPositions = findOrderPositions(orderProcessRow.getStepId());

        final BigDecimal basePrice = orderPositions.stream()
                .map(it -> it.getPrice().multiply(new BigDecimal(it.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        final PriceWithTax estimatedPrice = Price.of(basePrice)
                .withTax(Tax.asDecimalValue(taxService.gerCurrentTax()))
                .add(Price.of(shippingService.getCurrentShippingPrice()));

        return new OrderDto(
                orderProcessRow.getCorrelatedOrderId(),
                orderProcessRow.getStatus(),
                basePrice,
                estimatedPrice.asBigDecimal(),
                findConfirmedTotalPrice(orderProcessRow).getOrNull(),
                convertPositions(orderPositions)
        );
    }

    private List<OrderPositionRow> findOrderPositions(String orderId) {
        String sql = "SELECT * FROM ORDER_POSITION_VIEW WHERE ORDER_ID = ?";
        return jdbcTemplate.query(sql, new OrderPositionRowMapper(), orderId);
    }

    private Option<OrderProcessRow> findOrderProcess(String orderId) {
        return selectSingle(() -> jdbcTemplate.queryForObject("SELECT * FROM ORDER_PROCESS WHERE CORRELATED_ORDER_ID = ? ORDER BY STEP DESC LIMIT 1", new OrderProcessRowMapper(), orderId));
    }

    private Option<BigDecimal> findConfirmedTotalPrice(OrderProcessRow orderProcess) {
        if (!orderProcess.getStatus().equals(OrderStatus.CONFIRMED)) {
            return Option.none();
        }

        return selectSingle(() -> jdbcTemplate.queryForObject("SELECT CONFIRMED_TOTAL_PRICE FROM CONFIRMED_ORDER WHERE ID = ?", Integer.class, orderProcess.getStepId()))
                .map(BigDecimal::new);
    }

    private List<OrderPositionDto> convertPositions(List<OrderPositionRow> rows) {
        return rows.stream()
                .map(row -> new OrderPositionDto(convertProduct(row), row.getQuantity()))
                .sorted(Comparator.comparing(o -> o.getProduct().getName()))
                .collect(Collectors.toList());
    }

    private ProductDto convertProduct(OrderPositionRow row) {
        return new ProductDto(row.getProductId(), row.getProductName(), row.getPrice());
    }
}
