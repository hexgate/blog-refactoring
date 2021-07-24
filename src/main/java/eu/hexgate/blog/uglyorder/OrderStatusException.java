package eu.hexgate.blog.uglyorder;

public class OrderStatusException extends RuntimeException {
    private String orderId;
    private OrderStatus status;

    public OrderStatusException(String message, String orderId, OrderStatus status) {
        super(message);
        this.orderId = orderId;
        this.status = status;
    }

    public String getOrderId() {
        return orderId;
    }

    public OrderStatus getStatus() {
        return status;
    }
}
