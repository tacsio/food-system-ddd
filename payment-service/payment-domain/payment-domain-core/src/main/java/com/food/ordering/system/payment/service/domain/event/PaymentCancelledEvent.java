package com.food.ordering.system.payment.service.domain.event;

import com.food.ordering.system.payment.service.domain.entity.Payment;

import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.List;

public record PaymentCancelledEvent(Payment payment, ZonedDateTime createdAt)
        implements PaymentEvent {
    @Override
    public List<String> failureMessages() {
        return Collections.emptyList();
    }

}
