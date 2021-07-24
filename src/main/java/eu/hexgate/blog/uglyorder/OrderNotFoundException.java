package eu.hexgate.blog.uglyorder;

public class OrderNotFoundException extends RuntimeException {

    private String orderId;

    public OrderNotFoundException(String orderId) {
        super("Order not found.");
        this.orderId = orderId;
    }
}
