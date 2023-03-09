package com.food.ordering.system.restaurant.service.domain.entity;

import com.food.ordering.system.domain.entity.BaseEntity;
import com.food.ordering.system.domain.valueobject.Money;
import com.food.ordering.system.domain.valueobject.ProductId;

public class Product extends BaseEntity<ProductId> {
    private String name;
    private Money price;
    private final int quantity;
    private boolean available;

    public Money amount() {
        return this.price.multiply(this.quantity);
    }

    public void updateWithConfirmedNamePriceAndAvailability(String name, Money price, boolean available) {
        this.name = name;
        this.price = price;
        this.available = available;
    }

    private Product(Builder builder) {
        setId(builder.id);
        this.name = builder.name;
        this.price = builder.price;
        this.quantity = builder.quantity;
        this.available = builder.available;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private ProductId id;
        private String name;
        private Money price;
        private int quantity;
        private boolean available;

        private Builder() {
        }

        public Builder id(ProductId val) {
            this.id = val;
            return this;
        }

        public Builder name(String val) {
            this.name = val;
            return this;
        }

        public Builder price(Money val) {
            this.price = val;
            return this;
        }

        public Builder quantity(int val) {
            this.quantity = val;
            return this;
        }

        public Builder available(boolean val) {
            this.available = val;
            return this;
        }

        public Product build() {
            return new Product(this);
        }


    }

    public String getName() {
        return name;
    }

    public Money getPrice() {
        return price;
    }

    public int getQuantity() {
        return quantity;
    }

    public boolean isAvailable() {
        return available;
    }
}
