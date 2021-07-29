package eu.hexgate.blog.order.domain;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

@Embeddable
public class CorrelatedOrderId implements Serializable {

    @Column(name = "CORRELATED_ORDER_ID")
    private String id;

    private CorrelatedOrderId() {
        // jpa only
    }

    private CorrelatedOrderId(String id) {
        this.id = id;
    }

    public static CorrelatedOrderId generate() {
        return new CorrelatedOrderId(UUID.randomUUID().toString());
    }

    public static CorrelatedOrderId fromString(String id) {
        return new CorrelatedOrderId(Objects.requireNonNull(id));
    }

    public String getId() {
        return id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CorrelatedOrderId that = (CorrelatedOrderId) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}