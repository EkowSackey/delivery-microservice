package com.fooddelivery.delivery_service.dto;

import com.fooddelivery.delivery_service.model.Delivery;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class DeliveryResponse {
    private Long id;
    private String status;
    private String driverName;
    private String driverPhone;
    private String pickupAddress;
    private String deliveryAddress;
    private LocalDateTime assignedAt;
    private LocalDateTime pickedUpAt;
    private LocalDateTime deliveredAt;
    private LocalDateTime createdAt;

    private Long orderId;

    public static DeliveryResponse fromEntity(Delivery d) {
        DeliveryResponse dto = new DeliveryResponse();
        dto.setId(d.getId());
        dto.setStatus(d.getStatus().name());
        dto.setDriverName(d.getDriverName());
        dto.setDriverPhone(d.getDriverPhone());
        dto.setPickupAddress(d.getPickupAddress());
        dto.setDeliveryAddress(d.getDeliveryAddress());
        dto.setAssignedAt(d.getAssignedAt());
        dto.setPickedUpAt(d.getPickedUpAt());
        dto.setDeliveredAt(d.getDeliveredAt());
        dto.setCreatedAt(d.getCreatedAt());

        dto.setOrderId(d.getOrderId());

        return dto;
    }
}
