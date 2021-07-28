package eu.hexgate.blog.refactoredorder.domain.process;

import eu.hexgate.blog.refactoredorder.domain.AggregateId;
import eu.hexgate.blog.uglyorder.order.OrderNotFoundException;

import java.util.Optional;

public interface OrderProcessRepository {

    OrderProcess save(OrderProcess orderProcess);

    Optional<OrderProcess> findTopByOrderByStepAscAndByCorrelatedOrderId(AggregateId id);

    default OrderProcess findByCorrelatedId(AggregateId id) {
        return findTopByOrderByStepAscAndByCorrelatedOrderId(id)
                .orElseThrow(() -> new OrderNotFoundException(id.getId()));
    }
}
