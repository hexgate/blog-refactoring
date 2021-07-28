package eu.hexgate.blog.refactoredorder.domain.process;

import eu.hexgate.blog.refactoredorder.domain.AggregateId;
import eu.hexgate.blog.refactoredorder.domain.CorrelatedOrderId;
import eu.hexgate.blog.refactoredorder.domain.OrderStepId;

import javax.persistence.*;
import java.util.function.Supplier;

@Entity
@Table(name = "ORDER_PROCESS")
public class OrderProcess {

    @Id
    private AggregateId id;

    private OrderStepId stepId;

    private CorrelatedOrderId correlatedOrderId;

    @Enumerated(value = EnumType.STRING)
    private OrderStatus status;

    private int step;

    private OrderProcess(AggregateId id, OrderStepId stepId, CorrelatedOrderId correlatedOrderId, OrderStatus status, int step) {
        this.id = id;
        this.stepId = stepId;
        this.correlatedOrderId = correlatedOrderId;
        this.status = status;
        this.step = step;
    }

    public static OrderProcess first(OrderProcessStep orderProcessStep) {
        return new OrderProcess(AggregateId.generate(), orderProcessStep.getStepId(), orderProcessStep.getCorrelatedOrderId(), orderProcessStep.getStatus(), 0);
    }

    public OrderProcess next(OrderStatus status, OrderStepId stepId) {
        return new OrderProcess(AggregateId.generate(), stepId, correlatedOrderId, status, step + 1);
    }

    public String route(
            Supplier<String> handleDraft,
            Supplier<String> handleAccepted,
            Supplier<String> handleVip,
            Supplier<String> handleConfirmed
    ) {
        switch (status) {
            case DRAFT:
                return handleDraft.get();
            case ACCEPTED:
                return handleAccepted.get();
            case VIP:
                return handleVip.get();
            case CONFIRMED:
                return handleConfirmed.get();
        }

        throw new IllegalStateException("Invalid state");
    }

    public OrderStepId getStepId() {
        return stepId;
    }

}
