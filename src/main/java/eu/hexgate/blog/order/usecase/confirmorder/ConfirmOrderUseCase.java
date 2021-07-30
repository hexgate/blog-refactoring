package eu.hexgate.blog.order.usecase.confirmorder;

import eu.hexgate.blog.order.domain.CorrelatedOrderId;
import eu.hexgate.blog.order.domain.Price;
import eu.hexgate.blog.order.domain.Tax;
import eu.hexgate.blog.order.domain.TotalPriceCalculator;
import eu.hexgate.blog.order.domain.accepted.AcceptedOrder;
import eu.hexgate.blog.order.domain.accepted.AcceptedOrderRepository;
import eu.hexgate.blog.order.domain.confirmed.ConfirmedOrder;
import eu.hexgate.blog.order.domain.confirmed.ConfirmedOrderRepository;
import eu.hexgate.blog.order.domain.process.OrderProcess;
import eu.hexgate.blog.order.domain.process.OrderProcessService;
import eu.hexgate.blog.order.domain.process.OrderProcessStep;
import eu.hexgate.blog.order.domain.process.OrderStatus;
import eu.hexgate.blog.order.domain.vip.VipOrder;
import eu.hexgate.blog.order.domain.vip.VipOrderRepository;
import eu.hexgate.blog.order.dto.OrderNotFoundException;
import eu.hexgate.blog.order.dto.OrderStatusException;
import eu.hexgate.blog.order.usecase.UseCase;
import eu.hexgate.blog.product.ProductService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@Service
public class ConfirmOrderUseCase implements UseCase<ConfirmOrderCommand> {

    private final OrderProcessService orderProcessService;
    private final AcceptedOrderRepository acceptedOrderRepository;
    private final VipOrderRepository vipOrderRepository;
    private final ConfirmedOrderRepository confirmedOrderRepository;
    private final TotalPriceCalculator totalPriceCalculator;

    public ConfirmOrderUseCase(OrderProcessService orderProcessService, AcceptedOrderRepository acceptedOrderRepository, VipOrderRepository vipOrderRepository, ConfirmedOrderRepository confirmedOrderRepository, TotalPriceCalculator totalPriceCalculator) {
        this.orderProcessService = orderProcessService;
        this.acceptedOrderRepository = acceptedOrderRepository;
        this.vipOrderRepository = vipOrderRepository;
        this.confirmedOrderRepository = confirmedOrderRepository;
        this.totalPriceCalculator = totalPriceCalculator;
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

        final ConfirmedOrder confirmedOrder = acceptedOrder.confirm(totalPriceCalculator);
        return confirmedOrderRepository.save(confirmedOrder);
    }

    private OrderProcessStep confirmVip(OrderProcess orderProcess) {
        final VipOrder vipOrder = vipOrderRepository.findById(orderProcess.getStepId())
                .orElseThrow(() -> new OrderNotFoundException(orderProcess.getCorrelatedOrderId()));

        final ConfirmedOrder confirmedOrder = vipOrder.confirm(totalPriceCalculator);
        return confirmedOrderRepository.save(confirmedOrder);
    }

    private OrderProcessStep error(CorrelatedOrderId orderId, OrderStatus orderStatus) {
        throw new OrderStatusException(orderId, "Your order is neither accepted nor vip.", orderStatus);
    }
}
