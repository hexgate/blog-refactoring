package eu.hexgate.blog.refactoredorder.usecase.confirmorder;

import eu.hexgate.blog.refactoredorder.domain.Price;
import eu.hexgate.blog.refactoredorder.domain.Tax;
import eu.hexgate.blog.refactoredorder.domain.order.CorrelatedOrderId;
import eu.hexgate.blog.refactoredorder.domain.order.accepted.AcceptedOrder;
import eu.hexgate.blog.refactoredorder.domain.order.accepted.AcceptedOrderRepository;
import eu.hexgate.blog.refactoredorder.domain.order.confirmed.ConfirmedOrder;
import eu.hexgate.blog.refactoredorder.domain.order.confirmed.ConfirmedOrderRepository;
import eu.hexgate.blog.refactoredorder.domain.order.process.OrderProcess;
import eu.hexgate.blog.refactoredorder.domain.order.process.OrderProcessService;
import eu.hexgate.blog.refactoredorder.domain.order.process.OrderProcessStep;
import eu.hexgate.blog.refactoredorder.domain.order.process.OrderStatus;
import eu.hexgate.blog.refactoredorder.domain.order.vip.VipOrder;
import eu.hexgate.blog.refactoredorder.domain.order.vip.VipOrderRepository;
import eu.hexgate.blog.refactoredorder.usecase.UseCase;
import eu.hexgate.blog.uglyorder.order.OrderNotFoundException;
import eu.hexgate.blog.uglyorder.order.OrderStatusException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Transactional
@Service
public class ConfirmOrderUseCase implements UseCase<ConfirmOrderCommand> {

    private final OrderProcessService orderProcessService;
    private final AcceptedOrderRepository acceptedOrderRepository;
    private final VipOrderRepository vipOrderRepository;
    private final ConfirmedOrderRepository confirmedOrderRepository;

    public ConfirmOrderUseCase(OrderProcessService orderProcessService, AcceptedOrderRepository acceptedOrderRepository, VipOrderRepository vipOrderRepository, ConfirmedOrderRepository confirmedOrderRepository) {
        this.orderProcessService = orderProcessService;
        this.acceptedOrderRepository = acceptedOrderRepository;
        this.vipOrderRepository = vipOrderRepository;
        this.confirmedOrderRepository = confirmedOrderRepository;
    }

    @Override
    public String execute(ConfirmOrderCommand confirmOrderCommand) {
        final CorrelatedOrderId correlatedOrderId = CorrelatedOrderId.fromString(confirmOrderCommand.getOrderId());
        final OrderProcess orderProcess = orderProcessService.findByCorrelatedId(correlatedOrderId);

        final OrderProcess executed = orderProcess.routing()
                .handleAccepted(() -> confirmAccepted(orderProcess))
                .handleVip(() -> confirmVip(orderProcess))
                .handleDraft(() -> error(correlatedOrderId, OrderStatus.DRAFT))
                .handleConfirmed(() -> error(correlatedOrderId, OrderStatus.CONFIRMED))
                .execute();

        return orderProcessService.save(executed);
    }

    private OrderProcessStep confirmAccepted(OrderProcess orderProcess) {
        final AcceptedOrder acceptedOrder = acceptedOrderRepository.findById(orderProcess.getStepId())
                .orElseThrow(() -> new OrderNotFoundException(orderProcess.getCorrelatedOrderId()));

        final ConfirmedOrder confirmedOrder = acceptedOrder.confirm(Price.of(BigDecimal.ONE), Tax.asDecimalValue(new BigDecimal("0.5"))); // TODO
        return confirmedOrderRepository.save(confirmedOrder);
    }

    private OrderProcessStep confirmVip(OrderProcess orderProcess) {
        final VipOrder vipOrder = vipOrderRepository.findById(orderProcess.getStepId())
                .orElseThrow(() -> new OrderNotFoundException(orderProcess.getCorrelatedOrderId()));

        final ConfirmedOrder confirmedOrder = vipOrder.confirm(Price.of(BigDecimal.ONE), Tax.asDecimalValue(new BigDecimal("0.5"))); // TODO
        return confirmedOrderRepository.save(confirmedOrder);
    }

    private OrderProcessStep error(CorrelatedOrderId orderId, OrderStatus orderStatus) {
        throw new OrderStatusException(orderId, "Your order is neither accepted nor vip.", orderStatus);
    }
}
