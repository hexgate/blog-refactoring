package eu.hexgate.blog.uglyorder.dto;

import java.math.BigDecimal;

public class ProductDto {

    private String id;

    private String name;

    private BigDecimal price;

    public ProductDto(String id, String name, BigDecimal price) {
        this.id = id;
        this.name = name;
        this.price = price;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public BigDecimal getPrice() {
        return price;
    }
}
