package com.food.ordering.system.order.service.domain.mapper;

import com.food.ordering.system.domain.valueobject.CustomerId;
import com.food.ordering.system.domain.valueobject.Money;
import com.food.ordering.system.domain.valueobject.ProductId;
import com.food.ordering.system.domain.valueobject.RestaurantId;
import com.food.ordering.system.order.service.domain.dto.create.CreateOrderCommand;
import com.food.ordering.system.order.service.domain.dto.create.CreateOrderResponse;
import com.food.ordering.system.order.service.domain.dto.create.OrderAddress;
import com.food.ordering.system.order.service.domain.dto.create.OrderItem;
import com.food.ordering.system.order.service.domain.entity.Order;
import com.food.ordering.system.order.service.domain.entity.Product;
import com.food.ordering.system.order.service.domain.entity.Restaurant;
import com.food.ordering.system.order.service.domain.valueobject.StreetAddress;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class OrderDataMapper {

    public Restaurant createOrderCommandToRestaurant(CreateOrderCommand createOrderCommand) {
        return Restaurant.builder()
                .id(new RestaurantId(createOrderCommand.restaurantId()))
                .products(orderItemsToProductEntities(createOrderCommand.items()))
                .build();
    }

    public Order createOrderCommandToOrder(CreateOrderCommand createOrderCommand) {
        return Order.builder()
                .customerId(new CustomerId(createOrderCommand.customerId()))
                .restaurantId(new RestaurantId(createOrderCommand.restaurantId()))
                .deliveryAddress(orderAddressToStreetAddress(createOrderCommand.address()))
                .price(new Money(createOrderCommand.price()))
                .items(orderItemsToOrderItemEntities(createOrderCommand.items()))
                .build();
    }

    public CreateOrderResponse orderToCreateOrderResponse(Order order) {
        return new CreateOrderResponse(order.getTrackingId().value(),
                order.getOrderStatus(),
                "Order is created with id: %s".formatted(order.getId()));
    }

    private List<com.food.ordering.system.order.service.domain.entity.OrderItem> orderItemsToOrderItemEntities(List<OrderItem> items) {
        return items.stream()
                .map(this::convert)
                .collect(Collectors.toList());
    }

    private com.food.ordering.system.order.service.domain.entity.OrderItem convert(OrderItem orderItemDto) {
        return com.food.ordering.system.order.service.domain.entity.OrderItem
                .builder()
                .product(new Product(new ProductId(orderItemDto.productId())))
                .price(new Money(orderItemDto.price()))
                .quantity(orderItemDto.quantity())
                .subTotal(new Money(orderItemDto.subTotal()))
                .build();
    }

    private StreetAddress orderAddressToStreetAddress(OrderAddress orderAddress) {
        return new StreetAddress(UUID.randomUUID(),
                orderAddress.street(),
                orderAddress.postalCode(),
                orderAddress.city());
    }

    private List<Product> orderItemsToProductEntities(List<OrderItem> orderItems) {
        return orderItems.stream()
                .map(OrderItem::productId)
                .map(ProductId::new)
                .map(Product::new)
                .collect(Collectors.toList());
    }
}
