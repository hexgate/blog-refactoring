package eu.hexgate.blog.order.usecase.query;

import eu.hexgate.blog.order.domain.process.OrderStatus;
import org.springframework.data.annotation.Immutable;

import javax.persistence.*;

@Entity
@Table(name = "ORDER_PROCESS")
@Immutable
public class OrderProcessView {

    @Id
    private String id;

    private String stepId;

    private String correlatedOrderId;

    @Enumerated(value = EnumType.STRING)
    private OrderStatus status;

    private int step;

    public String getId() {
        return id;
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
