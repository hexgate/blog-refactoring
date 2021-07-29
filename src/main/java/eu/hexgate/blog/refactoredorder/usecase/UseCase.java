package eu.hexgate.blog.refactoredorder.usecase;

public interface UseCase<Command> {

    String execute(Command command);

    interface Command {
    }
}
