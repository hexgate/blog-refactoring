package eu.hexgate.blog.order.usecase.confirmorder;

import eu.hexgate.blog.order.domain.CorrelatedOrderId;
import eu.hexgate.blog.order.domain.TotalPriceCalculator;
import eu.hexgate.blog.order.domain.accepted.AcceptedOrderRepository;
import eu.hexgate.blog.order.domain.confirmed.ConfirmedOrderRepository;
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
    public Either<DomainError, String> execute(ConfirmOrderCommand confirmOrderCommand) {
        final CorrelatedOrderId correlatedOrderId = CorrelatedOrderId.fromString(confirmOrderCommand.getOrderId());

        return orderProcessService.findByCorrelatedId(correlatedOrderId)
                .flatMap(orderProcess -> orderProcess.routing()
                        .handle(OrderStatus.ACCEPTED, () -> confirmAccepted(orderProcess))
                        .handle(OrderStatus.VIP, () -> confirmVip(orderProcess))
                        .executeOrError(DomainError
                                .withCode(DomainErrorCode.INVALID_ORDER_STATUS)
                                .withMessage("Your order is neither accepted nor vip.")
                                .withAdditionalData(correlatedOrderId.getId())
                                .build()
                        )
                        .map(orderProcessService::save));
    }

    private Either<DomainError, OrderProcessStep> confirmAccepted(OrderProcess orderProcess) {
        return Option.ofOptional(acceptedOrderRepository.findById(orderProcess.getStepId()))
                .toEither(
                        DomainError.orderNotFound(orderProcess.getCorrelatedOrderId().getId())
                )
                .map(acceptedOrder -> acceptedOrder.confirm(totalPriceCalculator))
                .map(confirmedOrderRepository::save);
    }

    private Either<DomainError, OrderProcessStep> confirmVip(OrderProcess orderProcess) {
        return Option.ofOptional(vipOrderRepository.findById(orderProcess.getStepId()))
                .toEither(
                        DomainError.orderNotFound(orderProcess.getCorrelatedOrderId().getId())
                )
                .map(vipOrder -> vipOrder.confirm(totalPriceCalculator))
                .map(confirmedOrderRepository::save);
    }
}
