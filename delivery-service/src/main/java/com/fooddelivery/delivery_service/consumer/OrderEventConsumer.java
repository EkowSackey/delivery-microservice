package com.fooddelivery.delivery_service.consumer;

import com.fooddelivery.delivery_service.config.RabbitMQConfig;
import com.fooddelivery.delivery_service.dto.OrderPlacedEvent;
import com.fooddelivery.delivery_service.service.DeliveryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Service
public class OrderEventConsumer {

    private static final Logger log = LoggerFactory.getLogger(OrderEventConsumer.class);
    private final DeliveryService deliveryService;

    public OrderEventConsumer(DeliveryService deliveryService) {
        this.deliveryService = deliveryService;
    }

    @RabbitListener(queues = RabbitMQConfig.DELIVERY_QUEUE)
    public void consumeOrderEvent(Object event) {
        if (event instanceof OrderPlacedEvent placedEvent) {
            log.info("Received OrderPlacedEvent for order id: {}", placedEvent.getOrderId());
            deliveryService.createDeliveryForOrder(
                    placedEvent.getOrderId(),
                    placedEvent.getPickupAddress(),
                    placedEvent.getDeliveryAddress(),
                    placedEvent.getCustomerFirstName(),
                    placedEvent.getCustomerLastName(),
                    placedEvent.getRestaurantName()
            );
        } else if (event instanceof Long orderId) {
             log.info("Received OrderCancelledEvent for order id: {}", orderId);
             // We will implement cancel logic in the next step
        }
    }
}
