package eu.hexgate.blog.uglyorder.dto;

import eu.hexgate.blog.uglyorder.OrderStatus;

import java.math.BigDecimal;
import java.util.Set;

public class OrderDto {

    private String id;

    private OrderStatus status;

    private BigDecimal basePrice;

    private BigDecimal estimateTotalPrice;

    private BigDecimal confirmedTotalPrice;

    private Set<OrderPositionDto> positions;

    public OrderDto(String id, OrderStatus status, BigDecimal basePrice, BigDecimal estimateTotalPrice, BigDecimal confirmedTotalPrice, Set<OrderPositionDto> positions) {
        this.id = id;
        this.status = status;
        this.basePrice = basePrice;
        this.estimateTotalPrice = estimateTotalPrice;
        this.confirmedTotalPrice = confirmedTotalPrice;
        this.positions = positions;
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

    public BigDecimal getEstimateTotalPrice() {
        return estimateTotalPrice;
    }

    public BigDecimal getConfirmedTotalPrice() {
        return confirmedTotalPrice;
    }

    public Set<OrderPositionDto> getPositions() {
        return positions;
    }
}
