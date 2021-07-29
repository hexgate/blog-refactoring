package eu.hexgate.blog.refactoredorder.usecase.createorder;

import eu.hexgate.blog.refactoredorder.domain.AggregateId;
import eu.hexgate.blog.refactoredorder.domain.order.CorrelatedOrderId;
import eu.hexgate.blog.refactoredorder.domain.order.MergedOrderPositions;
import eu.hexgate.blog.refactoredorder.domain.order.draft.DraftOrder;
import eu.hexgate.blog.refactoredorder.domain.order.draft.DraftOrderRepository;
import eu.hexgate.blog.refactoredorder.domain.order.process.OrderProcessService;
import eu.hexgate.blog.refactoredorder.domain.order.vip.VipOrder;
import eu.hexgate.blog.refactoredorder.domain.order.vip.VipOrderRepository;
import eu.hexgate.blog.refactoredorder.usecase.UseCase;
import eu.hexgate.blog.uglyorder.user.UserService;
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
        return userService.isVip(command.getUserId()) ?
                createVipOrder(mergedOrderPositions, ownerId) :
                createDraftOrder(mergedOrderPositions, ownerId);
    }

    private String createVipOrder(MergedOrderPositions mergedOrderPositions, AggregateId ownerId) {
        final VipOrder vipOrder = new VipOrder(
                CorrelatedOrderId.generate(),
                ownerId,
                mergedOrderPositions
        );

        final VipOrder saved = vipOrderRepository.save(vipOrder);
        return orderProcessService.createAndSave(saved);
    }

    private String createDraftOrder(MergedOrderPositions mergedOrderPositions, AggregateId ownerId) {
        final DraftOrder draftOrder = new DraftOrder(
                CorrelatedOrderId.generate(),
                ownerId,
                mergedOrderPositions
        );

        final DraftOrder saved = draftOrderRepository.save(draftOrder);
        return orderProcessService.createAndSave(saved);
    }
}
