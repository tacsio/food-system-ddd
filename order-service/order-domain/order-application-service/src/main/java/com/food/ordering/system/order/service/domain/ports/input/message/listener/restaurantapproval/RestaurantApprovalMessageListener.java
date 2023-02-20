package com.food.ordering.system.order.service.domain.ports.input.message.listener.restaurantapproval;

import com.food.ordering.system.order.service.domain.dto.message.RestaurantApproveResponse;

public interface RestaurantApprovalMessageListener {

    void orderApproved(RestaurantApproveResponse restaurantApproveResponse);

    void orderRejected(RestaurantApproveResponse restaurantApproveResponse);
}
