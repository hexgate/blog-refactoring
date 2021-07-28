package eu.hexgate.blog.refactoredorder.usecase.createorder;

import eu.hexgate.blog.refactoredorder.usecase.UseCase;
import eu.hexgate.blog.uglyorder.forms.OrderPositionForm;

import java.util.List;

public class CreateOrderCommand implements UseCase.Command {

    private final String userId;
    private final List<OrderPositionForm> positions;

    public CreateOrderCommand(String userId, List<OrderPositionForm> positions) {
        this.userId = userId;
        this.positions = positions;
    }

    public String getUserId() {
        return userId;
    }

    public List<OrderPositionForm> getPositions() {
        return positions;
    }
}
