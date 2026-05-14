package com.fooddelivery.delivery_service.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.config.RetryInterceptorBuilder;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.retry.RejectAndDontRequeueRecoverer;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    public static final String EXCHANGE     = "food-delivery-exchange";
    public static final String DLX          = "food-delivery-dlx";

    public static final String DELIVERY_QUEUE           = "delivery-service-queue";
    public static final String DELIVERY_QUEUE_DLQ       = "delivery-service-queue.dlq";
    public static final String DELIVERY_ROUTING_KEY     = "order.placed";

    public static final String DELIVERY_CANCEL_QUEUE    = "delivery-cancel-queue";
    public static final String DELIVERY_CANCEL_QUEUE_DLQ = "delivery-cancel-queue.dlq";
    public static final String DELIVERY_CANCEL_ROUTING_KEY = "order.cancelled";

    // ── Main exchange ────────────────────────────────────────────────────────

    @Bean
    public TopicExchange exchange() {
        return new TopicExchange(EXCHANGE);
    }

    // ── Dead-letter exchange + queues ────────────────────────────────────────

    @Bean
    public DirectExchange deadLetterExchange() {
        return new DirectExchange(DLX);
    }

    @Bean
    public Queue deliveryQueueDlq() {
        return QueueBuilder.durable(DELIVERY_QUEUE_DLQ).build();
    }

    @Bean
    public Queue deliveryCancelQueueDlq() {
        return QueueBuilder.durable(DELIVERY_CANCEL_QUEUE_DLQ).build();
    }

    @Bean
    public Binding deliveryDlqBinding() {
        return BindingBuilder.bind(deliveryQueueDlq()).to(deadLetterExchange()).with(DELIVERY_QUEUE);
    }

    @Bean
    public Binding deliveryCancelDlqBinding() {
        return BindingBuilder.bind(deliveryCancelQueueDlq()).to(deadLetterExchange()).with(DELIVERY_CANCEL_QUEUE);
    }

    // ── Main queues (wired to DLX on rejection) ──────────────────────────────

    @Bean
    public Queue deliveryQueue() {
        return QueueBuilder.durable(DELIVERY_QUEUE)
                .deadLetterExchange(DLX)
                .deadLetterRoutingKey(DELIVERY_QUEUE)
                .build();
    }

    @Bean
    public Queue deliveryCancelQueue() {
        return QueueBuilder.durable(DELIVERY_CANCEL_QUEUE)
                .deadLetterExchange(DLX)
                .deadLetterRoutingKey(DELIVERY_CANCEL_QUEUE)
                .build();
    }

    @Bean
    public Binding binding(Queue deliveryQueue, TopicExchange exchange) {
        return BindingBuilder.bind(deliveryQueue).to(exchange).with(DELIVERY_ROUTING_KEY);
    }

    @Bean
    public Binding cancelBinding(Queue deliveryCancelQueue, TopicExchange exchange) {
        return BindingBuilder.bind(deliveryCancelQueue).to(exchange).with(DELIVERY_CANCEL_ROUTING_KEY);
    }

    // ── Messaging infrastructure ─────────────────────────────────────────────

    @Bean
    public MessageConverter converter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public AmqpTemplate template(ConnectionFactory connectionFactory) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(converter());
        return rabbitTemplate;
    }

    // ── Listener container factory with retry + dead-lettering ───────────────

    @Bean
    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(
            ConnectionFactory connectionFactory) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setMessageConverter(converter());
        factory.setAdviceChain(RetryInterceptorBuilder.stateless()
                .maxAttempts(3)
                .backOffOptions(1_000, 2.0, 10_000)
                .recoverer(new RejectAndDontRequeueRecoverer())
                .build());
        return factory;
    }
}
