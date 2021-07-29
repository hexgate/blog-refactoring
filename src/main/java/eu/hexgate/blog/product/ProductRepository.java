package eu.hexgate.blog.product;


import org.springframework.data.repository.Repository;

import java.util.Set;

public interface ProductRepository extends Repository<Product, String> {

    Product save(Product newProduct);

    Set<Product> findByIdIn(Set<String> ids);

}
