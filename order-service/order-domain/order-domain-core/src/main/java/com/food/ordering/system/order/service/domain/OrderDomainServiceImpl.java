package com.food.ordering.system.order.service.domain;

import com.food.ordering.system.order.service.domain.entity.Order;
import com.food.ordering.system.order.service.domain.entity.OrderItem;
import com.food.ordering.system.order.service.domain.entity.Product;
import com.food.ordering.system.order.service.domain.entity.Restaurant;
import com.food.ordering.system.order.service.domain.event.OrderCancelledEvent;
import com.food.ordering.system.order.service.domain.event.OrderCreatedEvent;
import com.food.ordering.system.order.service.domain.event.OrderPaidEvent;
import com.food.ordering.system.order.service.domain.exception.OrderDomainException;
import lombok.extern.slf4j.Slf4j;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.function.Function;

import static com.food.ordering.system.domain.DomainConstants.UTC;
import static java.util.stream.Collectors.toMap;

@Slf4j
class OrderDomainServiceImpl implements OrderDomainService {

    @Override
    public OrderCreatedEvent validateAndInitializeOrder(Order order, Restaurant restaurant) {
        validateRestaurant(restaurant);
        setOrderProductInformation(order, restaurant);
        order.validateOrder();
        order.initializeOrder();
        log.info("Order created with id: {} is initiated", order.getId().value());
        return new OrderCreatedEvent(order, ZonedDateTime.now(UTC));
    }

    private void setOrderProductInformation(Order order, Restaurant restaurant) {
        var productMap = restaurant.getProducts().stream()
                .collect(toMap(Product::getId, Function.identity()));

        order.getItems().stream()
                .map(OrderItem::getProduct)
                .filter(product -> productMap.containsKey(product.getId()))
                .forEach(product -> {
                    Product restaurantProduct = productMap.get(product.getId());
                    product.updateWithConfirmedNameAndPrice(restaurantProduct.getName(), restaurantProduct.getPrice());
                });
    }

    private void validateRestaurant(Restaurant restaurant) {
        if (!restaurant.isActive()) {
            throw new OrderDomainException("Restaurant with id %s is currently not active"
                    .formatted(restaurant.getId().value()));
        }
    }

    @Override
    public OrderPaidEvent payOrder(Order order) {
        order.pay();
        log.info("Order with id: {} is paid.", order.getId().value());
        return new OrderPaidEvent(order, ZonedDateTime.now(UTC));
    }

    @Override
    public void approveOrder(Order order) {
        order.approve();
        log.info("Order with id: {} is approved.", order.getId().value());
    }

    @Override
    public OrderCancelledEvent cancelOrderPayment(Order order, List<String> failureMessages) {
        order.initCancel(failureMessages);
        log.info("Order payment is calling for order id: {}", order.getId().value());
        return new OrderCancelledEvent(order, ZonedDateTime.now(UTC));
    }

    @Override
    public void cancelOrder(Order order, List<String> failureMessages) {
        order.cancel(failureMessages);
        log.info("Order with id: {} is cancelled.", order.getId().value());
    }
}
