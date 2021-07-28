package eu.hexgate.blog.refactoredorder.domain.process;

import eu.hexgate.blog.refactoredorder.domain.CorrelatedOrderId;
import eu.hexgate.blog.refactoredorder.domain.OrderStepId;

public interface OrderProcessStep {

    CorrelatedOrderId getCorrelatedOrderId();

    OrderStepId getStepId();

    OrderStatus getStatus();

}
