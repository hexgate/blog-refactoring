package eu.hexgate.blog.refactoredorder.usecase;

public interface UseCase<T extends UseCase.Command> {

    String execute(T command);

    interface Command {
    }
}
