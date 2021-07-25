package eu.hexgate.blog.uglyorder;

import eu.hexgate.blog.uglyorder.dto.OrderDto;
import eu.hexgate.blog.uglyorder.externalmodules.ShippingService;
import eu.hexgate.blog.uglyorder.externalmodules.TaxService;
import eu.hexgate.blog.uglyorder.forms.OrderForm;
import eu.hexgate.blog.uglyorder.forms.OrderPositionForm;
import eu.hexgate.blog.uglyorder.product.Product;
import eu.hexgate.blog.uglyorder.product.ProductService;
import eu.hexgate.blog.uglyorder.user.User;
import eu.hexgate.blog.uglyorder.user.UserService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final TaxService taxService;
    private final ShippingService shippingService;
    private final UserService userService;
    private final ProductService productService;

    public OrderService(OrderRepository orderRepository, TaxService taxService, ShippingService shippingService, UserService userService, ProductService productService) {
        this.orderRepository = orderRepository;
        this.taxService = taxService;
        this.shippingService = shippingService;
        this.userService = userService;
        this.productService = productService;
    }

    public OrderDto createOrder(String userId, OrderForm orderForm) {
        final User user = userService.getUser(userId);
        final OrderStatus orderStatus = user.isVip() ? OrderStatus.VIP : OrderStatus.DRAFT;
        final Set<OrderPosition> orderPositions = mergeOrderPositions(orderForm);

        final Order order = new Order(UUID.randomUUID().toString(), user, orderStatus, orderPositions);
        return saveAndGetDto(order);
    }

    @Transactional
    public OrderDto updateOrder(String orderId, OrderForm orderForm) {
        final Order order = findOrder(orderId);
        final Set<OrderPosition> mergedOrderPositions = mergeOrderPositions(orderForm);
        order.updateProductLines(mergedOrderPositions);
        return saveAndGetDto(order);
    }

    public OrderDto accept(String orderId) {
        final Order order = findOrder(orderId);
        order.accept();
        return saveAndGetDto(order);
    }

    public OrderDto decline(String orderId) {
        final Order order = findOrder(orderId);
        order.decline();
        return saveAndGetDto(order);
    }

    private OrderDto confirm(String orderId) {
        final Order order = findOrder(orderId);
        order.confirm(taxService.gerCurrentTax(), shippingService.getCurrentShippingPrice());
        return saveAndGetDto(order);
    }

    @Transactional(readOnly = true)
    public OrderDto find(String orderId) {
        return findOrder(orderId).dto(taxService.gerCurrentTax(), shippingService.getCurrentShippingPrice());
    }

    private Order findOrder(String id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new OrderNotFoundException(id));
    }

    private Set<OrderPosition> mergeOrderPositions(OrderForm orderForm) {
        return OrderPositionUtils.merge(orderForm.getPositions())
                .stream()
                .map(this::createOrderPosition)
                .collect(Collectors.toSet());
    }

    private OrderPosition createOrderPosition(OrderPositionForm orderPositionForm) {
        final Product product = productService.getProduct(orderPositionForm.getProductId());
        return new OrderPosition(product, orderPositionForm.getQuantity());
    }

    private OrderDto saveAndGetDto(Order order) {
        return orderRepository.save(order)
                .dto(taxService.gerCurrentTax(), shippingService.getCurrentShippingPrice());
    }
}
