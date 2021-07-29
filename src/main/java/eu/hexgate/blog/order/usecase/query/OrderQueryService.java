package eu.hexgate.blog.order.usecase.query;

import eu.hexgate.blog.order.domain.CorrelatedOrderId;
import eu.hexgate.blog.order.domain.process.OrderStatus;
import eu.hexgate.blog.order.dto.OrderDto;
import eu.hexgate.blog.order.dto.OrderNotFoundException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class OrderQueryService {

    private final OrderProcessViewRepository orderProcessViewRepository;
    private final JdbcTemplate jdbcTemplate;

    public OrderQueryService(OrderProcessViewRepository orderProcessViewRepository, JdbcTemplate jdbcTemplate) {
        this.orderProcessViewRepository = orderProcessViewRepository;
        this.jdbcTemplate = jdbcTemplate;
    }

    public OrderDto findByOrderId(String orderId) {
        final OrderProcessView orderProcessView = orderProcessViewRepository.findNewest(orderId)
                .orElseThrow(() -> new OrderNotFoundException(CorrelatedOrderId.fromString(orderId)));

        if (orderProcessView.getStatus().equals(OrderStatus.CONFIRMED)) {
            // todo confirmed price
        }

        final List<OrderPositionRow> orderPositions = getOrderPositions(orderId);

        return new OrderDto(
                orderProcessView.getId(),
                orderProcessView.getStatus(),
                null,
                null,
                null,
                List.of()
        );
    }

    private List<OrderPositionRow> getOrderPositions(String orderId) {
        String sql = "SELECT * FROM ORDER_POSITION WHERE ORDER_ID = ?";
        return jdbcTemplate.queryForList(sql, OrderPositionRow.class, orderId);
    }
}
