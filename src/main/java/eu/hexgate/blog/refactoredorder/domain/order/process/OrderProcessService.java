package eu.hexgate.blog.refactoredorder.domain.order.process;

import eu.hexgate.blog.refactoredorder.domain.order.CorrelatedOrderId;
import eu.hexgate.blog.uglyorder.order.OrderNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class OrderProcessService {

    private final OrderProcessRepository orderProcessRepository;

    public OrderProcessService(OrderProcessRepository orderProcessRepository) {
        this.orderProcessRepository = orderProcessRepository;
    }

    public String incrementStepAndSave(OrderProcess orderProcess, OrderProcessStep orderProcessStep) {
        orderProcessRepository.save(orderProcess.next(orderProcessStep.getStatus(), orderProcessStep.getStepId()));
        return orderProcessStep.getCorrelatedOrderId().getId();
    }

    public String createAndSave(OrderProcessStep orderProcessStep) {
        orderProcessRepository.save(OrderProcess.first(orderProcessStep));
        return orderProcessStep.getCorrelatedOrderId().getId();
    }

    public OrderProcess findByCorrelatedId(CorrelatedOrderId orderId) {
        return orderProcessRepository.findTopByOrderByStepAscAndByCorrelatedOrderId(orderId)
                .orElseThrow(() -> new OrderNotFoundException(orderId));
    }
}
