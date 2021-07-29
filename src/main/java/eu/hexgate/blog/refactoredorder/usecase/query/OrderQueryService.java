package eu.hexgate.blog.refactoredorder.usecase.query;

import eu.hexgate.blog.uglyorder.dto.OrderDto;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class OrderQueryService {

    public OrderDto findByOrderId(String orderId) {
        // todo
        return new OrderDto(orderId);
    }

}
