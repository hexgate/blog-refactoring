package eu.hexgate.blog.uglyorder.order;

import eu.hexgate.blog.uglyorder.dto.OrderDto;
import eu.hexgate.blog.uglyorder.dto.OrderPositionDto;
import eu.hexgate.blog.uglyorder.user.User;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Comparator;
import java.util.Set;
import java.util.stream.Collectors;

@Entity
@Table(name = "ORDER_DATA")
public class Order {

    @Id
    private String id;

    @ManyToOne
    @JoinColumn(name = "OWNER_ID")
    private User owner;

    private OrderStatus status;

    private BigDecimal confirmedTotalPrice;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "ORDER_ID")
    private Set<OrderPosition> positions;


    public Order(String id, User owner, OrderStatus status, Set<OrderPosition> positions) {
        this.id = id;
        this.owner = owner;
        this.status = status;
        this.positions = positions;
    }

    private Order() {
        // jpa only
    }

    public void updateProductLines(Set<OrderPosition> positions) {
        if (OrderStatus.CONFIRMED == status) {
            throw new OrderStatusException(id, "Your order has already been confirmed.", status);
        }

        final boolean anyChanges = OrderPositionUtils.anyChanges(positions, this.positions);
        this.positions.clear();
        this.positions.addAll(positions);
        if (OrderStatus.ACCEPTED == status && anyChanges) {
            status = OrderStatus.DRAFT;
        }
    }

    public void accept() {
        if (OrderStatus.DRAFT != status) {
            throw new OrderStatusException(id, "Your order is not draft.", status);
        }

        status = OrderStatus.ACCEPTED;
    }

    public void decline() {
        if (OrderStatus.ACCEPTED != status) {
            throw new OrderStatusException(id, "Your order is not accepted.", status);
        }

        status = OrderStatus.DRAFT;
    }

    public void confirm(BigDecimal tax, BigDecimal shippingPrice) {
        if (OrderStatus.ACCEPTED != status && OrderStatus.VIP != status) {
            throw new OrderStatusException(id, "Your order is neither accepted nor vip.", status);
        }

        confirmedTotalPrice = calculateTotalPrice(tax, shippingPrice);
        status = OrderStatus.CONFIRMED;
    }

    public OrderDto dto(BigDecimal tax, BigDecimal shippingPrice) {
        return new OrderDto(
                id,
                status,
                calculateBasePrice(),
                calculateTotalPrice(tax, shippingPrice),
                confirmedTotalPrice,
                positions.stream()
                        .map(OrderPosition::dto)
                        .sorted(Comparator.comparing(o -> o.getProduct().getName()))
                        .collect(Collectors.toList())
        );
    }

    private BigDecimal calculateBasePrice() {
        return positions.stream()
                .map(OrderPosition::dto)
                .map(OrderPositionDto::getTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private BigDecimal calculateTotalPrice(BigDecimal tax, BigDecimal shippingPrice) {
        return calculateBasePrice().multiply(BigDecimal.ONE.add(tax)).add(shippingPrice);
    }
}
