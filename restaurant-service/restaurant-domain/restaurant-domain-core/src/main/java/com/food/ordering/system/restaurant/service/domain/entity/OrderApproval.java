package com.food.ordering.system.restaurant.service.domain.entity;

import com.food.ordering.system.domain.entity.BaseEntity;
import com.food.ordering.system.domain.valueobject.OrderApprovalStatus;
import com.food.ordering.system.domain.valueobject.OrderId;
import com.food.ordering.system.domain.valueobject.RestaurantId;
import com.food.ordering.system.restaurant.service.domain.valueobject.OrderApprovalId;

public class OrderApproval extends BaseEntity<OrderApprovalId> {

    private final RestaurantId restaurantId;
    private final OrderId orderId;
    private final OrderApprovalStatus approvalStatus;

    private OrderApproval(Builder builder) {
        setId(builder.id);
        this.restaurantId = builder.restaurantId;
        this.orderId = builder.orderId;
        this.approvalStatus = builder.approvalStatus;
    }

    public RestaurantId getRestaurantId() {
        return restaurantId;
    }

    public OrderId getOrderId() {
        return orderId;
    }

    public OrderApprovalStatus getApprovalStatus() {
        return approvalStatus;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private OrderApprovalId id;
        private RestaurantId restaurantId;
        private OrderId orderId;
        private OrderApprovalStatus approvalStatus;

        private Builder() {
        }

        public Builder id(OrderApprovalId val) {
            this.id = val;
            return this;
        }

        public Builder restaurantId(RestaurantId val) {
            this.restaurantId = val;
            return this;
        }

        public Builder orderId(OrderId val) {
            this.orderId = val;
            return this;
        }

        public Builder approvalStatus(OrderApprovalStatus val) {
            this.approvalStatus = val;
            return this;
        }

        public OrderApproval build() {
            return new OrderApproval(this);
        }

    }
}
