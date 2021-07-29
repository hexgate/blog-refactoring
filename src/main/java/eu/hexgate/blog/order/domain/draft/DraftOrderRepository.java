package eu.hexgate.blog.order.domain.draft;

import eu.hexgate.blog.order.domain.OrderStepId;
import org.springframework.data.repository.Repository;

import java.util.Optional;

public interface DraftOrderRepository extends Repository<DraftOrder, OrderStepId> {

    DraftOrder save(DraftOrder draftOrder);

    Optional<DraftOrder> findById(OrderStepId id);

}
