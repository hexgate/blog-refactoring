package eu.hexgate.blog.uglyorder.order;

import eu.hexgate.blog.refactoredorder.usecase.UseCase;
import eu.hexgate.blog.refactoredorder.usecase.acceptorder.AcceptOrderCommand;
import eu.hexgate.blog.refactoredorder.usecase.confirmorder.ConfirmOrderCommand;
import eu.hexgate.blog.refactoredorder.usecase.createorder.CreateOrderCommand;
import eu.hexgate.blog.refactoredorder.usecase.declineorder.DeclineOrderCommand;
import eu.hexgate.blog.refactoredorder.usecase.query.OrderQueryService;
import eu.hexgate.blog.refactoredorder.usecase.updateorderpositions.UpdateOrderPositionsCommand;
import eu.hexgate.blog.uglyorder.dto.ErrorDto;
import eu.hexgate.blog.uglyorder.dto.OrderDto;
import eu.hexgate.blog.uglyorder.forms.OrderForm;
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

    public OrderController(UseCase<CreateOrderCommand> createOrderUseCase, UseCase<UpdateOrderPositionsCommand> updateOrderPositionsUseCase, UseCase<AcceptOrderCommand> acceptOrderUseCase, UseCase<DeclineOrderCommand> declineOrderUseCase, UseCase<ConfirmOrderCommand> confirmOrderUseCase, OrderQueryService orderQueryService) {
        this.createOrderUseCase = createOrderUseCase;
        this.updateOrderPositionsUseCase = updateOrderPositionsUseCase;
        this.acceptOrderUseCase = acceptOrderUseCase;
        this.declineOrderUseCase = declineOrderUseCase;
        this.confirmOrderUseCase = confirmOrderUseCase;
        this.orderQueryService = orderQueryService;
    }

    @PostMapping("/orders")
    ResponseEntity<OrderDto> createOrder(@RequestBody OrderForm orderForm, HttpServletRequest httpServletRequest) {
        final String userId = getCurrentUserId(httpServletRequest);
        final String orderId = createOrderUseCase.execute(new CreateOrderCommand(userId, orderForm.getPositions()));

        return ResponseEntity
                .created(URI.create(String.format("/orders/%s", orderId)))
                .body(orderQueryService.findByOrderId(orderId));
    }

    @GetMapping("/orders/{id}")
    ResponseEntity<OrderDto> getOrder(@PathVariable String id) {
        return ResponseEntity.ok().body(orderQueryService.findByOrderId(id));
    }

    @PatchMapping("/orders/{id}/update-positions")
    ResponseEntity<OrderDto> updatePositions(@PathVariable String id, @RequestBody OrderForm orderForm) {
        final String orderId = updateOrderPositionsUseCase.execute(new UpdateOrderPositionsCommand(id, orderForm.getPositions()));
        return ResponseEntity.ok().body(orderQueryService.findByOrderId(orderId));
    }

    @PatchMapping("/orders/{id}/accept")
    ResponseEntity<OrderDto> accept(@PathVariable String id) {
        final String orderId = acceptOrderUseCase.execute(new AcceptOrderCommand(id));
        return ResponseEntity.ok().body(orderQueryService.findByOrderId(orderId));

    }

    @PatchMapping("/orders/{id}/decline")
    ResponseEntity<OrderDto> decline(@PathVariable String id) {
        final String orderId = declineOrderUseCase.execute(new DeclineOrderCommand(id));
        return ResponseEntity.ok().body(orderQueryService.findByOrderId(orderId));

    }

    @PatchMapping("/orders/{id}/confirm")
    ResponseEntity<OrderDto> confirm(@PathVariable String id) {
        final String orderId = confirmOrderUseCase.execute(new ConfirmOrderCommand(id));
        return ResponseEntity.ok().body(orderQueryService.findByOrderId(orderId));

    }

    @ExceptionHandler(value = OrderStatusException.class)
    ResponseEntity<ErrorDto> handleOrderStatusException(OrderStatusException e) {
        return ResponseEntity
                .badRequest()
                .body(new ErrorDto(e.getMessage(), e.getOrderId()));
    }

    @ExceptionHandler(value = OrderNotFoundException.class)
    ResponseEntity<ErrorDto> handleOrderNotFound(OrderNotFoundException e) {
        return ResponseEntity
                .notFound()
                .build();
    }

    private String getCurrentUserId(HttpServletRequest request) {
        return request.getHeader("X-USER-ID");
    }
}
