package eu.hexgate.blog.refactoredorder.usecase;

public interface UseCase<Command, T> {

    T execute(Command command);

    interface Command {
    }
}
