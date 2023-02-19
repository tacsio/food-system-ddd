package com.food.ordering.system.order.service.domain.entity;

import com.food.ordering.system.domain.entity.AggregateRoot;
import com.food.ordering.system.domain.valueobject.RestaurantId;

import java.util.List;

public class Restaurant extends AggregateRoot<RestaurantId> {
    private final List<Product> products;
    private boolean active;

    private Restaurant(Builder builder) {
        super.setId(builder.restaurantId);
        this.products = builder.products;
        this.active = builder.active;
    }

    public List<Product> getProducts() {
        return products;
    }

    public boolean isActive() {
        return active;
    }

    public static final class Builder {
        private RestaurantId restaurantId;
        private List<Product> products;
        private boolean active;

        private Builder() {
        }

        public Builder id(RestaurantId val) {
            this.restaurantId = val;
            return this;
        }

        public Builder products(List<Product> val) {
            this.products = val;
            return this;
        }

        public Builder active(boolean val) {
            this.active = val;
            return this;
        }

        public Restaurant build() {
            return new Restaurant(this);
        }
    }
}
