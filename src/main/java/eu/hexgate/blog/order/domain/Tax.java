package eu.hexgate.blog.order.domain;

import java.math.BigDecimal;

public class Tax {

    private final BigDecimal value;

    private Tax(BigDecimal value) {
        this.value = value;
    }

    public static Tax asDecimalValue(BigDecimal decimalValue) {
        if (decimalValue.compareTo(BigDecimal.ZERO) < 0 || decimalValue.compareTo(BigDecimal.ONE) > 0) {
            throw new IllegalArgumentException("Invalid tax value");
        }

        return new Tax(decimalValue);
    }

    public BigDecimal asBigDecimal() {
        return value;
    }
}
