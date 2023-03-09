package com.food.ordering.system.restaurant.service.domain.event;

import com.food.ordering.system.domain.event.DomainEvent;
import com.food.ordering.system.domain.valueobject.RestaurantId;
import com.food.ordering.system.restaurant.service.domain.entity.OrderApproval;

import java.time.ZonedDateTime;
import java.util.List;

public interface OrderApprovalEvent extends DomainEvent<OrderApproval> {
    OrderApproval orderApproval();

    RestaurantId restaurantId();

    List<String> failureMessages();

    ZonedDateTime createdAt();
}
