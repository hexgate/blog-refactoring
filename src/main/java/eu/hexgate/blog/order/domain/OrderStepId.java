package eu.hexgate.blog.order.domain;

import javax.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;


@Embeddable
public class OrderStepId implements Serializable {

    private String id;

    private OrderStepId() {
        // jpa only
    }

    private OrderStepId(String id) {
        this.id = id;
    }

    public static OrderStepId generate() {
        return new OrderStepId(UUID.randomUUID().toString());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OrderStepId that = (OrderStepId) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
