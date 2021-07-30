package eu.hexgate.blog.order.domain.process;

import eu.hexgate.blog.order.ExternalAggregateId;
import eu.hexgate.blog.order.domain.CorrelatedOrderId;
import eu.hexgate.blog.order.domain.OrderStepId;
import eu.hexgate.blog.order.dto.OrderStatusException;

import javax.persistence.*;
import java.util.Optional;
import java.util.function.Supplier;

@Entity
@Table(name = "ORDER_PROCESS")
public class OrderProcess {

    @EmbeddedId
    private ExternalAggregateId id;

    @Embedded
    @AttributeOverride(name = "id", column = @Column(name = "STEP_ID"))
    private OrderStepId stepId;

    @Embedded
    private CorrelatedOrderId correlatedOrderId;

    @Enumerated(value = EnumType.STRING)
    private OrderStatus status;

    private int step;

    private OrderProcess(ExternalAggregateId id, OrderStepId stepId, CorrelatedOrderId correlatedOrderId, OrderStatus status, int step) {
        this.id = id;
        this.stepId = stepId;
        this.correlatedOrderId = correlatedOrderId;
        this.status = status;
        this.step = step;
    }

    private OrderProcess() {
        // JPA ONLY
    }

    public static OrderProcess first(OrderProcessStep orderProcessStep) {
        return new OrderProcess(ExternalAggregateId.generate(), orderProcessStep.getStepId(), orderProcessStep.getCorrelatedOrderId(), orderProcessStep.getStatus(), 0);
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

    private OrderProcess next(OrderStatus status, OrderStepId stepId) {
        return new OrderProcess(ExternalAggregateId.generate(), stepId, correlatedOrderId, status, step + 1);
    }

    public class Routing {
        private Supplier<OrderProcessStep> handleDraft;
        private Supplier<OrderProcessStep> handleAccepted;
        private Supplier<OrderProcessStep> handleVip;
        private Supplier<OrderProcessStep> handleConfirmed;

        private Routing() {
        }

        public Routing handleDraft(Supplier<OrderProcessStep> handleDraft) {
            this.handleDraft = handleDraft;
            return this;
        }

        public Routing handleAccepted(Supplier<OrderProcessStep> handleAccepted) {
            this.handleAccepted = handleAccepted;
            return this;
        }

        public Routing handleVip(Supplier<OrderProcessStep> handleVip) {
            this.handleVip = handleVip;
            return this;
        }

        public Routing handleConfirmed(Supplier<OrderProcessStep> handleConfirmed) {
            this.handleConfirmed = handleConfirmed;
            return this;
        }

        public OrderProcess execute() {

            OrderProcessStep orderProcessStep;

            switch (status) {
                case DRAFT:
                    orderProcessStep = tryExecuteSupplier(handleDraft);
                    break;
                case ACCEPTED:
                    orderProcessStep = tryExecuteSupplier(handleAccepted);
                    break;
                case VIP:
                    orderProcessStep = tryExecuteSupplier(handleVip);
                    break;
                case CONFIRMED:
                    orderProcessStep = tryExecuteSupplier(handleConfirmed);
                    break;
                default:
                    throw new IllegalStateException("Invalid state");
            }

            return next(orderProcessStep.getStatus(), orderProcessStep.getStepId());
        }

        private OrderProcessStep tryExecuteSupplier(Supplier<OrderProcessStep> supplier) {
            return Optional.ofNullable(supplier)
                    .map(Supplier::get)
                    .orElseThrow(() -> new OrderStatusException(correlatedOrderId, "Your order has invalid status", status));
        }
    }
}
