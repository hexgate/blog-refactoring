package eu.hexgate.blog.order.domain;

import eu.hexgate.blog.order.ExternalAggregateId;

import javax.persistence.AttributeOverride;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.Embedded;
import java.util.Objects;

@Embeddable
public class OrderPosition {

    @Embedded
    @AttributeOverride(name = "id", column = @Column(name = "PRODUCT_ID"))
    private ExternalAggregateId productId;

    @Embedded
    private Quantity quantity;

    public OrderPosition(ExternalAggregateId productId, Quantity quantity) {
        this.productId = productId;
        this.quantity = quantity;
    }

    private OrderPosition() {
        // jpa only
    }

    public ExternalAggregateId getProductId() {
        return productId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OrderPosition that = (OrderPosition) o;
        return Objects.equals(productId, that.productId) && Objects.equals(quantity, that.quantity);
    }

    @Override
    public int hashCode() {
        return Objects.hash(productId, quantity);
    }

    public Price calculatePrice(Price unitPrice) {
        return unitPrice.multiply(quantity);
    }
}
