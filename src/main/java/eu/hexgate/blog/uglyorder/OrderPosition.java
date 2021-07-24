package eu.hexgate.blog.uglyorder;

import eu.hexgate.blog.uglyorder.dto.OrderPositionDto;
import eu.hexgate.blog.uglyorder.product.Product;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.util.UUID;

@Entity
@Table(name = "ORDER_LINE")
public class OrderPosition {

    @Id
    private String id;

    @ManyToOne
    private Product product;

    private int quantity;

    public OrderPosition(Product product, int quantity) {
        this.id = UUID.randomUUID().toString();
        this.product = product;
        this.quantity = quantity;
    }

    public void addQuantity(int quantity) {
        this.quantity += quantity;
    }

    public String getProductId() {
        return product.getId();
    }

    public OrderPositionDto dto() {
        return new OrderPositionDto(product.dto(), quantity);
    }
}
