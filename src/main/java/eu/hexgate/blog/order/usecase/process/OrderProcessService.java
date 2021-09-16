package eu.hexgate.blog.order.usecase.process;

import eu.hexgate.blog.order.domain.CorrelatedOrderId;
import eu.hexgate.blog.order.domain.errors.DomainError;
import eu.hexgate.blog.order.domain.errors.DomainErrorCode;
import io.vavr.control.Either;
import io.vavr.control.Option;
import org.springframework.stereotype.Service;

@Service
public class OrderProcessService {

    private final OrderProcessRepository orderProcessRepository;

    public OrderProcessService(OrderProcessRepository orderProcessRepository) {
        this.orderProcessRepository = orderProcessRepository;
    }

    public String save(OrderProcess orderProcess) {
        final OrderProcess saved = orderProcessRepository.save(orderProcess);
        return saved.getCorrelatedOrderId().getId();
    }

    public String createAndSave(OrderProcessStep orderProcessStep) {
        orderProcessRepository.save(OrderProcess.first(orderProcessStep));
        return orderProcessStep.getCorrelatedOrderId().getId();
    }

    public Either<DomainError, OrderProcess> findByCorrelatedId(CorrelatedOrderId orderId) {
        return Option.ofOptional(orderProcessRepository.findNewest(orderId))
                .toEither(() -> DomainError.withCode(DomainErrorCode.ORDER_NOT_FOUND)
                        .withAdditionalData(orderId.getId())
                        .build());
    }
}
