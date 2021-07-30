package eu.hexgate.blog.order.usecase.query;

import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class OrderPositionRowMapper implements RowMapper<OrderPositionRow> {
    @Override
    public OrderPositionRow mapRow(ResultSet rs, int i) throws SQLException {
        return new OrderPositionRow(
                rs.getString("ORDER_ID"),
                rs.getString("PRODUCT_ID"),
                rs.getInt("QUANTITY"),
                rs.getString("PRODUCT_NAME"),
                rs.getBigDecimal("PRICE")
        );
    }
}
