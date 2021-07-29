package eu.hexgate.blog.order.domain.vip;

import eu.hexgate.blog.order.domain.OrderStepId;
import org.springframework.data.repository.Repository;

import java.util.Optional;

public interface VipOrderRepository extends Repository<VipOrder, OrderStepId> {

    VipOrder save(VipOrder vipOrder);

    Optional<VipOrder> findById(OrderStepId id);

}
