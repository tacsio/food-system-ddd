package com.food.ordering.system.restaurant.service.domain;

import com.food.ordering.system.domain.valueobject.OrderId;
import com.food.ordering.system.restaurant.service.domain.dto.RestaurantApprovalRequest;
import com.food.ordering.system.restaurant.service.domain.entity.Product;
import com.food.ordering.system.restaurant.service.domain.entity.Restaurant;
import com.food.ordering.system.restaurant.service.domain.event.OrderApprovalEvent;
import com.food.ordering.system.restaurant.service.domain.exception.RestaurantNotFoundException;
import com.food.ordering.system.restaurant.service.domain.mapper.RestaurantDataMapper;
import com.food.ordering.system.restaurant.service.domain.ports.output.message.publisher.OrderApprovedMessagePublisher;
import com.food.ordering.system.restaurant.service.domain.ports.output.message.publisher.OrderRejectedMessagePublisher;
import com.food.ordering.system.restaurant.service.domain.ports.output.repository.OrderApprovalRepository;
import com.food.ordering.system.restaurant.service.domain.ports.output.repository.RestaurantRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.Function;

import static java.util.stream.Collectors.toMap;

@Component
@Slf4j
public class RestaurantApprovalRequestHelper {

    private final RestaurantDomainService restaurantDomainService;
    private final RestaurantDataMapper restaurantDataMapper;
    private final RestaurantRepository restaurantRepository;
    private final OrderApprovalRepository orderApprovalRepository;
    private final OrderApprovedMessagePublisher orderApprovedMessagePublisher;
    private final OrderRejectedMessagePublisher orderRejectedMessagePublisher;

    public RestaurantApprovalRequestHelper(RestaurantDomainService restaurantDomainService, RestaurantDataMapper restaurantDataMapper, RestaurantRepository restaurantRepository, OrderApprovalRepository orderApprovalRepository, OrderApprovedMessagePublisher orderApprovedMessagePublisher, OrderRejectedMessagePublisher orderRejectedMessagePublisher) {
        this.restaurantDomainService = restaurantDomainService;
        this.restaurantDataMapper = restaurantDataMapper;
        this.restaurantRepository = restaurantRepository;
        this.orderApprovalRepository = orderApprovalRepository;
        this.orderApprovedMessagePublisher = orderApprovedMessagePublisher;
        this.orderRejectedMessagePublisher = orderRejectedMessagePublisher;
    }

    @Transactional
    public OrderApprovalEvent persistOrderApproval(RestaurantApprovalRequest restaurantApprovalRequest) {
        log.info("Processing restaurant approval for order id: {}", restaurantApprovalRequest.getOrderId());
        List<String> failureMessages = new ArrayList<>();

        var restaurant = findRestaurant(restaurantApprovalRequest);
        var orderApprovalEvent = restaurantDomainService.validateOrder(restaurant, failureMessages, orderApprovedMessagePublisher, orderRejectedMessagePublisher);
        orderApprovalRepository.save(restaurant.getOrderApproval());

        return orderApprovalEvent;
    }

    private Restaurant findRestaurant(RestaurantApprovalRequest restaurantApprovalRequest) {
        var restaurant = restaurantDataMapper.restaurantApprovalRequestToRestaurant(restaurantApprovalRequest);
        var restaurantEntity = restaurantRepository.findRestaurantInformation(restaurant)
                .orElseThrow(() -> {
                    var msgError = "Restaurant with id %s not found.".formatted(restaurant.getId().value());
                    log.error(msgError);
                    throw new RestaurantNotFoundException(msgError);
                });

        var productMap = restaurantEntity.getOrderDetail()
                .getProducts().stream()
                .collect(toMap(Product::getId, Function.identity()));

        restaurant.setActive(restaurantEntity.isActive());
        restaurant.getOrderDetail().getProducts().stream()
                .filter(product -> productMap.containsKey(product.getId()))
                .forEach(product -> {
                    var productEntity = productMap.get(product.getId());
                    product.updateWithConfirmedNamePriceAndAvailability(
                            productEntity.getName(), productEntity.getPrice(), productEntity.isAvailable());
                });

        restaurant.getOrderDetail().setId(new OrderId(UUID.fromString(restaurantApprovalRequest.getOrderId())));

        return restaurant;
    }
}
