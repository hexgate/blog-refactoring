package eu.hexgate.blog.order.domain;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Embeddable;
import java.math.BigDecimal;
import java.util.Objects;

@Embeddable
@Access(AccessType.FIELD)
public class Quantity {

    private int quantity;

    private Quantity(int quantity) {
        this.quantity = quantity;
    }

    private Quantity() {
        // JPA ONLY
    }

    public static Quantity of(int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity has to be greater than 0.");
        }

        return new Quantity(quantity);
    }

    public BigDecimal asBigDecimal() {
        return new BigDecimal(quantity);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Quantity quantity1 = (Quantity) o;
        return quantity == quantity1.quantity;
    }

    @Override
    public int hashCode() {
        return Objects.hash(quantity);
    }
}
