package eu.hexgate.blog.order.usecase.query;

import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class OrderPositionRowMapper implements RowMapper<OrderPositionRow> {
    @Override
    public OrderPositionRow mapRow(ResultSet resultSet, int i) throws SQLException {
        return new OrderPositionRow(
                resultSet.getString("ORDER_ID"),
                resultSet.getString("PRODUCT_INT"),
                resultSet.getInt("QUANTITY")
        );
    }
}
