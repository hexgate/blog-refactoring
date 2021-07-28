package eu.hexgate.blog.refactoredorder.domain.accepted;

import eu.hexgate.blog.refactoredorder.domain.OrderStepId;
import org.springframework.data.repository.Repository;

import java.util.Optional;

public interface AcceptedOrderRepository extends Repository<AcceptedOrder, OrderStepId> {

    AcceptedOrder save(AcceptedOrder acceptedOrder);

    Optional<AcceptedOrder> findById(OrderStepId id);

}
