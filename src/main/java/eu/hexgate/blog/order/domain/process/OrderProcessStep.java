package eu.hexgate.blog.order.domain.process;

import eu.hexgate.blog.order.domain.CorrelatedOrderId;
import eu.hexgate.blog.order.domain.OrderStepId;

public interface OrderProcessStep {

    CorrelatedOrderId getCorrelatedOrderId();

    OrderStepId getStepId();

    OrderStatus getStatus();

}
