package com.food.ordering.system.order.service.dataaccess.restaurant.mapper;

import com.food.ordering.system.domain.valueobject.Money;
import com.food.ordering.system.domain.valueobject.ProductId;
import com.food.ordering.system.domain.valueobject.RestaurantId;
import com.food.ordering.system.order.service.dataaccess.restaurant.entity.RestaurantEntity;
import com.food.ordering.system.order.service.dataaccess.restaurant.exception.RestaurantDataAccessException;
import com.food.ordering.system.order.service.domain.entity.Product;
import com.food.ordering.system.order.service.domain.entity.Restaurant;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class RestaurantDataAccess {

    public List<UUID> restaurantToRestaurantProducts(Restaurant restaurant) {
        return restaurant.getProducts().stream()
                .map(p -> p.getId().value())
                .collect(Collectors.toList());
    }

    public Restaurant restaurantsEntityToRestaurant(List<RestaurantEntity> restaurantEntities) {
        RestaurantEntity restaurantEntity = restaurantEntities.stream()
                .findFirst()
                .orElseThrow(() -> new RestaurantDataAccessException("Restaurant could not be found."));

        List<Product> products = restaurantEntities.stream()
                .map(it -> new Product(new ProductId(it.getProductId()),
                        it.getProductName(),
                        new Money(it.getProductPrice()))
                ).collect(Collectors.toList());

        Restaurant restaurant = Restaurant.builder()
                .id(new RestaurantId(restaurantEntity.getRestaurantId()))
                .active(restaurantEntity.isRestaurantActive())
                .products(products)
                .build();


        return restaurant;
    }
}
