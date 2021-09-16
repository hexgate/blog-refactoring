package eu.hexgate.blog.order.usecase.acceptorder;

import eu.hexgate.blog.order.domain.CorrelatedOrderId;
import eu.hexgate.blog.order.domain.accepted.AcceptedOrder;
import eu.hexgate.blog.order.domain.accepted.AcceptedOrderRepository;
import eu.hexgate.blog.order.domain.draft.DraftOrder;
import eu.hexgate.blog.order.domain.draft.DraftOrderRepository;
import eu.hexgate.blog.order.domain.errors.DomainError;
import eu.hexgate.blog.order.domain.errors.DomainErrorCode;
import eu.hexgate.blog.order.usecase.UseCase;
import eu.hexgate.blog.order.usecase.process.OrderProcess;
import eu.hexgate.blog.order.usecase.process.OrderProcessService;
import eu.hexgate.blog.order.usecase.process.OrderProcessStep;
import eu.hexgate.blog.order.usecase.process.OrderStatus;
import io.vavr.control.Either;
import io.vavr.control.Option;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@Service
public class AcceptOrderUseCase implements UseCase<AcceptOrderCommand> {

    private final OrderProcessService orderProcessService;
    private final DraftOrderRepository draftOrderRepository;
    private final AcceptedOrderRepository acceptedOrderRepository;

    public AcceptOrderUseCase(OrderProcessService orderProcessService, DraftOrderRepository draftOrderRepository, AcceptedOrderRepository acceptedOrderRepository) {
        this.orderProcessService = orderProcessService;
        this.draftOrderRepository = draftOrderRepository;
        this.acceptedOrderRepository = acceptedOrderRepository;
    }

    @Override
    public Either<DomainError, String> execute(AcceptOrderCommand acceptOrderCommand) {
        final CorrelatedOrderId correlatedOrderId = CorrelatedOrderId.fromString(acceptOrderCommand.getOrderId());
        return orderProcessService.findByCorrelatedId(correlatedOrderId)
                .flatMap(orderProcess -> orderProcess.routing()
                        .handle(OrderStatus.DRAFT, () -> acceptDraft(orderProcess))
                        .executeOrError(DomainError
                                .withCode(DomainErrorCode.INVALID_ORDER_STATUS)
                                .withMessage("Your order is not draft.")
                                .withAdditionalData(correlatedOrderId.getId())
                                .build()
                        )
                        .map(orderProcessService::save));
    }

    private Either<DomainError, OrderProcessStep> acceptDraft(OrderProcess orderProcess) {
        return Option.ofOptional(draftOrderRepository.findById(orderProcess.getStepId()))
                .toEither(
                        DomainError.orderNotFound(orderProcess.getCorrelatedOrderId().getId())
                )
                .map(DraftOrder::accept)
                .map(acceptedOrderRepository::save);
    }
}
