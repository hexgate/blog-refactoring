package eu.hexgate.blog.order.usecase;

import eu.hexgate.blog.order.domain.errors.DomainError;
import io.vavr.control.Either;

public interface UseCase<T extends UseCase.Command> {

    Either<DomainError, String> execute(T command);

    interface Command {
    }
}
