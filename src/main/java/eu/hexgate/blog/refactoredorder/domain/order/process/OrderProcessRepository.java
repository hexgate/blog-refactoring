package eu.hexgate.blog.refactoredorder.domain.order.process;

import eu.hexgate.blog.refactoredorder.domain.order.CorrelatedOrderId;

import java.util.Optional;

public interface OrderProcessRepository {

    OrderProcess save(OrderProcess orderProcess);

    Optional<OrderProcess> findTopByOrderByStepAscAndByCorrelatedOrderId(CorrelatedOrderId id);
    
}
