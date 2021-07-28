package eu.hexgate.blog.refactoredorder.domain.draft;

import eu.hexgate.blog.refactoredorder.domain.AggregateId;
import eu.hexgate.blog.refactoredorder.domain.CorrelatedOrderId;
import eu.hexgate.blog.refactoredorder.domain.MergedOrderPositions;
import eu.hexgate.blog.refactoredorder.domain.OrderStepId;
import eu.hexgate.blog.refactoredorder.domain.accepted.AcceptedOrder;
import eu.hexgate.blog.refactoredorder.domain.process.OrderProcessStep;
import eu.hexgate.blog.refactoredorder.domain.process.OrderStatus;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "DRAFT_ORDER")
public class DraftOrder implements OrderProcessStep {

    @EmbeddedId
    private OrderStepId id;

    private CorrelatedOrderId correlatedOrderId;

    private AggregateId ownerId;

    private MergedOrderPositions mergedOrderPositions;

    public DraftOrder(CorrelatedOrderId correlatedOrderId, AggregateId ownerId, MergedOrderPositions mergedOrderPositions) {
        this.id = OrderStepId.generate();
        this.correlatedOrderId = correlatedOrderId;
        this.ownerId = ownerId;
        this.mergedOrderPositions = mergedOrderPositions;
    }

    public DraftOrder updateProductLines(MergedOrderPositions newMergedOrderPositions) {
        return new DraftOrder(correlatedOrderId, ownerId, newMergedOrderPositions);
    }

    public AcceptedOrder accept() {
        return new AcceptedOrder(correlatedOrderId, ownerId, mergedOrderPositions);
    }

    public CorrelatedOrderId getCorrelatedOrderId() {
        return correlatedOrderId;
    }

    @Override
    public OrderStepId getStepId() {
        return id;
    }

    @Override
    public OrderStatus getStatus() {
        return OrderStatus.DRAFT;
    }
}
