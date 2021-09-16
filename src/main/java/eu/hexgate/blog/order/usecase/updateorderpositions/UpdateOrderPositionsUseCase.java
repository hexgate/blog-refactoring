package eu.hexgate.blog.order.usecase.updateorderpositions;

import eu.hexgate.blog.order.domain.CorrelatedOrderId;
import eu.hexgate.blog.order.domain.MergedOrderPositions;
import eu.hexgate.blog.order.domain.accepted.AcceptedOrderRepository;
import eu.hexgate.blog.order.domain.draft.DraftOrderRepository;
import eu.hexgate.blog.order.domain.errors.DomainError;
import eu.hexgate.blog.order.domain.errors.DomainErrorCode;
import eu.hexgate.blog.order.domain.vip.VipOrderRepository;
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
public class UpdateOrderPositionsUseCase implements UseCase<UpdateOrderPositionsCommand> {

    private final OrderProcessService orderProcessService;
    private final DraftOrderRepository draftOrderRepository;
    private final AcceptedOrderRepository acceptedOrderRepository;
    private final VipOrderRepository vipOrderRepository;

    public UpdateOrderPositionsUseCase(OrderProcessService orderProcessService, DraftOrderRepository draftOrderRepository, AcceptedOrderRepository acceptedOrderRepository, VipOrderRepository vipOrderRepository) {
        this.orderProcessService = orderProcessService;
        this.draftOrderRepository = draftOrderRepository;
        this.acceptedOrderRepository = acceptedOrderRepository;
        this.vipOrderRepository = vipOrderRepository;
    }

    @Override
    public Either<DomainError, String> execute(UpdateOrderPositionsCommand updateOrderPositionsCommand) {
        final MergedOrderPositions mergedOrderPositions = MergedOrderPositions.of(updateOrderPositionsCommand.getPositions());
        final CorrelatedOrderId orderId = CorrelatedOrderId.fromString(updateOrderPositionsCommand.getOrderId());

        return orderProcessService.findByCorrelatedId(orderId)
                .flatMap(orderProcess -> orderProcess.routing()
                        .handle(OrderStatus.DRAFT, () -> updateDraft(orderProcess, mergedOrderPositions))
                        .handle(OrderStatus.ACCEPTED, () -> updateAccepted(orderProcess, mergedOrderPositions))
                        .handle(OrderStatus.VIP, () -> updateVip(orderProcess, mergedOrderPositions))
                        .executeOrError(DomainError
                                .withCode(DomainErrorCode.INVALID_ORDER_STATUS)
                                .withMessage("Your order has already been confirmed.")
                                .withAdditionalData(orderId.getId())
                                .build()
                        )
                        .map(orderProcessService::save));
    }

    private Either<DomainError, OrderProcessStep> updateDraft(OrderProcess orderProcess, MergedOrderPositions mergedOrderPositions) {
        return Option.ofOptional(draftOrderRepository.findById(orderProcess.getStepId()))
                .toEither(
                        DomainError.orderNotFound(orderProcess.getCorrelatedOrderId().getId())
                )
                .map(draftOrder -> draftOrder.updateProductLines(mergedOrderPositions))
                .map(draftOrderRepository::save);
    }

    private Either<DomainError, OrderProcessStep> updateAccepted(OrderProcess orderProcess, MergedOrderPositions mergedOrderPositions) {
        return Option.ofOptional(acceptedOrderRepository.findById(orderProcess.getStepId()))
                .toEither(
                        DomainError.orderNotFound(orderProcess.getCorrelatedOrderId().getId())
                )
                .map(acceptedOrder -> acceptedOrder.updateProductLines(mergedOrderPositions)
                        .route(
                                acceptedOrderRepository::save,
                                draftOrderRepository::save
                        ));
    }

    private Either<DomainError, OrderProcessStep> updateVip(OrderProcess orderProcess, MergedOrderPositions mergedOrderPositions) {
        return Option.ofOptional(vipOrderRepository.findById(orderProcess.getStepId()))
                .toEither(
                        DomainError.orderNotFound(orderProcess.getCorrelatedOrderId().getId())
                )
                .map(vipOrder -> vipOrder.updateProductLines(mergedOrderPositions))
                .map(vipOrderRepository::save);
    }
}
