package com.fooddelivery.delivery_service.repository;

import com.fooddelivery.delivery_service.model.Delivery;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface DeliveryRepository extends JpaRepository<Delivery, Long> {
    Optional<Delivery> findByOrderId(Long orderId);
    List<Delivery> findByStatus(Delivery.DeliveryStatus status);
    List<Delivery> findByDriverNameIgnoreCase(String driverName);
}

