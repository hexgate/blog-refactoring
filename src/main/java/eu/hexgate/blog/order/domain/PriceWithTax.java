package eu.hexgate.blog.order.domain;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.math.BigDecimal;

@Embeddable
public class PriceWithTax {

    @Column(name = "CONFIRMED_TOTAL_PRICE")
    private BigDecimal value;

    private PriceWithTax(BigDecimal value) {
        this.value = value;
    }

    private PriceWithTax() {
        // JPA ONLY
    }

    public static PriceWithTax of(Price price, Tax tax) {
        return new PriceWithTax(price.asBigDecimal().multiply(tax.asFactor()));
    }

    public PriceWithTax add(Price price) {
        return new PriceWithTax(value.add(price.asBigDecimal()));
    }

    public BigDecimal asBigDecimal() {
        return value;
    }
}
