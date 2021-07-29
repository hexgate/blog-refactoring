package eu.hexgate.blog.refactoredorder.usecase.updateorderpositions;

import eu.hexgate.blog.refactoredorder.domain.order.CorrelatedOrderId;
import eu.hexgate.blog.refactoredorder.domain.order.MergedOrderPositions;
import eu.hexgate.blog.refactoredorder.domain.order.accepted.AcceptedOrder;
import eu.hexgate.blog.refactoredorder.domain.order.accepted.AcceptedOrderRepository;
import eu.hexgate.blog.refactoredorder.domain.order.draft.DraftOrder;
import eu.hexgate.blog.refactoredorder.domain.order.draft.DraftOrderRepository;
import eu.hexgate.blog.refactoredorder.domain.order.process.OrderProcess;
import eu.hexgate.blog.refactoredorder.domain.order.process.OrderProcessService;
import eu.hexgate.blog.refactoredorder.domain.order.process.OrderStatus;
import eu.hexgate.blog.refactoredorder.domain.order.vip.VipOrder;
import eu.hexgate.blog.refactoredorder.domain.order.vip.VipOrderRepository;
import eu.hexgate.blog.refactoredorder.usecase.UseCase;
import eu.hexgate.blog.uglyorder.order.OrderNotFoundException;
import eu.hexgate.blog.uglyorder.order.OrderStatusException;
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
    public String execute(UpdateOrderPositionsCommand updateOrderPositionsCommand) {
        final MergedOrderPositions mergedOrderPositions = MergedOrderPositions.of(updateOrderPositionsCommand.getPositions());
        final CorrelatedOrderId orderId = CorrelatedOrderId.fromString(updateOrderPositionsCommand.getOrderId());
        final OrderProcess orderProcess = orderProcessService.findByCorrelatedId(orderId);

        return orderProcess.routing()
                .handleDraft(() -> updateDraft(orderProcess, mergedOrderPositions))
                .handleAccepted(() -> updateAccepted(orderProcess, mergedOrderPositions))
                .handleVip(() -> updateVip(orderProcess, mergedOrderPositions))
                .handleConfirmed(() -> {
                    throw new OrderStatusException(orderId, "Your order has already been confirmed.", OrderStatus.CONFIRMED);
                })
                .execute();
    }

    private String updateDraft(OrderProcess orderProcess, MergedOrderPositions mergedOrderPositions) {
        final DraftOrder draftOrder = draftOrderRepository.findById(orderProcess.getStepId())
                .orElseThrow(() -> new OrderNotFoundException(orderProcess.getCorrelatedOrderId()));

        final DraftOrder newDraftOrder = draftOrder.updateProductLines(mergedOrderPositions);
        final DraftOrder savedNewDraftOrder = draftOrderRepository.save(newDraftOrder);

        return orderProcessService.incrementStepAndSave(orderProcess, savedNewDraftOrder);
    }

    private String updateAccepted(OrderProcess orderProcess, MergedOrderPositions mergedOrderPositions) {
        final AcceptedOrder acceptedOrder = acceptedOrderRepository.findById(orderProcess.getStepId())
                .orElseThrow(() -> new OrderNotFoundException(orderProcess.getCorrelatedOrderId()));

        return acceptedOrder.updateProductLines(mergedOrderPositions)
                .route(
                        accepted -> {
                            final AcceptedOrder savedAccepted = acceptedOrderRepository.save(accepted);
                            return orderProcessService.incrementStepAndSave(orderProcess, savedAccepted);
                        }, draft -> {
                            final DraftOrder savedDraft = draftOrderRepository.save(draft);
                            return orderProcessService.incrementStepAndSave(orderProcess, savedDraft);
                        }
                );
    }

    private String updateVip(OrderProcess orderProcess, MergedOrderPositions mergedOrderPositions) {
        final VipOrder vipOrder = vipOrderRepository.findById(orderProcess.getStepId())
                .orElseThrow(() -> new OrderNotFoundException(orderProcess.getCorrelatedOrderId()));

        final VipOrder newVipOrder = vipOrder.updateProductLines(mergedOrderPositions);
        final VipOrder savedNewVipOrder = vipOrderRepository.save(newVipOrder);

        return orderProcessService.incrementStepAndSave(orderProcess, savedNewVipOrder);
    }
}
