package eu.hexgate.blog.uglyorder.order;

import eu.hexgate.blog.refactoredorder.domain.process.OrderStatus;

public class OrderStatusException extends RuntimeException {
    private String orderId;
    private OrderStatus status;

    public OrderStatusException(String orderId, String message, OrderStatus status) {
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
