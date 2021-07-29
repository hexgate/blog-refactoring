package eu.hexgate.blog.refactoredorder.domain;

import javax.persistence.Embeddable;
import java.math.BigDecimal;

@Embeddable
public class Price {

    private BigDecimal value;

    private Price(BigDecimal value) {
        this.value = value;
    }

    private Price() {
    }

    public static Price of(BigDecimal value) {
        return new Price(value);
    }

    public PriceWithTax withTax(Tax tax) {
        return PriceWithTax.of(this, tax);
    }

    public BigDecimal asBigDecimal() {
        return value;
    }
}
