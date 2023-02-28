package com.food.ordering.system.order.service.domain.entity;

import com.food.ordering.system.domain.entity.AggregateRoot;
import com.food.ordering.system.domain.valueobject.*;
import com.food.ordering.system.order.service.domain.exception.OrderDomainException;
import com.food.ordering.system.order.service.domain.valueobject.OrderItemId;
import com.food.ordering.system.order.service.domain.valueobject.StreetAddress;
import com.food.ordering.system.order.service.domain.valueobject.TrackingId;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.Predicate;

public class Order extends AggregateRoot<OrderId> {

    private final CustomerId customerId;
    private final RestaurantId restaurantId;
    private final StreetAddress deliveryAddress;
    private final Money price;
    private final List<OrderItem> items;

    private TrackingId trackingId;
    private OrderStatus orderStatus;
    private List<String> failureMessages;

    public static final String FAILURE_MESSAGE_DELIMITER = ",";

    public void initializeOrder() {
        setId(new OrderId(UUID.randomUUID()));
        trackingId = new TrackingId(UUID.randomUUID());
        orderStatus = OrderStatus.PENDING;
        initializeOrderItems();
    }

    public void validateOrder() {
        validateInitialOrder();
        validateTotalPrice();
        validateItemsPrice();
    }

    public void pay() {
        if (orderStatus != OrderStatus.PENDING) {
            throw new OrderDomainException("Order is not in correct state for pay operation.");
        }

        orderStatus = OrderStatus.PAID;
    }

    public void approve() {
        if (orderStatus != OrderStatus.PAID) {
            throw new OrderDomainException("Order is not in correct state for approve operation.");
        }

        orderStatus = OrderStatus.APPROVED;
    }

    public void initCancel(List<String> failureMessages) {
        if (orderStatus != OrderStatus.PAID) {
            throw new OrderDomainException("Order is not in correct state for initCancel operation.");
        }

        orderStatus = OrderStatus.CANCELLING;
        updateFailureMessages(failureMessages);
    }

    public void cancel(List<String> failureMessages) {
        if (orderStatus != OrderStatus.CANCELLING && orderStatus != OrderStatus.PENDING) {
            throw new OrderDomainException("Order is not in correct state for cancel operation.");
        }

        orderStatus = OrderStatus.CANCELLED;
        updateFailureMessages(failureMessages);
    }

    private void updateFailureMessages(List<String> failureMessages) {
        if (this.failureMessages == null) this.failureMessages = new ArrayList<>();
        if (failureMessages == null) return;

        this.failureMessages.addAll(failureMessages.stream()
                .filter(Predicate.not(String::isBlank))
                .toList()
        );
    }

    private void validateInitialOrder() {
        if (orderStatus != null || getId() != null) {
            throw new OrderDomainException("Order is not in correct state for initialization.");
        }
    }

    private void validateTotalPrice() {
        if (price == null || !price.isGreaterThanZero()) {
            throw new OrderDomainException("Total price must be greater than zero.");
        }
    }

    private void validateItemsPrice() {
        var orderItemsTotal = items.stream()
                .map(it -> {
                    validateItemPrice(it);
                    return it.getSubTotal();
                }).reduce(Money.ZERO, Money::add);

        if (!price.equals(orderItemsTotal)) {
            var msg = "Total price: %s is not equal to Order items total: %s."
                    .formatted(price.amount(), orderItemsTotal.amount());
            throw new OrderDomainException(msg);
        }
    }

    private void initializeOrderItems() {
        long itemId = 1;

        for (OrderItem it : items) {
            it.initializeOrderItem(it.getOrderId(), new OrderItemId(itemId++));
        }
    }

    private void validateItemPrice(OrderItem orderItem) {
        if (!orderItem.isPriceValid()) {
            throw new OrderDomainException("Order item price: %s is not valid for product %s"
                    .formatted(orderItem.getPrice().amount(), orderItem.getProduct().getId().value()));
        }
    }

    private Order(Builder builder) {
        super.setId(builder.orderId);
        this.customerId = builder.customerId;
        this.restaurantId = builder.restaurantId;
        this.deliveryAddress = builder.deliveryAddress;
        this.price = builder.price;
        this.items = builder.items;
        this.trackingId = builder.trackingId;
        this.orderStatus = builder.orderStatus;
        this.failureMessages = builder.failureMessages;
    }

    public static Builder builder() {
        return new Builder();
    }

    public CustomerId getCustomerId() {
        return customerId;
    }

    public RestaurantId getRestaurantId() {
        return restaurantId;
    }

    public StreetAddress getDeliveryAddress() {
        return deliveryAddress;
    }

    public Money getPrice() {
        return price;
    }

    public List<OrderItem> getItems() {
        return items;
    }

    public TrackingId getTrackingId() {
        return trackingId;
    }

    public OrderStatus getOrderStatus() {
        return orderStatus;
    }

    public List<String> getFailureMessages() {
        return failureMessages;
    }

    public static final class Builder {
        private OrderId orderId;
        private CustomerId customerId;
        private RestaurantId restaurantId;
        private StreetAddress deliveryAddress;
        private Money price;
        private List<OrderItem> items;

        private TrackingId trackingId;
        private OrderStatus orderStatus;
        private List<String> failureMessages;

        private Builder() {
        }

        public Builder orderId(OrderId val) {
            orderId = val;
            return this;
        }

        ;

        public Builder customerId(CustomerId val) {
            customerId = val;
            return this;
        }

        ;

        public Builder restaurantId(RestaurantId val) {
            restaurantId = val;
            return this;
        }

        ;

        public Builder deliveryAddress(StreetAddress val) {
            deliveryAddress = val;
            return this;
        }

        ;

        public Builder price(Money val) {
            price = val;
            return this;
        }

        ;

        public Builder items(List<OrderItem> val) {
            items = val;
            return this;
        }

        ;

        public Builder trackingId(TrackingId val) {
            trackingId = val;
            return this;
        }

        ;

        public Builder orderStatus(OrderStatus val) {
            orderStatus = val;
            return this;
        }

        ;

        public Builder failureMessages(List<String> val) {
            failureMessages = val;
            return this;
        }

        ;

        public Order build() {
            return new Order(this);
        }
    }
}
