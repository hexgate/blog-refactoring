package eu.hexgate.blog.refactoredorder.domain.order.draft;

import eu.hexgate.blog.refactoredorder.domain.order.OrderStepId;
import org.springframework.data.repository.Repository;

import java.util.Optional;

public interface DraftOrderRepository extends Repository<DraftOrder, OrderStepId> {

    DraftOrder save(DraftOrder draftOrder);

    Optional<DraftOrder> findById(OrderStepId id);

}
