package eu.hexgate.blog.order.domain;

import eu.hexgate.blog.externalmodules.ShippingService;
import eu.hexgate.blog.externalmodules.TaxService;
import eu.hexgate.blog.order.domain.confirmed.ProductPriceRegistryFetcher;
import org.springframework.stereotype.Service;

@Service
public class TotalPriceCalculator {

    private final TaxService taxService;
    private final ShippingService shippingService;
    private final ProductPriceRegistryFetcher productPriceRegistryFetcher;

    public TotalPriceCalculator(TaxService taxService, ShippingService shippingService, ProductPriceRegistryFetcher productPriceRegistryFetcher) {
        this.taxService = taxService;
        this.shippingService = shippingService;
        this.productPriceRegistryFetcher = productPriceRegistryFetcher;
    }

    public PriceWithTax calculate(MergedOrderPositions mergedOrderPositions) {
        final Price shippingPrice = Price.of(shippingService.getCurrentShippingPrice());
        final Tax tax = Tax.asDecimalValue(taxService.gerCurrentTax());

        return mergedOrderPositions.calculateBasePrice(productPriceRegistryFetcher)
                .withTax(tax)
                .add(shippingPrice);
    }
}
