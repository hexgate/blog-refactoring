package eu.hexgate.blog.refactoredorder.domain.vip;

import eu.hexgate.blog.refactoredorder.domain.AggregateId;
import eu.hexgate.blog.refactoredorder.domain.CorrelatedOrderId;
import eu.hexgate.blog.refactoredorder.domain.MergedOrderPositions;
import eu.hexgate.blog.refactoredorder.domain.OrderStepId;
import eu.hexgate.blog.refactoredorder.domain.confirmed.ConfirmedOrder;
import eu.hexgate.blog.refactoredorder.domain.process.OrderProcessStep;
import eu.hexgate.blog.refactoredorder.domain.process.OrderStatus;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "VIP_ORDER")
public class VipOrder implements OrderProcessStep {

    @EmbeddedId
    private OrderStepId id;

    private CorrelatedOrderId correlatedOrderId;

    private AggregateId ownerId;

    private MergedOrderPositions mergedOrderPositions;

    public VipOrder(CorrelatedOrderId correlatedOrderId, AggregateId ownerId, MergedOrderPositions mergedOrderPositions) {
        this.id = OrderStepId.generate();
        this.correlatedOrderId = correlatedOrderId;
        this.ownerId = ownerId;
        this.mergedOrderPositions = mergedOrderPositions;
    }

    public VipOrder updateProductLines(MergedOrderPositions newMergedOrderPositions) {
        return new VipOrder(correlatedOrderId, ownerId, newMergedOrderPositions);
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
        return OrderStatus.VIP;
    }
}
