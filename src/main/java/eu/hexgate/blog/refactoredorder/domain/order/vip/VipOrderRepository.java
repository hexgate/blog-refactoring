package eu.hexgate.blog.refactoredorder.domain.order.vip;

import eu.hexgate.blog.refactoredorder.domain.order.OrderStepId;
import org.springframework.data.repository.Repository;

import java.util.Optional;

public interface VipOrderRepository extends Repository<VipOrder, OrderStepId> {

    VipOrder save(VipOrder vipOrder);

    Optional<VipOrder> findById(OrderStepId id);

}
