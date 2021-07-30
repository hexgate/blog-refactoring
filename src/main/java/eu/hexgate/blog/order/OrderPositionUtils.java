package eu.hexgate.blog.order;

import eu.hexgate.blog.dto.OrderPositionDto;
import eu.hexgate.blog.forms.OrderPositionForm;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class OrderPositionUtils {

    public static Set<OrderPositionForm> merge(List<OrderPositionForm> positions) {
        return positions.stream()
                .collect(Collectors.groupingBy(OrderPositionForm::getProductId))
                .values()
                .stream()
                .map(OrderPositionUtils::reduce)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toSet());
    }

    public static boolean anyChanges(Set<OrderPosition> before, Set<OrderPosition> after) {
        if (before.size() != after.size()) {
            return true;
        }

        final List<OrderPosition> sortedBefore = before.stream()
                .sorted(Comparator.comparing(o -> o.dto().getProduct().getId()))
                .collect(Collectors.toList());

        final List<OrderPosition> sortedAfter = after.stream()
                .sorted(Comparator.comparing(o -> o.dto().getProduct().getId()))
                .collect(Collectors.toList());

        for (int i = 0; i < sortedBefore.size(); ++i) {
            final OrderPositionDto positionBefore = sortedBefore.get(i).dto();
            final OrderPositionDto positionAfter = sortedAfter.get(i).dto();

            if (areDifferent(positionBefore, positionAfter)) {
                return true;
            }
        }

        return false;
    }

    private static Optional<OrderPositionForm> reduce(List<OrderPositionForm> positions) {
        return positions.stream()
                .reduce((orderPositionForm, orderPositionForm2) ->
                        new OrderPositionForm(orderPositionForm.getProductId(), orderPositionForm.getQuantity() + orderPositionForm2.getQuantity()));
    }

    private static boolean areDifferent(OrderPositionDto before, OrderPositionDto after) {
        return !before.getProduct().getId().equals(after.getProduct().getId()) ||
                before.getQuantity() != after.getQuantity();
    }
}
