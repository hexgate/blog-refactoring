package eu.hexgate.blog.refactoredorder.domain.process;

import eu.hexgate.blog.refactoredorder.domain.CorrelatedOrderId;
import eu.hexgate.blog.uglyorder.order.OrderNotFoundException;

import java.util.Optional;

public interface OrderProcessRepository {

    OrderProcess save(OrderProcess orderProcess);

    Optional<OrderProcess> findTopByOrderByStepAscAndByCorrelatedOrderId(CorrelatedOrderId id);

    default OrderProcess findByCorrelatedId(CorrelatedOrderId id) {
        return findTopByOrderByStepAscAndByCorrelatedOrderId(id)
                .orElseThrow(() -> new OrderNotFoundException(id));
    }
}
