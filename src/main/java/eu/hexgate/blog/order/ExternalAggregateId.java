package eu.hexgate.blog.order;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

@Embeddable
@Access(AccessType.FIELD)
public class ExternalAggregateId implements Serializable, Comparable<ExternalAggregateId> {

    private String id;

    private ExternalAggregateId() {
        // jpa only
    }

    private ExternalAggregateId(String id) {
        this.id = id;
    }

    public static ExternalAggregateId generate() {
        return new ExternalAggregateId(UUID.randomUUID().toString());
    }

    public static ExternalAggregateId fromString(String id) {
        return new ExternalAggregateId(Objects.requireNonNull(id));
    }

    public String asString() {
        return id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ExternalAggregateId that = (ExternalAggregateId) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public int compareTo(ExternalAggregateId o) {
        return id.compareTo(o.id);
    }
}
