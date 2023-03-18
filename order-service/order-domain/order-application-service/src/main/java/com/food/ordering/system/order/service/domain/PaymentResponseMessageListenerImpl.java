package com.food.ordering.system.order.service.domain;

import com.food.ordering.system.order.service.domain.dto.message.PaymentResponse;
import com.food.ordering.system.order.service.domain.event.OrderPaidEvent;
import com.food.ordering.system.order.service.domain.ports.input.message.listener.payment.PaymentResponseMessageListener;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.stream.Collectors;

@Service
@Slf4j
@Validated
public class PaymentResponseMessageListenerImpl implements PaymentResponseMessageListener {
    private final OrderPaymentSaga paymentSaga;

    public PaymentResponseMessageListenerImpl(OrderPaymentSaga paymentSaga) {
        this.paymentSaga = paymentSaga;
    }

    @Override
    public void paymentCompleted(PaymentResponse paymentResponse) {
        OrderPaidEvent orderPaidEvent = paymentSaga.process(paymentResponse);
        log.info("Publishing OrderPaidEvent for order id: {}", paymentResponse.getOrderId());
        orderPaidEvent.fire();
    }

    @Override
    public void paymentCancelled(PaymentResponse paymentResponse) {
        paymentSaga.rollback(paymentResponse);
        log.info("Order is rolled back for order id: {} with failure messages: {}",
                paymentResponse.getOrderId(),
                paymentResponse.getFailureMessages().stream().collect(Collectors.joining(",")));
    }
}
