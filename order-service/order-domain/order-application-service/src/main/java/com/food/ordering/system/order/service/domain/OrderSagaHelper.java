package com.food.ordering.system.order.service.domain;

import com.food.ordering.system.domain.valueobject.OrderId;
import com.food.ordering.system.order.service.domain.entity.Order;
import com.food.ordering.system.order.service.domain.exception.OrderNotFoundException;
import com.food.ordering.system.order.service.domain.ports.output.repository.OrderRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Slf4j
@Component
public class OrderSagaHelper {

    private final OrderRepository orderRepository;

    public OrderSagaHelper(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    Order findOrder(String orderId) {
        var id = new OrderId(UUID.fromString(orderId));

        return orderRepository.findById(id).orElseThrow(() -> {
            var errorMsg = "Order with id: %s could not be found.".formatted(orderId);
            log.error(errorMsg);
            throw new OrderNotFoundException(errorMsg);
        });
    }

    void saveOrder(Order order) {
        orderRepository.save(order);
    }
}
