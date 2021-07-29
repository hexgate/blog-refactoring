package eu.hexgate.blog.refactoredorder.domain.order.accepted;

import eu.hexgate.blog.refactoredorder.domain.order.OrderStepId;
import org.springframework.data.repository.Repository;

import java.util.Optional;

public interface AcceptedOrderRepository extends Repository<AcceptedOrder, OrderStepId> {

    AcceptedOrder save(AcceptedOrder acceptedOrder);

    Optional<AcceptedOrder> findById(OrderStepId id);

}
