package eu.hexgate.blog.order.domain.process;

import eu.hexgate.blog.order.domain.CorrelatedOrderId;
import eu.hexgate.blog.order.dto.OrderNotFoundException;
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

    public OrderProcess findByCorrelatedId(CorrelatedOrderId orderId) {
        return orderProcessRepository.findNewest(orderId)
                .orElseThrow(() -> new OrderNotFoundException(orderId));
    }
}
