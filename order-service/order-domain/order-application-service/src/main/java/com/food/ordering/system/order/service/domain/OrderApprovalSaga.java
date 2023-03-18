package com.food.ordering.system.order.service.domain;

import com.food.ordering.system.domain.event.EmptyEvent;
import com.food.ordering.system.order.service.domain.dto.message.RestaurantApproveResponse;
import com.food.ordering.system.order.service.domain.entity.Order;
import com.food.ordering.system.order.service.domain.event.OrderCancelledEvent;
import com.food.ordering.system.order.service.domain.ports.output.message.publisher.payment.OrderCancelledPaymentRequestMessagePublisher;
import com.food.ordering.system.saga.SagaStep;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
public class OrderApprovalSaga implements SagaStep<RestaurantApproveResponse, EmptyEvent, OrderCancelledEvent> {

    private final OrderDomainService orderDomainService;
    private final OrderCancelledPaymentRequestMessagePublisher orderCancelledPaymentRequestMessagePublisher;
    private final OrderSagaHelper orderSagaHelper;

    public OrderApprovalSaga(OrderDomainService orderDomainService,
                             OrderCancelledPaymentRequestMessagePublisher orderCancelledPaymentRequestMessagePublisher,
                             OrderSagaHelper orderSagaHelper) {
        this.orderDomainService = orderDomainService;
        this.orderSagaHelper = orderSagaHelper;
        this.orderCancelledPaymentRequestMessagePublisher = orderCancelledPaymentRequestMessagePublisher;
    }

    @Override
    @Transactional
    public EmptyEvent process(RestaurantApproveResponse restaurantApproveResponse) {
        log.info("Approving order with id: {}", restaurantApproveResponse.getOrderId());
        Order order = orderSagaHelper.findOrder(restaurantApproveResponse.getOrderId());
        orderDomainService.approveOrder(order);

        orderSagaHelper.saveOrder(order);
        log.info("Order with id: {} is approved.", order.getId().value());

        return EmptyEvent.INSTANCE;
    }

    @Override
    @Transactional
    public OrderCancelledEvent rollback(RestaurantApproveResponse restaurantApproveResponse) {
        log.info("Cancelling order with id: {}", restaurantApproveResponse.getOrderId());
        Order order = orderSagaHelper.findOrder(restaurantApproveResponse.getOrderId());
        OrderCancelledEvent orderCancelledEvent = orderDomainService.cancelOrderPayment(order,
                restaurantApproveResponse.getFailureMessages(),
                orderCancelledPaymentRequestMessagePublisher);

        orderSagaHelper.saveOrder(order);
        log.info("Order with id: {} is cancelling.", order.getId().value());
        return orderCancelledEvent;
    }
}
