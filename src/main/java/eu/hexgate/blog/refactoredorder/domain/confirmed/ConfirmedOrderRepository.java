package eu.hexgate.blog.refactoredorder.domain.confirmed;

import eu.hexgate.blog.refactoredorder.domain.OrderStepId;
import org.springframework.data.repository.Repository;

import java.util.Optional;

public interface ConfirmedOrderRepository extends Repository<ConfirmedOrder, OrderStepId> {

    ConfirmedOrder save(ConfirmedOrder confirmedOrder);

    Optional<ConfirmedOrder> findById(OrderStepId id);

}
