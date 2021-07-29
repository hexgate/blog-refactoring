package eu.hexgate.blog.order.usecase.declineorder;

import eu.hexgate.blog.order.usecase.UseCase;

public class DeclineOrderCommand implements UseCase.Command {

    private final String orderId;

    public DeclineOrderCommand(String orderId) {
        this.orderId = orderId;
    }

    public String getOrderId() {
        return orderId;
    }
}
