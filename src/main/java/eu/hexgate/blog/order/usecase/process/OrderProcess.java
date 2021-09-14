package eu.hexgate.blog.order.usecase.process;

import eu.hexgate.blog.order.AggregateId;
import eu.hexgate.blog.order.domain.CorrelatedOrderId;
import eu.hexgate.blog.order.domain.OrderStepId;
import eu.hexgate.blog.order.dto.OrderStatusException;

import javax.persistence.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;

@Entity
@Table(name = "ORDER_PROCESS")
public class OrderProcess {

    @EmbeddedId
    private AggregateId id;

    @Embedded
    @AttributeOverride(name = "id", column = @Column(name = "STEP_ID"))
    private OrderStepId stepId;

    @Embedded
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

    private OrderProcess() {
        // JPA ONLY
    }

    public static OrderProcess first(OrderProcessStep orderProcessStep) {
        return new OrderProcess(AggregateId.generate(), orderProcessStep.getStepId(), orderProcessStep.getCorrelatedOrderId(), orderProcessStep.getStatus(), 0);
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
        return new OrderProcess(AggregateId.generate(), stepId, correlatedOrderId, status, step + 1);
    }

    public class Routing {

        private final Map<OrderStatus, Supplier<OrderProcessStep>> handlersMap = new HashMap<>();

        private Routing() {
        }

        public Routing handle(OrderStatus orderStatus, Supplier<OrderProcessStep> handleDraft) {
            handlersMap.put(orderStatus, handleDraft);
            return this;
        }

        public OrderProcess executeOrHandleOther(Function<OrderStatus, OrderProcessStep> handleOther) {
            final OrderProcessStep orderProcessStep = Optional.ofNullable(handlersMap.get(status))
                    .map(this::tryExecuteSupplier)
                    .orElseGet(() -> handleOther.apply(status));

            return next(orderProcessStep.getStatus(), orderProcessStep.getStepId());
        }

        private OrderProcessStep tryExecuteSupplier(Supplier<OrderProcessStep> supplier) {
            return Optional.ofNullable(supplier)
                    .map(Supplier::get)
                    .orElseThrow(() -> new OrderStatusException(correlatedOrderId, "Your order has invalid status", status));
        }
    }
}
