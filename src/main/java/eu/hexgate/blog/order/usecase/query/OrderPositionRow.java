package eu.hexgate.blog.order.usecase.query;

public class OrderPositionRow {

    private String orderId;
    private String productId;
    private int quantity;

    public OrderPositionRow(String orderId, String productId, int quantity) {
        this.orderId = orderId;
        this.productId = productId;
        this.quantity = quantity;
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
}
