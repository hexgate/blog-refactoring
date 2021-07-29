package eu.hexgate.blog.refactoredorder.domain.order.process;

import eu.hexgate.blog.refactoredorder.domain.order.CorrelatedOrderId;
import eu.hexgate.blog.refactoredorder.domain.order.OrderStepId;

public interface OrderProcessStep {

    CorrelatedOrderId getCorrelatedOrderId();

    OrderStepId getStepId();

    OrderStatus getStatus();

}
