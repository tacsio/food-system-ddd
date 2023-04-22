package com.food.ordering.system.order.service.domain.mapper;

import com.food.ordering.system.domain.valueobject.*;
import com.food.ordering.system.order.service.domain.dto.create.CreateOrderCommand;
import com.food.ordering.system.order.service.domain.dto.create.CreateOrderResponse;
import com.food.ordering.system.order.service.domain.dto.create.OrderAddress;
import com.food.ordering.system.order.service.domain.dto.create.OrderItem;
import com.food.ordering.system.order.service.domain.dto.track.TrackOrderResponse;
import com.food.ordering.system.order.service.domain.entity.Order;
import com.food.ordering.system.order.service.domain.entity.Product;
import com.food.ordering.system.order.service.domain.entity.Restaurant;
import com.food.ordering.system.order.service.domain.event.OrderCancelledEvent;
import com.food.ordering.system.order.service.domain.event.OrderCreatedEvent;
import com.food.ordering.system.order.service.domain.event.OrderPaidEvent;
import com.food.ordering.system.order.service.domain.outbox.model.approval.OrderApprovalEventPayload;
import com.food.ordering.system.order.service.domain.outbox.model.approval.OrderApprovalEventProduct;
import com.food.ordering.system.order.service.domain.outbox.model.payment.OrderPaymentEventPayload;
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

    public CreateOrderResponse orderToCreateOrderResponse(Order order, String message) {
        return new CreateOrderResponse(order.getTrackingId().value(),
                order.getOrderStatus(),
                message);
    }

    public TrackOrderResponse OrderToTrackOrderResponse(Order order) {
        return new TrackOrderResponse(order.getTrackingId().value(),
                order.getOrderStatus(),
                order.getFailureMessages());
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

    public OrderPaymentEventPayload orderCreatedEventToOrderPaymentEventPayload(OrderCreatedEvent orderCreatedEvent) {
        return OrderPaymentEventPayload.builder()
                .customerId(orderCreatedEvent.order().getCustomerId().value().toString())
                .orderId(orderCreatedEvent.order().getId().value().toString())
                .price(orderCreatedEvent.order().getPrice().amount())
                .createdAt(orderCreatedEvent.createdAt())
                .paymentOrderStatus(PaymentOrderStatus.PENDING.name())
                .build();
    }

    public OrderApprovalEventPayload orderPaidEventToOrderApprovalEventPayload(OrderPaidEvent orderPaidEvent) {
        return OrderApprovalEventPayload.builder()
                .orderId(orderPaidEvent.order().getId().value().toString())
                .restaurantId(orderPaidEvent.order().getRestaurantId().value().toString())
                .restaurantOrderStatus(RestaurantOrderStatus.PAID.name())
                .products(orderPaidEvent.order().getItems().stream().map(orderItem ->
                        OrderApprovalEventProduct.builder()
                                .id(orderItem.getProduct().getId().value().toString())
                                .quantity(orderItem.getQuantity())
                                .build()).collect(Collectors.toList()))
                .price(orderPaidEvent.order().getPrice().amount())
                .createdAt(orderPaidEvent.createdAt())
                .build();
    }

    public OrderPaymentEventPayload orderCancelledEventToOrderPaymentEventPayload(OrderCancelledEvent orderCancelledEvent) {
        return OrderPaymentEventPayload.builder()
                .customerId(orderCancelledEvent.order().getCustomerId().value().toString())
                .orderId(orderCancelledEvent.order().getId().value().toString())
                .price(orderCancelledEvent.order().getPrice().amount())
                .createdAt(orderCancelledEvent.createdAt())
                .paymentOrderStatus(PaymentOrderStatus.CANCELLED.name())
                .build();
    }
}
