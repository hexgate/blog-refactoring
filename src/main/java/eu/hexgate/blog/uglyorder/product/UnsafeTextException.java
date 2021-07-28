package eu.hexgate.blog.uglyorder.product;

public class UnsafeTextException extends RuntimeException {

    public UnsafeTextException() {
        super("Content is unsafe");
    }
}
