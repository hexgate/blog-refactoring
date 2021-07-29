package eu.hexgate.blog.refactoredorder.domain;

import javax.persistence.Embeddable;
import java.math.BigDecimal;

@Embeddable
public class PriceWithTax {

    private BigDecimal value;

    private PriceWithTax(BigDecimal value) {
        this.value = value;
    }

    public static PriceWithTax of(Price price, Tax tax) {
        return new PriceWithTax(price.asBigDecimal().multiply(tax.asBigDecimal()));
    }

    public PriceWithTax add(Price price) {
        return new PriceWithTax(value.add(price.asBigDecimal()));
    }
}
