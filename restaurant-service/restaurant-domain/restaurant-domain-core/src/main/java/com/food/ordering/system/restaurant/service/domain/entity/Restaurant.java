package com.food.ordering.system.restaurant.service.domain.entity;

import com.food.ordering.system.domain.entity.BaseEntity;
import com.food.ordering.system.domain.valueobject.Money;
import com.food.ordering.system.domain.valueobject.OrderApprovalStatus;
import com.food.ordering.system.domain.valueobject.OrderStatus;
import com.food.ordering.system.domain.valueobject.RestaurantId;
import com.food.ordering.system.restaurant.service.domain.valueobject.OrderApprovalId;

import java.util.List;
import java.util.UUID;

import static java.util.function.Predicate.not;

public class Restaurant extends BaseEntity<RestaurantId> {

    private OrderApproval orderApproval;
    private boolean active;
    private final OrderDetail orderDetail;

    public void validateOrder(List<String> failureMessages) {
        if (orderDetail.getOrderStatus().equals(OrderStatus.PAID)) {
            failureMessages.add("Payment is not completed for order: %s"
                    .formatted(orderDetail.getId()));
        }

        orderDetail.getProducts().stream()
                .filter(not(Product::isAvailable))
                .forEach(it -> failureMessages.add("Product with id: %s is not available"
                        .formatted(it.getId().value())));

        Money totalAmount = orderDetail.getProducts().stream()
                .map(Product::amount)
                .reduce(Money.ZERO, Money::add);

        if (!totalAmount.equals(orderDetail.getTotalAmount())) {
            failureMessages.add("Price total is not correct for order: %s"
                    .formatted(orderDetail.getId().value()));
        }
    }

    public void constructOrderApproval(OrderApprovalStatus orderApprovalStatus) {
        this.orderApproval = OrderApproval.builder()
                .id(new OrderApprovalId(UUID.randomUUID()))
                .restaurantId(this.getId())
                .orderId(this.orderDetail.getId())
                .orderApprovalStatus(orderApprovalStatus)
                .build();
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    private Restaurant(Builder builder) {
        setId(builder.id);
        this.orderApproval = builder.orderApproval;
        this.active = builder.active;
        this.orderDetail = builder.orderDetail;
    }

    public OrderApproval getOrderApproval() {
        return orderApproval;
    }

    public boolean isActive() {
        return active;
    }

    public OrderDetail getOrderDetail() {
        return orderDetail;
    }

    public static final class Builder {
        private RestaurantId id;
        private OrderApproval orderApproval;
        private boolean active;
        private OrderDetail orderDetail;

        private Builder() {
        }

        public Builder id(RestaurantId val) {
            this.id = val;
            return this;
        }

        public Builder orderApproval(OrderApproval val) {
            this.orderApproval = val;
            return this;
        }

        public Builder active(boolean val) {
            this.active = val;
            return this;
        }

        public Builder orderDetail(OrderDetail val) {
            this.orderDetail = val;
            return this;
        }

        public Restaurant build() {
            return new Restaurant(this);
        }
    }
}
