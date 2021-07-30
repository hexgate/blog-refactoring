package eu.hexgate.blog.order.domain;

import eu.hexgate.blog.order.ExternalAggregateId;
import eu.hexgate.blog.order.domain.confirmed.ProductPriceRegistry;
import eu.hexgate.blog.order.domain.confirmed.ProductPriceRegistryFetcher;
import eu.hexgate.blog.order.forms.OrderPositionForm;

import javax.persistence.*;
import java.util.*;
import java.util.stream.Collectors;

@Embeddable
public class MergedOrderPositions {

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(
            name = "ORDER_POSITION",
            joinColumns = @JoinColumn(name = "ORDER_ID")
    )
    private Set<OrderPosition> positions = new HashSet<>();

    private MergedOrderPositions() {
        // jpa only
    }

    private MergedOrderPositions(Set<OrderPosition> positions) {
        this.positions = positions;
    }

    public static MergedOrderPositions of(List<OrderPositionForm> positions) {
        final Set<OrderPosition> orderPositions = merge(positions)
                .stream()
                .map(MergedOrderPositions::createOrderPosition)
                .collect(Collectors.toSet());

        return new MergedOrderPositions(orderPositions);
    }

    public boolean anyChanges(MergedOrderPositions mergedOrderPositions) {
        final Set<OrderPosition> before = positions;
        final Set<OrderPosition> after = mergedOrderPositions.positions;

        if (before.size() != after.size()) {
            return true;
        }

        final List<OrderPosition> sortedBefore = before.stream()
                .sorted(Comparator.comparing(OrderPosition::getProductId))
                .collect(Collectors.toList());

        final List<OrderPosition> sortedAfter = after.stream()
                .sorted(Comparator.comparing(OrderPosition::getProductId))
                .collect(Collectors.toList());

        for (int i = 0; i < sortedBefore.size(); ++i) {
            final OrderPosition positionBefore = sortedBefore.get(i);
            final OrderPosition positionAfter = sortedAfter.get(i);

            if (areDifferent(positionBefore, positionAfter)) {
                return true;
            }
        }

        return false;
    }

    public Price calculateBasePrice(ProductPriceRegistryFetcher productPriceRegistryFetcher) {
        final Set<ExternalAggregateId> ids = positions.stream()
                .map(OrderPosition::getProductId)
                .collect(Collectors.toSet());

        final ProductPriceRegistry registry = productPriceRegistryFetcher.load(ids);

        return positions.stream()
                .map(orderPosition -> orderPosition.calculatePrice(registry.getPriceByProductId(orderPosition.getProductId())
                        .orElseThrow(() -> new IllegalStateException(String.format("Price not defined for product with id %s", orderPosition.getProductId())))))
                .reduce(Price.zero(), Price::add);
    }

    private static OrderPosition createOrderPosition(OrderPositionForm orderPositionForm) {
        return new OrderPosition(ExternalAggregateId.fromString(orderPositionForm.getProductId()), Quantity.of(orderPositionForm.getQuantity()));
    }

    private static Set<OrderPositionForm> merge(List<OrderPositionForm> positions) {
        return positions.stream()
                .collect(Collectors.groupingBy(OrderPositionForm::getProductId))
                .values()
                .stream()
                .map(MergedOrderPositions::reduce)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toSet());
    }

    private static Optional<OrderPositionForm> reduce(List<OrderPositionForm> positions) {
        return positions.stream()
                .reduce((orderPositionForm, orderPositionForm2) ->
                        new OrderPositionForm(orderPositionForm.getProductId(), orderPositionForm.getQuantity() + orderPositionForm2.getQuantity()));
    }

    private static boolean areDifferent(OrderPosition before, OrderPosition after) {
        return !before.equals(after);
    }
}
