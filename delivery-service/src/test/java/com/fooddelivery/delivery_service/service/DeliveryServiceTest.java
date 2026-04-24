package com.fooddelivery.delivery_service.service;

import com.fooddelivery.delivery_service.dto.DeliveryResponse;
import com.fooddelivery.delivery_service.exception.ResourceNotFoundException;
import com.fooddelivery.delivery_service.model.Delivery;
import com.fooddelivery.delivery_service.repository.DeliveryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DeliveryServiceTest {

    @Mock
    private DeliveryRepository deliveryRepository;

    @InjectMocks
    private DeliveryService deliveryService;

    private Delivery sampleDelivery;

    @BeforeEach
    void setUp() {
        sampleDelivery = Delivery.builder()
                .id(1L)
                .orderId(100L)
                .status(Delivery.DeliveryStatus.ASSIGNED)
                .driverName("Carlos Martinez")
                .pickupAddress("123 Food St")
                .deliveryAddress("456 Home Ave")
                .build();
    }

    @Test
    void createDeliveryForOrder_savesDeliverySuccessfully() {
        // Act
        deliveryService.createDeliveryForOrder(100L, "123 Food St", "456 Home Ave", "John", "Doe", "Pizza Place");

        // Assert
        ArgumentCaptor<Delivery> deliveryCaptor = ArgumentCaptor.forClass(Delivery.class);
        verify(deliveryRepository, times(1)).save(deliveryCaptor.capture());

        Delivery savedDelivery = deliveryCaptor.getValue();
        assertEquals(100L, savedDelivery.getOrderId());
        assertEquals("123 Food St", savedDelivery.getPickupAddress());
        assertEquals("456 Home Ave", savedDelivery.getDeliveryAddress());
        assertEquals(Delivery.DeliveryStatus.ASSIGNED, savedDelivery.getStatus());
        assertNotNull(savedDelivery.getDriverName());
        assertNotNull(savedDelivery.getAssignedAt());
    }

    @Test
    void getById_whenExists_returnsResponse() {
        // Arrange
        when(deliveryRepository.findById(1L)).thenReturn(Optional.of(sampleDelivery));

        // Act
        DeliveryResponse response = deliveryService.getById(1L);

        // Assert
        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals("ASSIGNED", response.getStatus());
    }

    @Test
    void getById_whenNotExists_throwsException() {
        // Arrange
        when(deliveryRepository.findById(99L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> deliveryService.getById(99L));
    }

    @Test
    void updateStatus_toDelivered_updatesTimestamps() {
        // Arrange
        when(deliveryRepository.findById(1L)).thenReturn(Optional.of(sampleDelivery));
        when(deliveryRepository.save(any(Delivery.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        DeliveryResponse response = deliveryService.updateStatus(1L, "DELIVERED");

        // Assert
        assertEquals("DELIVERED", response.getStatus());
        assertNotNull(sampleDelivery.getDeliveredAt());
        verify(deliveryRepository).save(sampleDelivery);
    }
}
