package eu.hexgate.blog.order.web;

import eu.hexgate.blog.order.domain.errors.DomainError;
import eu.hexgate.blog.order.forms.OrderForm;
import eu.hexgate.blog.order.usecase.UseCase;
import eu.hexgate.blog.order.usecase.acceptorder.AcceptOrderCommand;
import eu.hexgate.blog.order.usecase.confirmorder.ConfirmOrderCommand;
import eu.hexgate.blog.order.usecase.createorder.CreateOrderCommand;
import eu.hexgate.blog.order.usecase.declineorder.DeclineOrderCommand;
import eu.hexgate.blog.order.usecase.query.OrderQueryService;
import eu.hexgate.blog.order.usecase.updateorderpositions.UpdateOrderPositionsCommand;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.net.URI;

@RestController
public class OrderController {

    private final UseCase<CreateOrderCommand> createOrderUseCase;
    private final UseCase<UpdateOrderPositionsCommand> updateOrderPositionsUseCase;
    private final UseCase<AcceptOrderCommand> acceptOrderUseCase;
    private final UseCase<DeclineOrderCommand> declineOrderUseCase;
    private final UseCase<ConfirmOrderCommand> confirmOrderUseCase;
    private final OrderQueryService orderQueryService;
    private final DomainErrorResolver domainErrorResolver = new DomainErrorResolver();

    public OrderController(UseCase<CreateOrderCommand> createOrderUseCase, UseCase<UpdateOrderPositionsCommand> updateOrderPositionsUseCase, UseCase<AcceptOrderCommand> acceptOrderUseCase, UseCase<DeclineOrderCommand> declineOrderUseCase, UseCase<ConfirmOrderCommand> confirmOrderUseCase, OrderQueryService orderQueryService) {
        this.createOrderUseCase = createOrderUseCase;
        this.updateOrderPositionsUseCase = updateOrderPositionsUseCase;
        this.acceptOrderUseCase = acceptOrderUseCase;
        this.declineOrderUseCase = declineOrderUseCase;
        this.confirmOrderUseCase = confirmOrderUseCase;
        this.orderQueryService = orderQueryService;
    }

    @PostMapping("/orders")
    ResponseEntity<?> createOrder(@RequestBody OrderForm orderForm, HttpServletRequest httpServletRequest) {
        final String userId = getCurrentUserId(httpServletRequest);

        return createOrderUseCase.execute(new CreateOrderCommand(userId, orderForm.getPositions()))
                .fold(
                        domainErrorResolver::resolve,
                        orderId -> orderQueryService.findByOrderId(orderId)
                                .map(orderDto -> ResponseEntity
                                        .created(URI.create(String.format("/orders/%s", orderId)))
                                        .body(orderDto)
                                )
                                .getOrElse(() -> ResponseEntity
                                        .notFound()
                                        .build())
                );
    }

    @GetMapping("/orders/{id}")
    ResponseEntity<?> getOrder(@PathVariable String id) {
        return findOrder(id);
    }

    @PatchMapping("/orders/{id}/update-positions")
    ResponseEntity<?> updatePositions(@PathVariable String id, @RequestBody OrderForm orderForm) {
        return updateOrderPositionsUseCase.execute(new UpdateOrderPositionsCommand(id, orderForm.getPositions()))
                .fold(
                        domainErrorResolver::resolve,
                        orderId -> findOrder(id)
                );
    }

    @PatchMapping("/orders/{id}/accept")
    ResponseEntity<?> accept(@PathVariable String id) {
        return acceptOrderUseCase.execute(new AcceptOrderCommand(id))
                .fold(
                        domainErrorResolver::resolve,
                        orderId -> findOrder(id)
                );
    }

    @PatchMapping("/orders/{id}/decline")
    ResponseEntity<?> decline(@PathVariable String id) {
        return declineOrderUseCase.execute(new DeclineOrderCommand(id))
                .fold(
                        domainErrorResolver::resolve,
                        orderId -> findOrder(id)
                );
    }

    @PatchMapping("/orders/{id}/confirm")
    ResponseEntity<?> confirm(@PathVariable String id) {
        return confirmOrderUseCase.execute(new ConfirmOrderCommand(id))
                .fold(
                        domainErrorResolver::resolve,
                        orderId -> findOrder(id)
                );
    }

    private ResponseEntity<?> findOrder(String orderId) {
        return orderQueryService.findByOrderId(orderId)
                .toEither(DomainError.orderNotFound(orderId))
                .fold(
                        domainErrorResolver::resolve,
                        order -> ResponseEntity.ok().body(order)
                );
    }

    private String getCurrentUserId(HttpServletRequest request) {
        return request.getHeader("X-USER-ID");
    }
}
