package eu.hexgate.blog.uglyorder.forms;

import java.util.Set;

public class OrderForm {

    private Set<OrderPositionForm> positions;

    public OrderForm(Set<OrderPositionForm> positions) {
        this.positions = positions;
    }

    public Set<OrderPositionForm> getPositions() {
        return positions;
    }
}
