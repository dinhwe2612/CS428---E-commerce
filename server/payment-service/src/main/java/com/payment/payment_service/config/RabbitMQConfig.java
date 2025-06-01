package com.payment.payment_service.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    // Payment exchanges and queues
    public static final String PAYMENT_EXCHANGE = "payment.exchange";
    public static final String PAYMENT_CREATED_QUEUE = "payment.created.queue";
    public static final String PAYMENT_STATUS_UPDATED_QUEUE = "payment.status.updated.queue";
    public static final String PAYMENT_CREATED_ROUTING_KEY = "payment.created";
    public static final String PAYMENT_STATUS_UPDATED_ROUTING_KEY = "payment.status.updated";

    // Order integration
    public static final String ORDER_PAYMENT_QUEUE = "order.payment.queue";
    public static final String ORDER_PAYMENT_ROUTING_KEY = "order.payment";

    @Bean
    public DirectExchange paymentExchange() {
        return new DirectExchange(PAYMENT_EXCHANGE);
    }

    @Bean
    public Queue paymentCreatedQueue() {
        return new Queue(PAYMENT_CREATED_QUEUE);
    }

    @Bean
    public Queue paymentStatusUpdatedQueue() {
        return new Queue(PAYMENT_STATUS_UPDATED_QUEUE);
    }

    @Bean
    public Queue orderPaymentQueue() {
        return new Queue(ORDER_PAYMENT_QUEUE);
    }

    @Bean
    public Binding paymentCreatedBinding() {
        return BindingBuilder
                .bind(paymentCreatedQueue())
                .to(paymentExchange())
                .with(PAYMENT_CREATED_ROUTING_KEY);
    }

    @Bean
    public Binding paymentStatusUpdatedBinding() {
        return BindingBuilder
                .bind(paymentStatusUpdatedQueue())
                .to(paymentExchange())
                .with(PAYMENT_STATUS_UPDATED_ROUTING_KEY);
    }

    @Bean
    public Binding orderPaymentBinding() {
        return BindingBuilder
                .bind(orderPaymentQueue())
                .to(paymentExchange())
                .with(ORDER_PAYMENT_ROUTING_KEY);
    }

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(jsonMessageConverter());
        return template;
    }
} 