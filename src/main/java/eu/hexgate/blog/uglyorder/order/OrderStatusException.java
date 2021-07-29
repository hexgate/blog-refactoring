package eu.hexgate.blog.uglyorder.order;

import eu.hexgate.blog.refactoredorder.domain.order.CorrelatedOrderId;
import eu.hexgate.blog.refactoredorder.domain.order.process.OrderStatus;

public class OrderStatusException extends RuntimeException {
    private final String orderId;
    private final OrderStatus status;

    public OrderStatusException(CorrelatedOrderId correlatedOrderId, String message, OrderStatus status) {
        super(message);
        this.orderId = correlatedOrderId.getId();
        this.status = status;
    }

    public String getOrderId() {
        return orderId;
    }

    public OrderStatus getStatus() {
        return status;
    }
}
