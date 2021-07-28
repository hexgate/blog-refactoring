package eu.hexgate.blog.refactoredorder.usecase.createorder;

import eu.hexgate.blog.refactoredorder.domain.AggregateId;
import eu.hexgate.blog.refactoredorder.domain.CorrelatedOrderId;
import eu.hexgate.blog.refactoredorder.domain.MergedOrderPositions;
import eu.hexgate.blog.refactoredorder.domain.draft.DraftOrder;
import eu.hexgate.blog.refactoredorder.domain.draft.DraftOrderRepository;
import eu.hexgate.blog.refactoredorder.domain.process.OrderProcessService;
import eu.hexgate.blog.refactoredorder.domain.vip.VipOrder;
import eu.hexgate.blog.refactoredorder.domain.vip.VipOrderRepository;
import eu.hexgate.blog.refactoredorder.usecase.UseCase;
import eu.hexgate.blog.uglyorder.user.UserService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@Service
public class CreateOrderUseCase implements UseCase<CreateOrderCommand, String> {

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
        return userService.isVip(command.getUserId()) ?
                createVipOrder(command) :
                createDraftOrder(command);
    }

    private String createVipOrder(CreateOrderCommand command) {
        final VipOrder vipOrder = new VipOrder(
                CorrelatedOrderId.generate(),
                AggregateId.fromString(command.getUserId()),
                MergedOrderPositions.of(command.getPositions())
        );

        final VipOrder saved = vipOrderRepository.save(vipOrder);
        return orderProcessService.createAndSave(saved);
    }

    private String createDraftOrder(CreateOrderCommand command) {
        final DraftOrder draftOrder = new DraftOrder(
                CorrelatedOrderId.generate(),
                AggregateId.fromString(command.getUserId()),
                MergedOrderPositions.of(command.getPositions())
        );

        final DraftOrder saved = draftOrderRepository.save(draftOrder);
        return orderProcessService.createAndSave(saved);
    }
}
