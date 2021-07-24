package eu.hexgate.blog.uglyorder.dto;

import java.math.BigDecimal;

public class OrderPositionDto {

    private ProductDto productDto;
    private int quantity;
    private BigDecimal total;

    public OrderPositionDto(ProductDto productDto, int quantity) {
        this.productDto = productDto;
        this.quantity = quantity;
        this.total = productDto.getPrice().multiply(new BigDecimal(quantity));
    }

    public ProductDto getProductDto() {
        return productDto;
    }

    public int getQuantity() {
        return quantity;
    }

    public BigDecimal getTotal() {
        return total;
    }
}
