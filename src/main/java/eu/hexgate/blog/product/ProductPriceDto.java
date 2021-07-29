package eu.hexgate.blog.product;

import java.math.BigDecimal;

public class ProductPriceDto {
    private final String id;
    private final BigDecimal price;

    public ProductPriceDto(String id, BigDecimal price) {
        this.id = id;
        this.price = price;
    }

    public String getId() {
        return id;
    }

    public BigDecimal getPrice() {
        return price;
    }
}
