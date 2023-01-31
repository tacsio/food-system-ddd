package com.food.ordering.system.order.service.domain.valueobject;

import java.util.UUID;

public record StreetAddress(UUID id, String street, String postalCode, String city) {
}
