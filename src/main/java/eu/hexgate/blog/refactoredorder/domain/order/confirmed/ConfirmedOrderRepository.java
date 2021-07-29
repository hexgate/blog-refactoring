package eu.hexgate.blog.refactoredorder.domain.order.confirmed;

import eu.hexgate.blog.refactoredorder.domain.order.OrderStepId;
import org.springframework.data.repository.Repository;

import java.util.Optional;

public interface ConfirmedOrderRepository extends Repository<ConfirmedOrder, OrderStepId> {

    ConfirmedOrder save(ConfirmedOrder confirmedOrder);

    Optional<ConfirmedOrder> findById(OrderStepId id);

}
