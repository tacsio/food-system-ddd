package com.food.ordering.system.restaurant.service.domain.event;

import com.food.ordering.system.domain.event.publisher.DomainEventPublisher;
import com.food.ordering.system.domain.valueobject.RestaurantId;
import com.food.ordering.system.restaurant.service.domain.entity.OrderApproval;

import java.time.ZonedDateTime;
import java.util.List;

public record OrderApprovedEvent(OrderApproval orderApproval,
                                 RestaurantId restaurantId,
                                 List<String> failureMessages,
                                 ZonedDateTime createdAt,
                                 DomainEventPublisher<OrderApprovedEvent> orderApprovedEventDomainEventPublisher) implements OrderApprovalEvent {

    @Override
    public void fire() {
        orderApprovedEventDomainEventPublisher.publish(this);
    }
}
