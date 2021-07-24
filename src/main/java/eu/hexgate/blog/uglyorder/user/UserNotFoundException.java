package eu.hexgate.blog.uglyorder.user;

public class UserNotFoundException extends RuntimeException {

    private String id;

    public UserNotFoundException(String id) {
        super("User not found.");
        this.id = id;
    }
}
