package eu.hexgate.blog.order.usecase.createorder;

import eu.hexgate.blog.order.AggregateId;
import eu.hexgate.blog.order.domain.CorrelatedOrderId;
import eu.hexgate.blog.order.domain.MergedOrderPositions;
import eu.hexgate.blog.order.domain.draft.DraftOrder;
import eu.hexgate.blog.order.domain.draft.DraftOrderRepository;
import eu.hexgate.blog.order.domain.process.OrderProcessService;
import eu.hexgate.blog.order.domain.process.OrderProcessStep;
import eu.hexgate.blog.order.domain.vip.VipOrder;
import eu.hexgate.blog.order.domain.vip.VipOrderRepository;
import eu.hexgate.blog.order.usecase.UseCase;
import eu.hexgate.blog.user.UserService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@Service
public class CreateOrderUseCase implements UseCase<CreateOrderCommand> {

    private final DraftOrderRepository draftOrderRepository;
    private final VipOrderRepository vipOrderRepository;
    private final OrderProcessService orderProcessService;
    private final UserService userService;

    public CreateOrderUseCase(DraftOrderRepository draftOrderRepository, VipOrderRepository vipOrderRepository, OrderProcessService orderProcessService, UserService userService) {
        this.draftOrderRepository = draftOrderRepository;
        this.vipOrderRepository = vipOrderRepository;
        this.orderProcessService = orderProcessService;
        this.userService = userService;
    }

    @Override
    public String execute(CreateOrderCommand command) {
        final MergedOrderPositions mergedOrderPositions = MergedOrderPositions.of(command.getPositions());
        final AggregateId ownerId = AggregateId.fromString(command.getUserId());
        final OrderProcessStep orderProcessStep = userService.isVip(command.getUserId()) ?
                createVipOrder(mergedOrderPositions, ownerId) :
                createDraftOrder(mergedOrderPositions, ownerId);

        return orderProcessService.createAndSave(orderProcessStep);
    }

    private OrderProcessStep createVipOrder(MergedOrderPositions mergedOrderPositions, AggregateId ownerId) {
        final VipOrder vipOrder = new VipOrder(
                CorrelatedOrderId.generate(),
                ownerId,
                mergedOrderPositions
        );

        return vipOrderRepository.save(vipOrder);
    }

    private OrderProcessStep createDraftOrder(MergedOrderPositions mergedOrderPositions, AggregateId ownerId) {
        final DraftOrder draftOrder = new DraftOrder(
                CorrelatedOrderId.generate(),
                ownerId,
                mergedOrderPositions
        );

        return draftOrderRepository.save(draftOrder);
    }
}
