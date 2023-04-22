package com.food.ordering.system.restaurant.service.domain.mapper;

import com.food.ordering.system.domain.valueobject.Money;
import com.food.ordering.system.domain.valueobject.OrderId;
import com.food.ordering.system.domain.valueobject.OrderStatus;
import com.food.ordering.system.domain.valueobject.RestaurantId;
import com.food.ordering.system.restaurant.service.domain.dto.RestaurantApprovalRequest;
import com.food.ordering.system.restaurant.service.domain.entity.OrderDetail;
import com.food.ordering.system.restaurant.service.domain.entity.Product;
import com.food.ordering.system.restaurant.service.domain.entity.Restaurant;
import com.food.ordering.system.restaurant.service.domain.event.OrderApprovalEvent;
import com.food.ordering.system.restaurant.service.domain.outbox.model.OrderEventPayload;
import org.springframework.stereotype.Component;

import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class RestaurantDataMapper {
    public Restaurant restaurantApprovalRequestToRestaurant(RestaurantApprovalRequest restaurantApprovalRequest) {
        var totalAmount = new Money(restaurantApprovalRequest.getPrice());
        var orderStatus = OrderStatus.valueOf(restaurantApprovalRequest.getRestaurantOrderStatus().name());
        var products = restaurantApprovalRequest.getProducts().stream()
                .map(it -> Product.builder()
                        .id(it.getId())
                        .quantity(it.getQuantity())
                        .build())
                .collect(Collectors.toList());

        var orderDetail = OrderDetail.builder()
                .orderId(new OrderId(UUID.fromString(restaurantApprovalRequest.getOrderId())))
                .products(products)
                .totalAmount(totalAmount)
                .orderStatus(orderStatus)
                .build();

        return Restaurant.builder()
                .id(new RestaurantId(UUID.fromString(restaurantApprovalRequest.getRestaurantId())))
                .orderDetail(orderDetail)
                .build();
    }

    public OrderEventPayload orderApprovalEventToOrderEventPayload(OrderApprovalEvent orderApprovalEvent) {
        return OrderEventPayload.builder()
                .orderId(orderApprovalEvent.orderApproval().getOrderId().value().toString())
                .restaurantId(orderApprovalEvent.restaurantId().value().toString())
                .orderApprovalStatus(orderApprovalEvent.orderApproval().getApprovalStatus().name())
                .createdAt(orderApprovalEvent.createdAt())
                .failureMessages(orderApprovalEvent.failureMessages())
                .build();
    }
}
