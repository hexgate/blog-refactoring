package eu.hexgate.blog.order.usecase.query;

import org.springframework.dao.EmptyResultDataAccessException;

import java.util.Optional;
import java.util.function.Supplier;

public class JdbcUtils {

    private JdbcUtils() {
    }

    public static <T> Optional<T> selectSingle(Supplier<T> query) {
        try {
            return Optional.of(query.get());
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }
}
