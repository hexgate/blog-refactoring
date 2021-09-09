package eu.hexgate.blog.order.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import eu.hexgate.blog.order.web.MoneySerializer;
import eu.hexgate.blog.order.usecase.process.OrderStatus;

import java.math.BigDecimal;
import java.util.List;

public class OrderDto {

    private String id;

    private OrderStatus status;

    @JsonSerialize(using = MoneySerializer.class)
    private BigDecimal basePrice;

    @JsonSerialize(using = MoneySerializer.class)
    private BigDecimal estimatedTotalPrice;

    @JsonSerialize(using = MoneySerializer.class)
    private BigDecimal confirmedTotalPrice;

    private List<OrderPositionDto> positions;

    public OrderDto(String id, OrderStatus status, BigDecimal basePrice, BigDecimal estimatedTotalPrice, BigDecimal confirmedTotalPrice, List<OrderPositionDto> positions) {
        this.id = id;
        this.status = status;
        this.basePrice = basePrice;
        this.estimatedTotalPrice = estimatedTotalPrice;
        this.confirmedTotalPrice = confirmedTotalPrice;
        this.positions = positions;
    }

    public OrderDto() {
    }

    public String getId() {
        return id;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public BigDecimal getBasePrice() {
        return basePrice;
    }

    public BigDecimal getEstimatedTotalPrice() {
        return estimatedTotalPrice;
    }

    public BigDecimal getConfirmedTotalPrice() {
        return confirmedTotalPrice;
    }

    public List<OrderPositionDto> getPositions() {
        return positions;
    }
}
