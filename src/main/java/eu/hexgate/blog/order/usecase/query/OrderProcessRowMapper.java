package eu.hexgate.blog.order.usecase.query;

import eu.hexgate.blog.order.domain.process.OrderStatus;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class OrderProcessRowMapper implements RowMapper<OrderProcessRow> {
    @Override
    public OrderProcessRow mapRow(ResultSet rs, int i) throws SQLException {
        return new OrderProcessRow(
                rs.getString("STEP_ID"),
                rs.getString("CORRELATED_ORDER_ID"),
                OrderStatus.valueOf(rs.getString("STATUS")),
                rs.getInt("STEP")
        );
    }
}