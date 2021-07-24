package eu.hexgate.blog.uglyorder.product;

public class ProductNotFoundException extends RuntimeException {

    private String id;

    public ProductNotFoundException(String id) {
        super("Product not found.");
        this.id = id;
    }
}
