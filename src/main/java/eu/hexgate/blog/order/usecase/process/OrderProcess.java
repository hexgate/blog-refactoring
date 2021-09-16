package eu.hexgate.blog.order.usecase.process;

import eu.hexgate.blog.order.AggregateId;
import eu.hexgate.blog.order.domain.CorrelatedOrderId;
import eu.hexgate.blog.order.domain.OrderStepId;
import eu.hexgate.blog.order.domain.errors.DomainError;
import eu.hexgate.blog.order.domain.errors.DomainErrorCode;
import io.vavr.Function0;
import io.vavr.collection.HashMap;
import io.vavr.collection.Map;
import io.vavr.control.Either;

import javax.persistence.*;


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

        private Map<OrderStatus, Function0<Either<DomainError, OrderProcessStep>>> handlersMap = HashMap.empty();

        private Routing() {
        }

        public Routing handle(OrderStatus orderStatus, Function0<Either<DomainError, OrderProcessStep>> handleDraft) {
            handlersMap = handlersMap.put(orderStatus, handleDraft);
            return this;
        }

        public Either<DomainError, OrderProcess> executeOrError(DomainError error) {
            return handlersMap.get(status)
                    .map(this::tryExecuteSupplier)
                    .getOrElse(() -> Either.left(error))
                    .map(it -> next(it.getStatus(), it.getStepId()));
        }

        private Either<DomainError, OrderProcessStep> tryExecuteSupplier(Function0<Either<DomainError, OrderProcessStep>> supplier) {
            return supplier.apply()
                    .toEither(
                            DomainError.withCode(DomainErrorCode.INVALID_ORDER_STATUS)
                                    .withMessage("Your order has invalid status")
                                    .withAdditionalData(correlatedOrderId.getId())
                                    .build()
                    );
        }
    }
}
