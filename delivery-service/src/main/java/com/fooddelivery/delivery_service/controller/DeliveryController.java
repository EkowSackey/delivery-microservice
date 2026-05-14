package com.fooddelivery.delivery_service.controller;

import com.fooddelivery.delivery_service.dto.CreateDeliveryRequest;
import com.fooddelivery.delivery_service.dto.DeliveryResponse;
import com.fooddelivery.delivery_service.service.DeliveryService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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
    @PreAuthorize("hasRole('SERVICE')")
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
    @PreAuthorize("hasRole('SERVICE')")
    public ResponseEntity<DeliveryResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(deliveryService.getById(id));
    }

    @GetMapping("/order/{orderId}")
    @PreAuthorize("hasRole('SERVICE')")
    public ResponseEntity<DeliveryResponse> getByOrderId(@PathVariable Long orderId) {
        return ResponseEntity.ok(deliveryService.getByOrderId(orderId));
    }

    @GetMapping("/status/{status}")
    @PreAuthorize("hasRole('SERVICE')")
    public ResponseEntity<List<DeliveryResponse>> getByStatus(@PathVariable String status) {
        return ResponseEntity.ok(deliveryService.getByStatus(status));
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasRole('SERVICE')")
    public ResponseEntity<DeliveryResponse> updateStatus(
            @PathVariable Long id, @RequestParam String status) {
        return ResponseEntity.ok(deliveryService.updateStatus(id, status));
    }

    @PostMapping("/order/{orderId}/cancel")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('SERVICE')")
    public void cancelByOrderId(@PathVariable Long orderId) {
        deliveryService.cancelDeliveryByOrderId(orderId);
    }
}
