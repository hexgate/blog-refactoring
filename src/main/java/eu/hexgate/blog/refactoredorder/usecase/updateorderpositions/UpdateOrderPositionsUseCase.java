package eu.hexgate.blog.refactoredorder.usecase.updateorderpositions;

import eu.hexgate.blog.refactoredorder.domain.CorrelatedOrderId;
import eu.hexgate.blog.refactoredorder.domain.MergedOrderPositions;
import eu.hexgate.blog.refactoredorder.domain.accepted.AcceptedOrder;
import eu.hexgate.blog.refactoredorder.domain.accepted.AcceptedOrderRepository;
import eu.hexgate.blog.refactoredorder.domain.draft.DraftOrder;
import eu.hexgate.blog.refactoredorder.domain.draft.DraftOrderRepository;
import eu.hexgate.blog.refactoredorder.domain.process.OrderProcess;
import eu.hexgate.blog.refactoredorder.domain.process.OrderProcessService;
import eu.hexgate.blog.refactoredorder.domain.process.OrderStatus;
import eu.hexgate.blog.refactoredorder.domain.vip.VipOrder;
import eu.hexgate.blog.refactoredorder.domain.vip.VipOrderRepository;
import eu.hexgate.blog.refactoredorder.usecase.UseCase;
import eu.hexgate.blog.uglyorder.order.OrderNotFoundException;
import eu.hexgate.blog.uglyorder.order.OrderStatusException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@Service
public class UpdateOrderPositionsUseCase implements UseCase<UpdateOrderPositionsCommand, String> {

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

        return orderProcess.route(
                () -> updateForDraft(orderProcess, mergedOrderPositions),
                () -> updateForAccepted(orderProcess, mergedOrderPositions),
                () -> updateForVip(orderProcess, mergedOrderPositions),
                () -> {
                    throw new OrderStatusException(orderId, "Your order has already been confirmed.", OrderStatus.CONFIRMED);
                }
        );
    }

    private String updateForDraft(OrderProcess orderProcess, MergedOrderPositions mergedOrderPositions) {
        final DraftOrder draftOrder = draftOrderRepository.findById(orderProcess.getStepId())
                .orElseThrow(() -> new OrderNotFoundException(orderProcess.getCorrelatedOrderId()));

        final DraftOrder newDraftOrder = draftOrder.updateProductLines(mergedOrderPositions);
        final DraftOrder savedNewDraftOrder = draftOrderRepository.save(newDraftOrder);

        return orderProcessService.incrementStepAndSave(orderProcess, savedNewDraftOrder);
    }

    private String updateForAccepted(OrderProcess orderProcess, MergedOrderPositions mergedOrderPositions) {
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
                        });
    }

    private String updateForVip(OrderProcess orderProcess, MergedOrderPositions mergedOrderPositions) {
        final VipOrder vipOrder = vipOrderRepository.findById(orderProcess.getStepId())
                .orElseThrow(() -> new OrderNotFoundException(orderProcess.getCorrelatedOrderId()));

        final VipOrder newVipOrder = vipOrder.updateProductLines(mergedOrderPositions);
        final VipOrder savedNewVipOrder = vipOrderRepository.save(newVipOrder);

        return orderProcessService.incrementStepAndSave(orderProcess, savedNewVipOrder);
    }
}
