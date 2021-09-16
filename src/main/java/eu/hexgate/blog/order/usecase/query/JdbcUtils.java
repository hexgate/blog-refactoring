package eu.hexgate.blog.order.usecase.query;

import io.vavr.control.Option;
import org.springframework.dao.EmptyResultDataAccessException;

import java.util.function.Supplier;

public class JdbcUtils {

    private JdbcUtils() {
    }

    public static <T> Option<T> selectSingle(Supplier<T> query) {
        try {
            return Option.of(query.get());
        } catch (EmptyResultDataAccessException e) {
            return Option.none();
        }
    }
}
