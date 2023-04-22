package com.food.ordering.system.restaurant.service.domain;

import com.food.ordering.system.domain.DomainConstants;
import com.food.ordering.system.domain.event.publisher.DomainEventPublisher;
import com.food.ordering.system.domain.valueobject.OrderApprovalStatus;
import com.food.ordering.system.restaurant.service.domain.entity.Restaurant;
import com.food.ordering.system.restaurant.service.domain.event.OrderApprovalEvent;
import com.food.ordering.system.restaurant.service.domain.event.OrderApprovedEvent;
import com.food.ordering.system.restaurant.service.domain.event.OrderRejectedEvent;
import lombok.extern.slf4j.Slf4j;

import java.time.ZonedDateTime;
import java.util.List;

@Slf4j
public class RestaurantDomainServiceImpl implements RestaurantDomainService {
    @Override
    public OrderApprovalEvent validateOrder(Restaurant restaurant, List<String> failureMessages, DomainEventPublisher<OrderApprovedEvent> orderApprovedEventDomainEventPublisher, DomainEventPublisher<OrderRejectedEvent> orderRejectedEventDomainEventPublisher) {
        var orderId = restaurant.getOrderDetail().getId().value();

        log.info("Validating order with id: {}", orderId);
        restaurant.validateOrder(failureMessages);

        if (failureMessages.isEmpty()) {
            log.info("Order is approved for order id: {}", orderId);
            restaurant.constructOrderApproval(OrderApprovalStatus.APPROVED);
            return new OrderApprovedEvent(restaurant.getOrderApproval(),
                    restaurant.getId(),
                    failureMessages,
                    ZonedDateTime.now(DomainConstants.UTC));
        } else {
            log.info("Order is rejected for order id: {}", orderId);
            restaurant.constructOrderApproval(OrderApprovalStatus.REJECTED);

            return new OrderRejectedEvent(restaurant.getOrderApproval(),
                    restaurant.getId(),
                    failureMessages,
                    ZonedDateTime.now(DomainConstants.UTC));
        }
    }
}
