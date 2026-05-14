package com.fooddelivery.delivery_service.consumer;

import com.fooddelivery.delivery_service.config.RabbitMQConfig;
import com.fooddelivery.delivery_service.dto.OrderCancelledEvent;
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
    public void consumeOrderPlacedEvent(OrderPlacedEvent event) {
        log.info("Received OrderPlacedEvent for order id: {}", event.getOrderId());
        deliveryService.createDeliveryForOrder(
                event.getOrderId(),
                event.getPickupAddress(),
                event.getDeliveryAddress(),
                event.getCustomerFirstName(),
                event.getCustomerLastName(),
                event.getRestaurantName()
        );
    }

    @RabbitListener(queues = RabbitMQConfig.DELIVERY_CANCEL_QUEUE)
    public void consumeOrderCancelledEvent(OrderCancelledEvent event) {
        log.info("Received OrderCancelledEvent for order id: {}", event.getOrderId());
        deliveryService.cancelDeliveryByOrderId(event.getOrderId());
    }
}
