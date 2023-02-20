package com.food.ordering.system.order.service.domain;

import com.food.ordering.system.order.service.domain.dto.message.RestaurantApproveResponse;
import com.food.ordering.system.order.service.domain.ports.input.message.listener.restaurantapproval.RestaurantApprovalMessageListener;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

@Service
@Validated
@Slf4j
public class RestaurantApprovalMessageListenerImpl implements RestaurantApprovalMessageListener {
    @Override
    public void orderApproved(RestaurantApproveResponse restaurantApproveResponse) {

    }

    @Override
    public void orderRejected(RestaurantApproveResponse restaurantApproveResponse) {

    }
}
