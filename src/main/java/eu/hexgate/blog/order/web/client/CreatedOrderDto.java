package eu.hexgate.blog.order.web.client;

import eu.hexgate.blog.order.dto.OrderDto;

public class CreatedOrderDto {
    private final OrderDto createdOrder;
    private final String location;

    public CreatedOrderDto(OrderDto createdOrder, String location) {
        this.createdOrder = createdOrder;
        this.location = location;
    }

    public OrderDto getCreatedOrder() {
        return createdOrder;
    }

    public String getLocation() {
        return location;
    }
}
