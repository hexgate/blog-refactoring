package eu.hexgate.blog.order.domain.accepted;

import eu.hexgate.blog.order.domain.OrderStepId;
import org.springframework.data.repository.Repository;

import java.util.Optional;

public interface AcceptedOrderRepository extends Repository<AcceptedOrder, OrderStepId> {

    AcceptedOrder save(AcceptedOrder acceptedOrder);

    Optional<AcceptedOrder> findById(OrderStepId id);

}
