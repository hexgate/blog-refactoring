package eu.hexgate.blog.product;

public class UnsafeTextException extends RuntimeException {

    public UnsafeTextException() {
        super("Content is unsafe");
    }
}
