package eu.hexgate.blog.refactoredorder.domain.order.confirmed;

import eu.hexgate.blog.refactoredorder.domain.AggregateId;
import eu.hexgate.blog.refactoredorder.domain.PriceWithTax;
import eu.hexgate.blog.refactoredorder.domain.order.CorrelatedOrderId;
import eu.hexgate.blog.refactoredorder.domain.order.OrderStepId;
import eu.hexgate.blog.refactoredorder.domain.order.process.OrderProcessStep;
import eu.hexgate.blog.refactoredorder.domain.order.process.OrderStatus;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "CONFIRMED_ORDER")
public class ConfirmedOrder implements OrderProcessStep {

    @EmbeddedId
    private OrderStepId id;

    private CorrelatedOrderId correlatedOrderId;

    private AggregateId ownerId;

    private PriceWithTax confirmedTotalPrice; // todo refactor

    public ConfirmedOrder(CorrelatedOrderId correlatedOrderId, AggregateId ownerId, PriceWithTax confirmedTotalPrice) {
        this.id = OrderStepId.generate();
        this.correlatedOrderId = correlatedOrderId;
        this.ownerId = ownerId;
        this.confirmedTotalPrice = confirmedTotalPrice;
    }

    @Override
    public CorrelatedOrderId getCorrelatedOrderId() {
        return correlatedOrderId;
    }

    @Override
    public OrderStepId getStepId() {
        return id;
    }

    @Override
    public OrderStatus getStatus() {
        return OrderStatus.CONFIRMED;
    }
}

