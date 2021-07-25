package eu.hexgate.blog.uglyorder;

import eu.hexgate.blog.uglyorder.dto.OrderDto;
import eu.hexgate.blog.uglyorder.forms.OrderForm;
import eu.hexgate.blog.uglyorder.forms.OrderPositionForm;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Set;

@SpringBootTest
public class OrderTest {

    @Autowired
    private OrderService orderService;

    @Test
    public void test() {
        final OrderForm orderForm = new OrderForm(Set.of(new OrderPositionForm("1", 2), new OrderPositionForm("2", 1)));

        final OrderDto order = orderService.createOrder("1", orderForm);

        final OrderDto orderDto = orderService.find(order.getId());

        final OrderForm orderForm2 = new OrderForm(Set.of(new OrderPositionForm("3", 3), new OrderPositionForm("4", 1)));

        System.out.println("########################## before update");

        orderService.updateOrder(order.getId(), orderForm2);

        final OrderDto orderDto1 = orderService.find(order.getId());
    }

}
