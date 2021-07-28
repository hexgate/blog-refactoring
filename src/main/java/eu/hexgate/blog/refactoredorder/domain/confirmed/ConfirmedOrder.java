package eu.hexgate.blog.refactoredorder.domain.confirmed;

import eu.hexgate.blog.refactoredorder.domain.AggregateId;
import eu.hexgate.blog.refactoredorder.domain.CorrelatedOrderId;
import eu.hexgate.blog.refactoredorder.domain.OrderStepId;
import eu.hexgate.blog.refactoredorder.domain.process.OrderProcessStep;
import eu.hexgate.blog.refactoredorder.domain.process.OrderStatus;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.math.BigDecimal;

@Entity
@Table(name = "CONFIRMED_ORDER")
public class ConfirmedOrder implements OrderProcessStep {

    @EmbeddedId
    private OrderStepId id;

    private CorrelatedOrderId correlatedOrderId;

    private AggregateId ownerId;

    private BigDecimal confirmedTotalPrice; // todo refactor

    public ConfirmedOrder(CorrelatedOrderId correlatedOrderId, AggregateId ownerId) {
        this.id = OrderStepId.generate();
        this.correlatedOrderId = correlatedOrderId;
        this.ownerId = ownerId;
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

