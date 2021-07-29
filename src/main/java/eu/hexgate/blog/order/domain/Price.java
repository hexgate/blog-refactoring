package eu.hexgate.blog.order.domain;

import javax.persistence.Embeddable;
import java.math.BigDecimal;

@Embeddable
public class Price {

    private static final Price ZERO = Price.of(BigDecimal.ZERO);

    private BigDecimal value;

    private Price(BigDecimal value) {
        this.value = value;
    }

    private Price() {
    }

    public static Price of(BigDecimal value) {
        return new Price(value);
    }

    public static Price zero() {
        return ZERO;
    }

    public PriceWithTax withTax(Tax tax) {
        return PriceWithTax.of(this, tax);
    }

    public Price multiply(Quantity quantity) {
        return new Price(value.multiply(quantity.asBigDecimal()));
    }

    public Price add(Price other) {
        return new Price(value.add(other.value));
    }

    public BigDecimal asBigDecimal() {
        return value;
    }
}
