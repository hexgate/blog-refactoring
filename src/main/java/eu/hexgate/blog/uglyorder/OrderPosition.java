package eu.hexgate.blog.uglyorder;

import eu.hexgate.blog.uglyorder.dto.OrderPositionDto;
import eu.hexgate.blog.uglyorder.product.Product;

import javax.persistence.*;
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
}
