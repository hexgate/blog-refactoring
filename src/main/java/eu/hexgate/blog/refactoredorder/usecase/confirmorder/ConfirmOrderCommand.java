package eu.hexgate.blog.refactoredorder.usecase.confirmorder;

import eu.hexgate.blog.refactoredorder.usecase.UseCase;

public class ConfirmOrderCommand implements UseCase.Command {

    private final String orderId;

    public ConfirmOrderCommand(String orderId) {
        this.orderId = orderId;
    }

    public String getOrderId() {
        return orderId;
    }
}
