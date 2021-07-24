package eu.hexgate.blog.uglyorder.product;

import eu.hexgate.blog.uglyorder.dto.ProductDto;

import javax.persistence.Entity;
import javax.persistence.Table;
import java.math.BigDecimal;

@Entity
@Table(name = "PRODUCT")
public class Product {

    private String id;

    private String name;

    private BigDecimal price;

    public Product(String id, String name, BigDecimal price) {
        this.id = id;
        this.name = name;
        this.price = price;
    }

    public String getId() {
        return id;
    }

    public ProductDto dto() {
        return new ProductDto(id, name, price);
    }
}
