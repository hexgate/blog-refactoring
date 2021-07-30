package eu.hexgate.blog.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import eu.hexgate.blog.restuitls.MoneySerializer;

import java.math.BigDecimal;

public class OrderPositionDto {

    private ProductDto product;

    private int quantity;

    @JsonSerialize(using = MoneySerializer.class)
    private BigDecimal total;

    public OrderPositionDto(ProductDto product, int quantity) {
        this.product = product;
        this.quantity = quantity;
        this.total = product.getPrice().multiply(new BigDecimal(quantity));
    }

    public OrderPositionDto() {
    }

    public ProductDto getProduct() {
        return product;
    }

    public int getQuantity() {
        return quantity;
    }

    public BigDecimal getTotal() {
        return total;
    }
}
