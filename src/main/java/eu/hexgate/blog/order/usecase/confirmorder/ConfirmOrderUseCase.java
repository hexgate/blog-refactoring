package eu.hexgate.blog.order.usecase.confirmorder;

import eu.hexgate.blog.externalmodules.ShippingService;
import eu.hexgate.blog.externalmodules.TaxService;
import eu.hexgate.blog.order.domain.CorrelatedOrderId;
import eu.hexgate.blog.order.domain.Price;
import eu.hexgate.blog.order.domain.Tax;
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
    private final ProductService productService;
    private final TaxService taxService;
    private final ShippingService shippingService;

    public ConfirmOrderUseCase(OrderProcessService orderProcessService, AcceptedOrderRepository acceptedOrderRepository, VipOrderRepository vipOrderRepository, ConfirmedOrderRepository confirmedOrderRepository, ProductService productService, TaxService taxService, ShippingService shippingService) {
        this.orderProcessService = orderProcessService;
        this.acceptedOrderRepository = acceptedOrderRepository;
        this.vipOrderRepository = vipOrderRepository;
        this.confirmedOrderRepository = confirmedOrderRepository;
        this.productService = productService;
        this.taxService = taxService;
        this.shippingService = shippingService;
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

        final Price shippingPrice = Price.of(shippingService.getCurrentShippingPrice());
        final Tax tax = Tax.asDecimalValue(taxService.gerCurrentTax());

        final ConfirmedOrder confirmedOrder = acceptedOrder.confirm(shippingPrice, tax, productService);
        return confirmedOrderRepository.save(confirmedOrder);
    }

    private OrderProcessStep confirmVip(OrderProcess orderProcess) {
        final VipOrder vipOrder = vipOrderRepository.findById(orderProcess.getStepId())
                .orElseThrow(() -> new OrderNotFoundException(orderProcess.getCorrelatedOrderId()));

        final Price shippingPrice = Price.of(shippingService.getCurrentShippingPrice());
        final Tax tax = Tax.asDecimalValue(taxService.gerCurrentTax());

        final ConfirmedOrder confirmedOrder = vipOrder.confirm(shippingPrice, tax, productService);
        return confirmedOrderRepository.save(confirmedOrder);
    }

    private OrderProcessStep error(CorrelatedOrderId orderId, OrderStatus orderStatus) {
        throw new OrderStatusException(orderId, "Your order is neither accepted nor vip.", orderStatus);
    }
}
