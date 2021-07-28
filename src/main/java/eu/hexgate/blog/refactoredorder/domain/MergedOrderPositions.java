package eu.hexgate.blog.refactoredorder.domain;

import eu.hexgate.blog.uglyorder.forms.OrderPositionForm;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Embeddable
public class MergedOrderPositions {

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "ORDER_ID")
    private Set<OrderPosition> positions;

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
                .sorted(Comparator.comparing(o -> o.getProductId().getId()))
                .collect(Collectors.toList());

        final List<OrderPosition> sortedAfter = after.stream()
                .sorted(Comparator.comparing(o -> o.getProductId().getId()))
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

    private static OrderPosition createOrderPosition(OrderPositionForm orderPositionForm) {
        return new OrderPosition(AggregateId.fromString(orderPositionForm.getProductId()), orderPositionForm.getQuantity());
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
        return !before.getProductId().equals(after.getProductId()) ||
                before.getQuantity() != after.getQuantity();
    }
}
