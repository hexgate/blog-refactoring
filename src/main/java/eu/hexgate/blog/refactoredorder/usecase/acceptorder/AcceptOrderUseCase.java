package eu.hexgate.blog.refactoredorder.usecase.acceptorder;

import eu.hexgate.blog.refactoredorder.domain.order.CorrelatedOrderId;
import eu.hexgate.blog.refactoredorder.domain.order.accepted.AcceptedOrder;
import eu.hexgate.blog.refactoredorder.domain.order.accepted.AcceptedOrderRepository;
import eu.hexgate.blog.refactoredorder.domain.order.draft.DraftOrder;
import eu.hexgate.blog.refactoredorder.domain.order.draft.DraftOrderRepository;
import eu.hexgate.blog.refactoredorder.domain.order.process.OrderProcess;
import eu.hexgate.blog.refactoredorder.domain.order.process.OrderProcessService;
import eu.hexgate.blog.refactoredorder.domain.order.process.OrderStatus;
import eu.hexgate.blog.refactoredorder.usecase.UseCase;
import eu.hexgate.blog.uglyorder.order.OrderNotFoundException;
import eu.hexgate.blog.uglyorder.order.OrderStatusException;

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
    public String execute(AcceptOrderCommand acceptOrderCommand) {
        final CorrelatedOrderId correlatedOrderId = CorrelatedOrderId.fromString(acceptOrderCommand.getOrderId());
        final OrderProcess orderProcess = orderProcessService.findByCorrelatedId(correlatedOrderId);

        return orderProcess.routing()
                .handleDraft(() -> acceptDraft(orderProcess))
                .handleAccepted(() -> error(correlatedOrderId, OrderStatus.ACCEPTED))
                .handleVip(() -> error(correlatedOrderId, OrderStatus.VIP))
                .handleConfirmed(() -> error(correlatedOrderId, OrderStatus.CONFIRMED))
                .execute();
    }

    private String acceptDraft(OrderProcess orderProcess) {
        final DraftOrder draftOrder = draftOrderRepository.findById(orderProcess.getStepId())
                .orElseThrow(() -> new OrderNotFoundException(orderProcess.getCorrelatedOrderId()));

        final AcceptedOrder acceptedOrder = draftOrder.accept();
        final AcceptedOrder savedAcceptedOrder = acceptedOrderRepository.save(acceptedOrder);

        return orderProcessService.incrementStepAndSave(orderProcess, savedAcceptedOrder);
    }

    private String error(CorrelatedOrderId orderId, OrderStatus orderStatus) {
        throw new OrderStatusException(orderId, "Your order is not draft.", orderStatus);
    }
}
