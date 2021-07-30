package eu.hexgate.blog.product;

public class ProductNotFoundException extends RuntimeException {

    private String id;

    public ProductNotFoundException(String id) {
        super("Product not found.");
        this.id = id;
    }
}
