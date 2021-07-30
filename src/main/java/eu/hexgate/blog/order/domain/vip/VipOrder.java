package eu.hexgate.blog.order.domain.vip;

import eu.hexgate.blog.order.ExternalAggregateId;
import eu.hexgate.blog.order.domain.CorrelatedOrderId;
import eu.hexgate.blog.order.domain.MergedOrderPositions;
import eu.hexgate.blog.order.domain.OrderStepId;
import eu.hexgate.blog.order.domain.TotalPriceCalculator;
import eu.hexgate.blog.order.domain.confirmed.ConfirmedOrder;
import eu.hexgate.blog.order.domain.process.OrderProcessStep;
import eu.hexgate.blog.order.domain.process.OrderStatus;

import javax.persistence.*;

@Entity
@Table(name = "VIP_ORDER")
public class VipOrder implements OrderProcessStep {

    @EmbeddedId
    private OrderStepId id;

    @Embedded
    private CorrelatedOrderId correlatedOrderId;

    @Embedded
    @AttributeOverride(name = "id", column = @Column(name = "OWNER_ID"))
    private ExternalAggregateId ownerId;

    @Embedded
    private MergedOrderPositions mergedOrderPositions;

    public VipOrder(CorrelatedOrderId correlatedOrderId, ExternalAggregateId ownerId, MergedOrderPositions mergedOrderPositions) {
        this.id = OrderStepId.generate();
        this.correlatedOrderId = correlatedOrderId;
        this.ownerId = ownerId;
        this.mergedOrderPositions = mergedOrderPositions;
    }

    private VipOrder() {
        // JPA ONLY
    }

    public VipOrder updateProductLines(MergedOrderPositions newMergedOrderPositions) {
        return new VipOrder(correlatedOrderId, ownerId, newMergedOrderPositions);
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
        return OrderStatus.VIP;
    }
}
