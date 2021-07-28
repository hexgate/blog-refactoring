package eu.hexgate.blog.refactoredorder.usecase.updateorderpositions;

import eu.hexgate.blog.refactoredorder.usecase.UseCase;
import eu.hexgate.blog.uglyorder.forms.OrderPositionForm;

import java.util.List;

public class UpdateOrderPositionsCommand implements UseCase.Command {

    private final String orderId;
    private final List<OrderPositionForm> positions;

    public UpdateOrderPositionsCommand(String orderId, List<OrderPositionForm> positions) {
        this.orderId = orderId;
        this.positions = positions;
    }

    public String getOrderId() {
        return orderId;
    }

    public List<OrderPositionForm> getPositions() {
        return positions;
    }
}
