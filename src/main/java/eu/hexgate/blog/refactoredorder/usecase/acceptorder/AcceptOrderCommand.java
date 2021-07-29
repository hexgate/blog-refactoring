package eu.hexgate.blog.refactoredorder.usecase.acceptorder;

import eu.hexgate.blog.refactoredorder.usecase.UseCase;

public class AcceptOrderCommand implements UseCase.Command {

    private final String orderId;

    public AcceptOrderCommand(String orderId) {
        this.orderId = orderId;
    }

    public String getOrderId() {
        return orderId;
    }
}
