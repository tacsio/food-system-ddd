package com.food.ordering.system.payment.service.domain.event;

import com.food.ordering.system.domain.event.DomainEvent;
import com.food.ordering.system.payment.service.domain.entity.Payment;

import java.time.ZonedDateTime;
import java.util.List;

public interface PaymentEvent extends DomainEvent<Payment> {
    Payment payment();

    ZonedDateTime createdAt();

    List<String> failureMessages();

    void fire();
}
