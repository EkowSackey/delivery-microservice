package com.fooddelivery.delivery_service.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CreateDeliveryRequest {
    @NotNull
    private Long orderId;

    @NotBlank
    private String pickupAddress;

    @NotBlank
    private String deliveryAddress;

    @NotBlank
    private String customerFirstName;

    @NotBlank
    private String customerLastName;

    @NotBlank
    private String restaurantName;
}
