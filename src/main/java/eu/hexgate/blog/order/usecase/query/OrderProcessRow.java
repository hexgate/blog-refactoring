package eu.hexgate.blog.order.usecase.query;

import eu.hexgate.blog.order.domain.process.OrderStatus;

import javax.persistence.*;

public class OrderProcessRow {

    private String stepId;

    private String correlatedOrderId;

    @Enumerated(value = EnumType.STRING)
    private OrderStatus status;

    private int step;

    public OrderProcessRow(String stepId, String correlatedOrderId, OrderStatus status, int step) {
        this.stepId = stepId;
        this.correlatedOrderId = correlatedOrderId;
        this.status = status;
        this.step = step;
    }

    public String getStepId() {
        return stepId;
    }

    public String getCorrelatedOrderId() {
        return correlatedOrderId;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public int getStep() {
        return step;
    }
}
