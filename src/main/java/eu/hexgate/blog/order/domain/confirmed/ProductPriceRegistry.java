package eu.hexgate.blog.order.domain.confirmed;

import eu.hexgate.blog.order.AggregateId;
import eu.hexgate.blog.order.domain.Price;
import eu.hexgate.blog.product.ProductPriceDto;

import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class ProductPriceRegistry {

    private final Map<AggregateId, Price> priceMap;

    private ProductPriceRegistry(Map<AggregateId, Price> priceMap) {
        this.priceMap = priceMap;
    }

    public static ProductPriceRegistry fromProductPriceSet(Set<ProductPriceDto> set) {
        final Map<AggregateId, Price> map = set.stream()
                .collect(Collectors.toMap(it -> AggregateId.fromString(it.getId()), it -> Price.of(it.getPrice())));

        return new ProductPriceRegistry(map);
    }

    public Optional<Price> getPriceByProductId(AggregateId productId) {
        return Optional.ofNullable(priceMap.get(productId));
    }
}
