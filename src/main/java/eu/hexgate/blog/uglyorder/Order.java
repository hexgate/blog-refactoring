package eu.hexgate.blog.uglyorder;

import eu.hexgate.blog.uglyorder.dto.OrderDto;
import eu.hexgate.blog.uglyorder.dto.OrderPositionDto;
import eu.hexgate.blog.uglyorder.user.User;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Set;
import java.util.stream.Collectors;

@Entity
@Table(name = "ORDER")
public class Order {

    @Id
    private String id;

    @ManyToOne
    @JoinColumn(name = "OWNER_ID")
    private User owner;

    private OrderStatus status;

    private BigDecimal confirmedTotalPrice;

    @OneToMany
    private Set<OrderPosition> positions;

    public Order(String id, User owner, OrderStatus status, Set<OrderPosition> positions) {
        this.id = id;
        this.owner = owner;
        this.status = status;
        this.positions = positions;
    }

    public void updateProductLines(Set<OrderPosition> positions) {
        if (OrderStatus.CONFIRMED == status) {
            throw new OrderStatusException(id, "Your order has already been confirmed.", status);
        }

        final boolean anyChanges = anyChanges(positions, this.positions);
        this.positions = positions;

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

    private BigDecimal calculateBasePrice() {
        return positions.stream()
                .map(OrderPosition::dto)
                .map(OrderPositionDto::getTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

    }

    private BigDecimal calculateTotalPrice(BigDecimal tax, BigDecimal shippingPrice) {
        return calculateBasePrice().multiply(BigDecimal.ONE.add(tax)).add(shippingPrice);
    }

    private boolean anyChanges(Set<OrderPosition> before, Set<OrderPosition> after) {
        // todo
        return false;
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
                        .collect(Collectors.toSet())
        );
    }
}
