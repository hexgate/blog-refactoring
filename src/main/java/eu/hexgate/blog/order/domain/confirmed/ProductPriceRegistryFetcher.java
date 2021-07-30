package eu.hexgate.blog.order.domain.confirmed;

import eu.hexgate.blog.order.AggregateId;

import java.util.Set;

public interface ProductPriceRegistryFetcher {

    ProductPriceRegistry load(Set<AggregateId> productIds);

}
