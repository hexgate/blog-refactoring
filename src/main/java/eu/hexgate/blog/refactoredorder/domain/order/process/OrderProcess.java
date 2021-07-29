package eu.hexgate.blog.refactoredorder.domain.order.process;

import eu.hexgate.blog.refactoredorder.domain.AggregateId;
import eu.hexgate.blog.refactoredorder.domain.order.CorrelatedOrderId;
import eu.hexgate.blog.refactoredorder.domain.order.OrderStepId;
import eu.hexgate.blog.uglyorder.order.OrderStatusException;

import javax.persistence.*;
import java.util.Optional;
import java.util.function.Supplier;

import static java.util.Objects.requireNonNull;

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

    public Routing routing() {
        return new Routing();
    }

    public OrderStepId getStepId() {
        return stepId;
    }

    public CorrelatedOrderId getCorrelatedOrderId() {
        return correlatedOrderId;
    }

    public class Routing {
        private Supplier<String> handleDraft;
        private Supplier<String> handleAccepted;
        private Supplier<String> handleVip;
        private Supplier<String> handleConfirmed;

        private Routing() {
        }

        public Routing handleDraft(Supplier<String> handleDraft) {
            this.handleDraft = handleDraft;
            return this;
        }

        public Routing handleAccepted(Supplier<String> handleAccepted) {
            this.handleAccepted = handleAccepted;
            return this;
        }

        public Routing handleVip(Supplier<String> handleVip) {
            this.handleVip = handleVip;
            return this;
        }

        public Routing handleConfirmed(Supplier<String> handleConfirmed) {
            this.handleConfirmed = handleConfirmed;
            return this;
        }

        public String execute() {
            switch (status) {
                case DRAFT:
                    return tryExecuteSupplier(handleDraft);
                case ACCEPTED:
                    return tryExecuteSupplier(handleAccepted);
                case VIP:
                    return tryExecuteSupplier(handleVip);
                case CONFIRMED:
                    return tryExecuteSupplier(handleConfirmed);
            }

            throw new IllegalStateException("Invalid state");
        }

        private String tryExecuteSupplier(Supplier<String> supplier) {
            return Optional.ofNullable(supplier)
                    .map(Supplier::get)
                    .orElseThrow(() -> new OrderStatusException(correlatedOrderId, "Your order has invalid status", status));
        }
    }
}
