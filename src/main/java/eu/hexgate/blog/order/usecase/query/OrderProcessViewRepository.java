package eu.hexgate.blog.order.usecase.query;


import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;

import java.util.List;
import java.util.Optional;

public interface OrderProcessViewRepository extends Repository<OrderProcessView, String> {

    @Query("select o from OrderProcess o where o.correlatedOrderId = ?1 order by o.step desc")
    List<OrderProcessView> findByOrderId(String id, Pageable pageable);

    default Optional<OrderProcessView> findNewest(String id) {
        final List<OrderProcessView> byOrderId = findByOrderId(id, PageRequest.of(0, 1));

        return byOrderId.isEmpty() ? Optional.empty() : Optional.of(byOrderId.get(0));
    }

}
