package com.food.ordering.system.payment.service.domain.event;

import com.food.ordering.system.payment.service.domain.entity.Payment;

import java.time.ZonedDateTime;
import java.util.List;

public record PaymentFailedEvent(Payment payment, ZonedDateTime createdAt, List<String> failureMessages)
        implements PaymentEvent {

}
