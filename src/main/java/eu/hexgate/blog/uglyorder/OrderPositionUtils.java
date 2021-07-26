package eu.hexgate.blog.uglyorder;

import eu.hexgate.blog.uglyorder.forms.OrderPositionForm;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class OrderPositionUtils {

    public static Set<OrderPositionForm> merge(Set<OrderPositionForm> positions) {
        return positions.stream()
                .collect(Collectors.groupingBy(OrderPositionForm::getProductId))
                .values()
                .stream()
                .map(OrderPositionUtils::reduce)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toSet());
    }

    private static Optional<OrderPositionForm> reduce(List<OrderPositionForm> positions) {
        return positions.stream()
                .reduce((orderPositionForm, orderPositionForm2) ->
                        new OrderPositionForm(orderPositionForm.getProductId(), orderPositionForm.getQuantity() + orderPositionForm2.getQuantity()));
    }
}
