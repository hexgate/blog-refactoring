package eu.hexgate.blog.product;

import eu.hexgate.blog.forms.ProductForm;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class ProductService {

    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public String createProduct(ProductForm productForm) {
        validateProductName(productForm.getName());

        final Product newProduct = new Product(UUID.randomUUID().toString(), productForm.getName(), productForm.getPrice());

        return productRepository.save(newProduct)
                .getId();
    }

    public Product getProduct(String id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException(id));
    }

    private void validateProductName(String name) {
        if (name.contains("fuck")) {
            throw new UnsafeTextException();
        }
    }
}
