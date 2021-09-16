package eu.hexgate.blog.order.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import eu.hexgate.blog.order.usecase.process.OrderStatus;
import eu.hexgate.blog.order.web.MoneySerializer;

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

    private OrderDto(String id, OrderStatus status, BigDecimal basePrice, BigDecimal estimatedTotalPrice, BigDecimal confirmedTotalPrice, List<OrderPositionDto> positions) {
        this.id = id;
        this.status = status;
        this.basePrice = basePrice;
        this.estimatedTotalPrice = estimatedTotalPrice;
        this.confirmedTotalPrice = confirmedTotalPrice;
        this.positions = positions;
    }

    public OrderDto() {
    }

    public static Builder builder() {
        return new Builder();
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

    public static class Builder {
        private String id;
        private OrderStatus status;
        private BigDecimal basePrice;
        private BigDecimal estimatedTotalPrice;
        private BigDecimal confirmedTotalPrice;
        private List<OrderPositionDto> positions;

        public Builder withId(String id) {
            this.id = id;
            return this;
        }

        public Builder withStatus(OrderStatus status) {
            this.status = status;
            return this;
        }

        public Builder withBasePrice(BigDecimal basePrice) {
            this.basePrice = basePrice;
            return this;
        }

        public Builder withEstimatedTotalPrice(BigDecimal estimatedTotalPrice) {
            this.estimatedTotalPrice = estimatedTotalPrice;
            return this;
        }

        public Builder withConfirmedTotalPrice(BigDecimal confirmedTotalPrice) {
            this.confirmedTotalPrice = confirmedTotalPrice;
            return this;
        }

        public Builder withPositions(List<OrderPositionDto> positions) {
            this.positions = positions;
            return this;
        }

        public OrderDto build() {
            return new OrderDto(
                    id,
                    status,
                    basePrice,
                    estimatedTotalPrice,
                    confirmedTotalPrice,
                    positions
            );
        }
    }
}
