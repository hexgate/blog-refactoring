package eu.hexgate.blog.refactoredorder.domain.draft;

import eu.hexgate.blog.refactoredorder.domain.OrderStepId;
import org.springframework.data.repository.Repository;

import java.util.Optional;

public interface DraftOrderRepository extends Repository<DraftOrder, OrderStepId> {

    DraftOrder save(DraftOrder draftOrder);

    Optional<DraftOrder> findById(OrderStepId id);

}
