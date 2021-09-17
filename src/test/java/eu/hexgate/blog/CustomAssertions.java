package eu.hexgate.blog;

import io.vavr.control.Either;
import org.assertj.core.api.Assertions;

import java.util.function.Consumer;

public class CustomAssertions {

    public static <L, R> void assertIsRight(Either<L, R> either, Consumer<R> assertions) {
        Assertions.assertThat(either.isRight()).isTrue();
        either.forEach(assertions);
    }

    public static <L, R> void assertIsLeft(Either<L, R> either, Consumer<L> assertions) {
        Assertions.assertThat(either.isLeft()).isTrue();
        either.peekLeft(assertions);
    }
}
