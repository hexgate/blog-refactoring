package eu.hexgate.blog.order.usecase;

public interface UseCase<T extends UseCase.Command> {

    String execute(T command);

    interface Command {
    }
}
