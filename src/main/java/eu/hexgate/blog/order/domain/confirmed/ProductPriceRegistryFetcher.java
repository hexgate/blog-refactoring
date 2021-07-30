package eu.hexgate.blog.order.domain.confirmed;

import eu.hexgate.blog.order.ExternalAggregateId;

import java.util.Set;

public interface ProductPriceRegistryFetcher {

    ProductPriceRegistry load(Set<ExternalAggregateId> productIds);

}
