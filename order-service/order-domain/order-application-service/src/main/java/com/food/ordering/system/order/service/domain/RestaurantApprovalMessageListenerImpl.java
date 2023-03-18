package com.food.ordering.system.order.service.domain;

import com.food.ordering.system.order.service.domain.dto.message.RestaurantApproveResponse;
import com.food.ordering.system.order.service.domain.event.OrderCancelledEvent;
import com.food.ordering.system.order.service.domain.ports.input.message.listener.restaurantapproval.RestaurantApprovalMessageListener;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.stream.Collectors;

@Service
@Validated
@Slf4j
public class RestaurantApprovalMessageListenerImpl implements RestaurantApprovalMessageListener {

    private final OrderApprovalSaga orderApprovalSaga;

    public RestaurantApprovalMessageListenerImpl(OrderApprovalSaga orderApprovalSaga) {
        this.orderApprovalSaga = orderApprovalSaga;
    }

    @Override
    public void orderApproved(RestaurantApproveResponse restaurantApproveResponse) {
        orderApprovalSaga.process(restaurantApproveResponse);
        log.info("Order is approved for order id: {}", restaurantApproveResponse.getOrderId());
    }

    @Override
    public void orderRejected(RestaurantApproveResponse restaurantApproveResponse) {
        OrderCancelledEvent orderCancelledEvent = orderApprovalSaga.rollback(restaurantApproveResponse);

        log.info("Publishing order cancelled event for order id: {} with failure message: {}",
                orderCancelledEvent.order().getId().value(),
                restaurantApproveResponse.getFailureMessages().stream().collect(Collectors.joining(",")));
        orderCancelledEvent.fire();
    }
}
