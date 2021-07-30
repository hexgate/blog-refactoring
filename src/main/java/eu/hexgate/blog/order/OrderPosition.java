package eu.hexgate.blog.order;

import eu.hexgate.blog.dto.OrderPositionDto;
import eu.hexgate.blog.product.Product;

import javax.persistence.*;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "ORDER_POSITION")
public class OrderPosition {

    @Id
    private String id;

    @ManyToOne
    @JoinColumn(name = "PRODUCT_ID")
    private Product product;

    private int quantity;

    public OrderPosition(Product product, int quantity) {
        this.id = UUID.randomUUID().toString();
        this.product = product;
        this.quantity = quantity;
    }

    private OrderPosition() {
        // jpa only
    }

    public OrderPositionDto dto() {
        return new OrderPositionDto(product.dto(), quantity);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OrderPosition that = (OrderPosition) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
