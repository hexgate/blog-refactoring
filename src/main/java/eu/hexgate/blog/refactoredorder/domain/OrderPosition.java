package eu.hexgate.blog.refactoredorder.domain;

import javax.persistence.Embeddable;
import java.util.Objects;

@Embeddable
public class OrderPosition {

    private AggregateId productId;

    private int quantity;

    public OrderPosition(AggregateId productId, int quantity) {
        this.productId = productId;
        this.quantity = quantity;
    }

    private OrderPosition() {
        // jpa only
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OrderPosition that = (OrderPosition) o;
        return quantity == that.quantity && Objects.equals(productId, that.productId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(productId, quantity);
    }

    public AggregateId getProductId() {
        return productId;
    }

    public int getQuantity() {
        return quantity;
    }
}