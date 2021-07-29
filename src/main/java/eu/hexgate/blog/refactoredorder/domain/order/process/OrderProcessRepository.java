package eu.hexgate.blog.refactoredorder.domain.order.process;

import eu.hexgate.blog.refactoredorder.domain.AggregateId;
import eu.hexgate.blog.refactoredorder.domain.order.CorrelatedOrderId;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;

import java.util.List;
import java.util.Optional;

public interface OrderProcessRepository extends Repository<OrderProcess, AggregateId> {

    OrderProcess save(OrderProcess orderProcess);

    @Query("select o from OrderProcess o where o.correlatedOrderId = ?1 order by o.step desc")
    List<OrderProcess> findByOrderId(CorrelatedOrderId id, Pageable pageable);

    default Optional<OrderProcess> findTop(CorrelatedOrderId id) {
        return Optional.ofNullable(findByOrderId(id, PageRequest.of(0, 1)).get(0));
    }

}
