package com.food.ordering.system.order.service.dataaccess.restaurant.adapter;

import com.food.ordering.system.order.service.dataaccess.restaurant.entity.RestaurantEntity;
import com.food.ordering.system.order.service.dataaccess.restaurant.mapper.RestaurantDataAccess;
import com.food.ordering.system.order.service.dataaccess.restaurant.repository.RestaurantJpaRepository;
import com.food.ordering.system.order.service.domain.entity.Restaurant;
import com.food.ordering.system.order.service.domain.ports.output.repository.RestaurantRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class RestaurantRepositoryImpl implements RestaurantRepository {

    private RestaurantJpaRepository restaurantJpaRepository;
    private RestaurantDataAccess restaurantDataAccess;

    public RestaurantRepositoryImpl(RestaurantJpaRepository restaurantJpaRepository, RestaurantDataAccess restaurantDataAccess) {
        this.restaurantJpaRepository = restaurantJpaRepository;
        this.restaurantDataAccess = restaurantDataAccess;
    }

    @Override
    public Optional<Restaurant> findRestaurantInformation(Restaurant restaurant) {
        List<UUID> restaurantProducts = restaurant.getProducts().stream()
                .map(p -> p.getId().value())
                .collect(Collectors.toList());

        Optional<List<RestaurantEntity>> restaurantEntities = restaurantJpaRepository.findByRestaurantIdAndProductIdIn(restaurant.getId().value(), restaurantProducts);

        return restaurantEntities.map(restaurantDataAccess::restaurantsEntityToRestaurant);
    }
}
