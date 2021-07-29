package eu.hexgate.blog.order.forms;

import java.util.List;

public class OrderForm {

    private List<OrderPositionForm> positions;

    public OrderForm(List<OrderPositionForm> positions) {
        this.positions = positions;
    }

    public OrderForm() {
    }

    public List<OrderPositionForm> getPositions() {
        return positions;
    }
}
