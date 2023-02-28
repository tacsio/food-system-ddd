package com.food.ordering.system.order.service.dataaccess.order.adapter;

import com.food.ordering.system.order.service.dataaccess.order.entity.OrderEntity;
import com.food.ordering.system.order.service.dataaccess.order.mapper.OrderDataAccessMapper;
import com.food.ordering.system.order.service.dataaccess.order.repository.OrderJpaRepository;
import com.food.ordering.system.order.service.domain.entity.Order;
import com.food.ordering.system.order.service.domain.ports.output.repository.OrderRepository;
import com.food.ordering.system.order.service.domain.valueobject.TrackingId;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Component
public class OrderRepositoryImpl implements OrderRepository {

    private OrderJpaRepository orderJpaRepository;
    private OrderDataAccessMapper orderDataAccessMapper;

    public OrderRepositoryImpl(OrderJpaRepository orderJpaRepository, OrderDataAccessMapper orderDataAccessMapper) {
        this.orderJpaRepository = orderJpaRepository;
        this.orderDataAccessMapper = orderDataAccessMapper;
    }

    @Override
    @Transactional
    public Order save(Order order) {
        OrderEntity orderEntity = orderJpaRepository.save(orderDataAccessMapper.orderToOrderEntity(order));
        return orderDataAccessMapper.orderEntityToOrder(orderEntity);
    }

    @Override
    public Optional<Order> findByTrackingId(TrackingId trackingId) {
        return orderJpaRepository.findByTrackingId(trackingId.value())
                .map(orderDataAccessMapper::orderEntityToOrder);
    }
}
