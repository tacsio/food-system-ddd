package com.food.ordering.system.order.service.domain.dto.create;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotBlank;

public record OrderAddress(
        @NotBlank @Max(50) String street,
        @NotBlank @Max(10) String postalCode,
        @NotBlank @Max(50) String city) {

}
