package eu.hexgate.blog.refactoredorder.domain;

import javax.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

@Embeddable
public class AggregateId implements Serializable {

    private String id;

    private AggregateId() {
        // jpa only
    }

    private AggregateId(String id) {
        this.id = id;
    }

    public static AggregateId generate() {
        return new AggregateId(UUID.randomUUID().toString());
    }

    public static AggregateId fromString(String id) {
        return new AggregateId(Objects.requireNonNull(id));
    }

    public String getId() {
        return id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AggregateId that = (AggregateId) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}