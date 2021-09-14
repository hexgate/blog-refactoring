package eu.hexgate.blog.order.usecase.updateorderpositions;

import eu.hexgate.blog.order.domain.CorrelatedOrderId;
import eu.hexgate.blog.order.domain.MergedOrderPositions;
import eu.hexgate.blog.order.domain.accepted.AcceptedOrder;
import eu.hexgate.blog.order.domain.accepted.AcceptedOrderRepository;
import eu.hexgate.blog.order.domain.draft.DraftOrder;
import eu.hexgate.blog.order.domain.draft.DraftOrderRepository;
import eu.hexgate.blog.order.usecase.process.OrderProcess;
import eu.hexgate.blog.order.usecase.process.OrderProcessService;
import eu.hexgate.blog.order.usecase.process.OrderProcessStep;
import eu.hexgate.blog.order.usecase.process.OrderStatus;
import eu.hexgate.blog.order.domain.vip.VipOrder;
import eu.hexgate.blog.order.domain.vip.VipOrderRepository;
import eu.hexgate.blog.order.usecase.UseCase;
import eu.hexgate.blog.order.dto.OrderNotFoundException;
import eu.hexgate.blog.order.dto.OrderStatusException;
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

        final OrderProcess executed = orderProcess.routing()
                .handle(OrderStatus.DRAFT, () -> updateDraft(orderProcess, mergedOrderPositions))
                .handle(OrderStatus.ACCEPTED, () -> updateAccepted(orderProcess, mergedOrderPositions))
                .handle(OrderStatus.VIP, () -> updateVip(orderProcess, mergedOrderPositions))
                .executeOrHandleOther(orderStatus -> {
                    throw new OrderStatusException(orderId, "Your order has already been confirmed.", OrderStatus.CONFIRMED);
                });

        return orderProcessService.save(executed);
    }

    private OrderProcessStep updateDraft(OrderProcess orderProcess, MergedOrderPositions mergedOrderPositions) {
        final DraftOrder draftOrder = draftOrderRepository.findById(orderProcess.getStepId())
                .orElseThrow(() -> new OrderNotFoundException(orderProcess.getCorrelatedOrderId()));

        final DraftOrder newDraftOrder = draftOrder.updateProductLines(mergedOrderPositions);
        return draftOrderRepository.save(newDraftOrder);
    }

    private OrderProcessStep updateAccepted(OrderProcess orderProcess, MergedOrderPositions mergedOrderPositions) {
        final AcceptedOrder acceptedOrder = acceptedOrderRepository.findById(orderProcess.getStepId())
                .orElseThrow(() -> new OrderNotFoundException(orderProcess.getCorrelatedOrderId()));

        return acceptedOrder.updateProductLines(mergedOrderPositions)
                .route(
                        acceptedOrderRepository::save,
                        draftOrderRepository::save
                );
    }

    private OrderProcessStep updateVip(OrderProcess orderProcess, MergedOrderPositions mergedOrderPositions) {
        final VipOrder vipOrder = vipOrderRepository.findById(orderProcess.getStepId())
                .orElseThrow(() -> new OrderNotFoundException(orderProcess.getCorrelatedOrderId()));

        final VipOrder newVipOrder = vipOrder.updateProductLines(mergedOrderPositions);
        return vipOrderRepository.save(newVipOrder);
    }
}
