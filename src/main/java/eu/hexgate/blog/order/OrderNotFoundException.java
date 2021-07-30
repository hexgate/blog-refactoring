package eu.hexgate.blog.order;

public class OrderNotFoundException extends RuntimeException {

    private String orderId;

    public OrderNotFoundException(String orderId) {
        super("Order not found.");
        this.orderId = orderId;
    }

    public String getOrderId() {
        return orderId;
    }
}
