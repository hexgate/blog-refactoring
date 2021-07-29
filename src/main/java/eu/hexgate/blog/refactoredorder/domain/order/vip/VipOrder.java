package eu.hexgate.blog.refactoredorder.domain.order.vip;

import eu.hexgate.blog.refactoredorder.domain.AggregateId;
import eu.hexgate.blog.refactoredorder.domain.Price;
import eu.hexgate.blog.refactoredorder.domain.PriceWithTax;
import eu.hexgate.blog.refactoredorder.domain.Tax;
import eu.hexgate.blog.refactoredorder.domain.order.CorrelatedOrderId;
import eu.hexgate.blog.refactoredorder.domain.order.MergedOrderPositions;
import eu.hexgate.blog.refactoredorder.domain.order.OrderStepId;
import eu.hexgate.blog.refactoredorder.domain.order.confirmed.ConfirmedOrder;
import eu.hexgate.blog.refactoredorder.domain.order.process.OrderProcessStep;
import eu.hexgate.blog.refactoredorder.domain.order.process.OrderStatus;

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

    public ConfirmedOrder confirm(Price shippingPrice, Tax tax) {
        final PriceWithTax totalPriceWithTax = mergedOrderPositions.calculateBasePrice()
                .withTax(tax)
                .add(shippingPrice);

        return new ConfirmedOrder(correlatedOrderId, ownerId, totalPriceWithTax);
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
