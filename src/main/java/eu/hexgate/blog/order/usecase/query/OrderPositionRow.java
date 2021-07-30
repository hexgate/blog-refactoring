package eu.hexgate.blog.order.usecase.query;

import java.math.BigDecimal;

public class OrderPositionRow {

    private String orderId;
    private String productId;
    private int quantity;
    private String productName;
    private BigDecimal price;

    public OrderPositionRow(String orderId, String productId, int quantity, String productName, BigDecimal price) {
        this.orderId = orderId;
        this.productId = productId;
        this.quantity = quantity;
        this.productName = productName;
        this.price = price;
    }

    public String getOrderId() {
        return orderId;
    }

    public String getProductId() {
        return productId;
    }

    public int getQuantity() {
        return quantity;
    }

    public String getProductName() {
        return productName;
    }

    public BigDecimal getPrice() {
        return price;
    }
}
