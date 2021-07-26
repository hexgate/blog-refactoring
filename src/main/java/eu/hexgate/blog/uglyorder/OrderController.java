package eu.hexgate.blog.uglyorder;

import eu.hexgate.blog.uglyorder.dto.OrderDto;
import eu.hexgate.blog.uglyorder.forms.OrderForm;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.net.URI;

@RestController
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping("/orders")
    ResponseEntity<OrderDto> createOrder(@RequestBody OrderForm orderForm, HttpServletRequest httpServletRequest) {
        final String userId = getCurrentUserId(httpServletRequest);
        final OrderDto order = orderService.createOrder(userId, orderForm);
        return ResponseEntity
                .created(URI.create(String.format("/orders/%s", order.getId())))
                .body(order);
    }

    @GetMapping("/orders/{id}")
    ResponseEntity<OrderDto> getOrder(@PathVariable String id) {
        return ResponseEntity.ok(orderService.find(id));
    }

    @PatchMapping("/orders/{id}/update-positions")
    ResponseEntity<OrderDto> updatePositions(@PathVariable String id, @RequestBody OrderForm orderForm) {
        return ResponseEntity.ok(orderService.updateOrder(id, orderForm));
    }

    @PatchMapping("/orders/{id}/accept")
    ResponseEntity<OrderDto> accept(@PathVariable String id) {
        return ResponseEntity.ok(orderService.accept(id));
    }

    @PatchMapping("/orders/{id}/decline")
    ResponseEntity<OrderDto> decline(@PathVariable String id) {
        return ResponseEntity.ok(orderService.decline(id));
    }

    @PatchMapping("/orders/{id}/confirm")
    ResponseEntity<OrderDto> confirm(@PathVariable String id) {
        return ResponseEntity.ok(orderService.confirm(id));
    }

    private String getCurrentUserId(HttpServletRequest request) {
        return request.getHeader("X-USER-ID");
    }
}
