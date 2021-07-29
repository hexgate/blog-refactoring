package eu.hexgate.blog.order.usecase.acceptorder;

import eu.hexgate.blog.order.usecase.UseCase;

public class AcceptOrderCommand implements UseCase.Command {

    private final String orderId;

    public AcceptOrderCommand(String orderId) {
        this.orderId = orderId;
    }

    public String getOrderId() {
        return orderId;
    }
}
