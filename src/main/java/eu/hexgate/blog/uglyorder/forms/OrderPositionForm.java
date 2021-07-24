package eu.hexgate.blog.uglyorder.forms;

public class OrderPositionForm {

    private String productId;

    private int quantity;

    public OrderPositionForm(String productId, int quantity) {
        this.productId = productId;
        this.quantity = quantity;
    }

    public String getProductId() {
        return productId;
    }

    public int getQuantity() {
        return quantity;
    }
}
