package eu.hexgate.blog.product;

import eu.hexgate.blog.order.AggregateId;
import eu.hexgate.blog.order.domain.confirmed.ProductPriceRegistry;
import eu.hexgate.blog.order.domain.confirmed.ProductPriceRegistryFetcher;
import eu.hexgate.blog.order.forms.ProductForm;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ProductService implements ProductPriceRegistryFetcher {

    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Override
    public ProductPriceRegistry load(Set<AggregateId> productIds) {
        final Set<String> ids = productIds.stream()
                .map(AggregateId::asString)
                .collect(Collectors.toSet());

        return ProductPriceRegistry.fromProductPriceSet(fetchProductWithPrice(ids));
    }

    public String createProduct(ProductForm productForm) {
        validateProductName(productForm.getName());

        final Product newProduct = new Product(UUID.randomUUID().toString(), productForm.getName(), productForm.getPrice());

        return productRepository.save(newProduct)
                .getId();
    }

    private Set<ProductPriceDto> fetchProductWithPrice(Set<String> ids) {
        return productRepository.findByIdIn(ids)
                .stream()
                .map(Product::toPriceDto)
                .collect(Collectors.toSet());
    }

    private void validateProductName(String name) {
        if (name.contains("fuck")) {
            throw new UnsafeTextException();
        }
    }
}
