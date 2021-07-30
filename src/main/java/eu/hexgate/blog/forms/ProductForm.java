package eu.hexgate.blog.forms;

import java.math.BigDecimal;

public class ProductForm {

    private String name;

    private BigDecimal price;

    public ProductForm(String name, BigDecimal price) {
        this.name = name;
        this.price = price;
    }

    public ProductForm() {
    }

    public String getName() {
        return name;
    }

    public BigDecimal getPrice() {
        return price;
    }
}
