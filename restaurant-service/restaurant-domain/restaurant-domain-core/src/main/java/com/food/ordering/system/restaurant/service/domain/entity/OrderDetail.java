package com.food.ordering.system.restaurant.service.domain.entity;

import com.food.ordering.system.domain.entity.BaseEntity;
import com.food.ordering.system.domain.valueobject.Money;
import com.food.ordering.system.domain.valueobject.OrderId;
import com.food.ordering.system.domain.valueobject.OrderStatus;

import java.util.List;

public class OrderDetail extends BaseEntity<OrderId> {
    private OrderStatus orderStatus;
    private Money totalAmount;
    private final List<Product> products;

    private OrderDetail(Builder builder) {
        setId(builder.orderId);
        this.orderStatus = builder.orderStatus;
        this.totalAmount = builder.totalAmount;
        this.products = builder.products;
    }

    public static Builder builder() {
        return new Builder();
    }

    public OrderStatus getOrderStatus() {
        return orderStatus;
    }

    public Money getTotalAmount() {
        return totalAmount;
    }

    public List<Product> getProducts() {
        return products;
    }

    public final static class Builder {
        private OrderId orderId;
        private OrderStatus orderStatus;
        private Money totalAmount;
        private List<Product> products;

        private Builder() {
        }

        public Builder orderId(OrderId val) {
            this.orderId = val;
            return this;
        }

        public Builder orderStatus(OrderStatus val) {
            this.orderStatus = val;
            return this;
        }

        public Builder totalAmount(Money val) {
            this.totalAmount = val;
            return this;
        }

        public Builder products(List<Product> val) {
            this.products = val;
            return this;
        }

        public OrderDetail build() {
            return new OrderDetail(this);
        }
    }


}
