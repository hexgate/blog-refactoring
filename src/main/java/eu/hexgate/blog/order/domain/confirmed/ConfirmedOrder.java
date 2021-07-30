package eu.hexgate.blog.order.domain.confirmed;

import eu.hexgate.blog.order.ExternalAggregateId;
import eu.hexgate.blog.order.domain.PriceWithTax;
import eu.hexgate.blog.order.domain.CorrelatedOrderId;
import eu.hexgate.blog.order.domain.OrderStepId;
import eu.hexgate.blog.order.domain.process.OrderProcessStep;
import eu.hexgate.blog.order.domain.process.OrderStatus;

import javax.persistence.*;

@Entity
@Table(name = "CONFIRMED_ORDER")
public class ConfirmedOrder implements OrderProcessStep {

    @EmbeddedId
    private OrderStepId id;

    @Embedded
    private CorrelatedOrderId correlatedOrderId;

    @Embedded
    @AttributeOverride(name = "id", column = @Column(name = "OWNER_ID"))
    private ExternalAggregateId ownerId;

    @Embedded
    private PriceWithTax confirmedTotalPrice; // todo refactor

    public ConfirmedOrder(CorrelatedOrderId correlatedOrderId, ExternalAggregateId ownerId, PriceWithTax confirmedTotalPrice) {
        this.id = OrderStepId.generate();
        this.correlatedOrderId = correlatedOrderId;
        this.ownerId = ownerId;
        this.confirmedTotalPrice = confirmedTotalPrice;
    }

    private ConfirmedOrder() {
        // JPA ONLY
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

