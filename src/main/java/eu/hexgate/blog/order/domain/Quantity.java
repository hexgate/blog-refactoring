package eu.hexgate.blog.order.domain;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Embeddable;
import java.math.BigDecimal;

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
}
