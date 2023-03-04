package com.food.ordering.system.order.service.domain.event;

import com.food.ordering.system.domain.event.DomainEvent;
import com.food.ordering.system.domain.event.publisher.DomainEventPublisher;
import com.food.ordering.system.order.service.domain.entity.Order;

import java.time.ZonedDateTime;

public record OrderCreatedEvent(Order order, ZonedDateTime createdAt,
                                DomainEventPublisher<OrderCreatedEvent> orderCreatedEventDomainEventPublisher) implements DomainEvent<Order> {

    @Override
    public void fire() {
        orderCreatedEventDomainEventPublisher.publish(this);
    }
}
