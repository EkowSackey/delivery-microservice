package com.fooddelivery.delivery_service.publisher;

import com.fooddelivery.delivery_service.config.RabbitMQConfig;
import com.fooddelivery.delivery_service.dto.DeliveryStatusEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Service
public class DeliveryEventPublisher {

    private static final Logger log = LoggerFactory.getLogger(DeliveryEventPublisher.class);
    private final RabbitTemplate rabbitTemplate;

    public DeliveryEventPublisher(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void publishDeliveryStatusEvent(DeliveryStatusEvent event) {
        log.info("Publishing DeliveryStatusEvent for order id: {} with status: {}", 
                 event.getOrderId(), event.getStatus());
        rabbitTemplate.convertAndSend(RabbitMQConfig.EXCHANGE, "delivery.status.updated", event);
    }
}
