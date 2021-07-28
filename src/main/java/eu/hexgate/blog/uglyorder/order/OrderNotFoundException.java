package eu.hexgate.blog.uglyorder.order;

import eu.hexgate.blog.refactoredorder.domain.CorrelatedOrderId;

public class OrderNotFoundException extends RuntimeException {

    private final String orderId;

    public OrderNotFoundException(CorrelatedOrderId correlatedOrderId) {
        super("Order not found.");
        this.orderId = correlatedOrderId.getId();
    }

    public String getOrderId() {
        return orderId;
    }
}
