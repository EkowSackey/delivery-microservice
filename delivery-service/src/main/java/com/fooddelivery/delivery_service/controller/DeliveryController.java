package com.fooddelivery.delivery_service.controller;

import com.fooddelivery.delivery_service.dto.CreateDeliveryRequest;
import com.fooddelivery.delivery_service.dto.DeliveryResponse;
import com.fooddelivery.delivery_service.service.DeliveryService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/deliveries")
public class DeliveryController {

    private final DeliveryService deliveryService;

    public DeliveryController(DeliveryService deliveryService) {
        this.deliveryService = deliveryService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void createDelivery(@Valid @RequestBody CreateDeliveryRequest request) {
        deliveryService.createDeliveryForOrder(
                request.getOrderId(),
                request.getPickupAddress(),
                request.getDeliveryAddress(),
                request.getCustomerFirstName(),
                request.getCustomerLastName(),
                request.getRestaurantName()
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<DeliveryResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(deliveryService.getById(id));
    }

    @GetMapping("/order/{orderId}")
    public ResponseEntity<DeliveryResponse> getByOrderId(@PathVariable Long orderId) {
        return ResponseEntity.ok(deliveryService.getByOrderId(orderId));
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<DeliveryResponse>> getByStatus(@PathVariable String status) {
        return ResponseEntity.ok(deliveryService.getByStatus(status));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<DeliveryResponse> updateStatus(
            @PathVariable Long id, @RequestParam String status) {
        return ResponseEntity.ok(deliveryService.updateStatus(id, status));
    }
}
