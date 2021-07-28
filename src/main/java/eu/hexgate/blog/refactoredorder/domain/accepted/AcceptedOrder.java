package eu.hexgate.blog.refactoredorder.domain.accepted;

import eu.hexgate.blog.refactoredorder.domain.AggregateId;
import eu.hexgate.blog.refactoredorder.domain.CorrelatedOrderId;
import eu.hexgate.blog.refactoredorder.domain.MergedOrderPositions;
import eu.hexgate.blog.refactoredorder.domain.OrderStepId;
import eu.hexgate.blog.refactoredorder.domain.confirmed.ConfirmedOrder;
import eu.hexgate.blog.refactoredorder.domain.draft.DraftOrder;
import eu.hexgate.blog.refactoredorder.domain.process.OrderProcessStep;
import eu.hexgate.blog.refactoredorder.domain.process.OrderStatus;
import eu.hexgate.blog.uglyorder.order.UpdateAcceptedOrderPositionsResult;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "ACCEPTED_ORDER")
public class AcceptedOrder implements OrderProcessStep {

    @EmbeddedId
    private OrderStepId id;

    private CorrelatedOrderId correlatedOrderId;

    private AggregateId ownerId;

    private MergedOrderPositions mergedOrderPositions;

    public AcceptedOrder(CorrelatedOrderId correlatedOrderId, AggregateId ownerId, MergedOrderPositions mergedOrderPositions) {
        this.id = OrderStepId.generate();
        this.correlatedOrderId = correlatedOrderId;
        this.ownerId = ownerId;
        this.mergedOrderPositions = mergedOrderPositions;
    }

    public UpdateAcceptedOrderPositionsResult updateProductLines(MergedOrderPositions newMergedOrderPositions) {
        return newMergedOrderPositions.anyChanges(mergedOrderPositions) ?
                UpdateAcceptedOrderPositionsResult.draft(correlatedOrderId, ownerId, newMergedOrderPositions) :
                UpdateAcceptedOrderPositionsResult.accepted(correlatedOrderId, ownerId, newMergedOrderPositions);
    }

    public DraftOrder decline() {
        return new DraftOrder(correlatedOrderId, ownerId, mergedOrderPositions);
    }

    public ConfirmedOrder confirm() {
        return new ConfirmedOrder(correlatedOrderId, ownerId);
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
        return OrderStatus.ACCEPTED;
    }
}
