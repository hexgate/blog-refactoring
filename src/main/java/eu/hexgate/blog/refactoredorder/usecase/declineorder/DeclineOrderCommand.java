package eu.hexgate.blog.refactoredorder.usecase.declineorder;

import eu.hexgate.blog.refactoredorder.usecase.UseCase;

public class DeclineOrderCommand implements UseCase.Command {

    private final String orderId;

    public DeclineOrderCommand(String orderId) {
        this.orderId = orderId;
    }

    public String getOrderId() {
        return orderId;
    }
}
