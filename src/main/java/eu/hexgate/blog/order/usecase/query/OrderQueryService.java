package eu.hexgate.blog.order.usecase.query;

import eu.hexgate.blog.order.dto.OrderDto;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class OrderQueryService {

    private final JdbcTemplate jdbcTemplate;

    public OrderQueryService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public OrderDto findByOrderId(String orderId) {
        final OrderProcessRow orderProcessRow = find(orderId);

        final List<OrderPositionRow> orderPositions = getOrderPositions(orderProcessRow.getStepId());

        return new OrderDto(
                null,
                null,
                null,
                null,
                null,
                List.of()
        );
    }

    private List<OrderPositionRow> getOrderPositions(String orderId) {
//        String sql = "SELECT * FROM ORDER_POSITION WHERE ORDER_ID = ?";
//        return jdbcTemplate.queryForList(sql, OrderPositionRow.class, orderId);
        String sql = "SELECT * FROM ORDER_POSITION";
        return jdbcTemplate.queryForList(sql, OrderPositionRow.class);
    }

    private OrderProcessRow find(String orderId) {
        return jdbcTemplate.queryForObject("SELECT * FROM ORDER_PROCESS WHERE CORRELATED_ORDER_ID = ? ORDER BY STEP DESC LIMIT 1", OrderProcessRow.class, orderId);
    }
}
