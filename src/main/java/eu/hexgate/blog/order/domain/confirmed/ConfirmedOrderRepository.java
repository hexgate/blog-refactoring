package eu.hexgate.blog.order.domain.confirmed;

import eu.hexgate.blog.order.domain.OrderStepId;
import org.springframework.data.repository.Repository;

import java.util.Optional;

public interface ConfirmedOrderRepository extends Repository<ConfirmedOrder, OrderStepId> {

    ConfirmedOrder save(ConfirmedOrder confirmedOrder);

    Optional<ConfirmedOrder> findById(OrderStepId id);

}
