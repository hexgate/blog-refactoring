package eu.hexgate.blog.order.domain.accepted;

import eu.hexgate.blog.order.AggregateId;
import eu.hexgate.blog.order.domain.CorrelatedOrderId;
import eu.hexgate.blog.order.domain.MergedOrderPositions;
import eu.hexgate.blog.order.domain.OrderStepId;
import eu.hexgate.blog.order.domain.TotalPriceCalculator;
import eu.hexgate.blog.order.domain.confirmed.ConfirmedOrder;
import eu.hexgate.blog.order.domain.draft.DraftOrder;
import eu.hexgate.blog.order.domain.process.OrderProcessStep;
import eu.hexgate.blog.order.domain.process.OrderStatus;

import javax.persistence.*;

@Entity
@Table(name = "ACCEPTED_ORDER")
public class AcceptedOrder implements OrderProcessStep {

    @EmbeddedId
    private OrderStepId id;

    @Embedded
    private CorrelatedOrderId correlatedOrderId;

    @Embedded
    @AttributeOverride(name = "id", column = @Column(name = "OWNER_ID"))
    private AggregateId ownerId;

    @Embedded
    private MergedOrderPositions mergedOrderPositions;

    public AcceptedOrder(CorrelatedOrderId correlatedOrderId, AggregateId ownerId, MergedOrderPositions mergedOrderPositions) {
        this.id = OrderStepId.generate();
        this.correlatedOrderId = correlatedOrderId;
        this.ownerId = ownerId;
        this.mergedOrderPositions = mergedOrderPositions;
    }

    private AcceptedOrder() {
        // JPA ONLY
    }

    public UpdateAcceptedOrderPositionsResult updateProductLines(MergedOrderPositions newMergedOrderPositions) {
        return newMergedOrderPositions.anyChanges(mergedOrderPositions) ?
                UpdateAcceptedOrderPositionsResult.draft(correlatedOrderId, ownerId, newMergedOrderPositions) :
                UpdateAcceptedOrderPositionsResult.accepted(correlatedOrderId, ownerId, newMergedOrderPositions);
    }

    public DraftOrder decline() {
        return new DraftOrder(correlatedOrderId, ownerId, mergedOrderPositions);
    }

    public ConfirmedOrder confirm(TotalPriceCalculator totalPriceCalculator) {
        return new ConfirmedOrder(correlatedOrderId, ownerId, totalPriceCalculator.calculate(mergedOrderPositions));
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
