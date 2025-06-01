package com.order.order_service.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.amqp.support.converter.MessageConverter;


@Configuration
public class RabbitMQConfig {

    public static final String ORDER_EXCHANGE = "order.exchange";
    public static final String ORDER_CREATED_QUEUE = "order.created.queue";
    public static final String ORDER_STATUS_UPDATED_QUEUE = "order.status.updated.queue";
    public static final String ORDER_CREATED_ROUTING_KEY = "order.created";
    public static final String ORDER_STATUS_UPDATED_ROUTING_KEY = "order.status.updated";

    public static final String PAYMENT_EXCHANGE = "payment.exchange";
    public static final String PAYMENT_CREATED_QUEUE = "payment.created.queue";
    public static final String ORDER_PAYMENT_QUEUE = "order.payment.queue";
    public static final String PAYMENT_CREATED_ROUTING_KEY = "payment.created";
    public static final String ORDER_PAYMENT_ROUTING_KEY = "order.payment";

    public static final String PRODUCT_OUTOFSTOCK_QUEUE = "product.outofstock.queue";

    @Bean
    public DirectExchange orderExchange() {
        return new DirectExchange(ORDER_EXCHANGE);
    }

    @Bean
    public DirectExchange paymentExchange() {
        return new DirectExchange(PAYMENT_EXCHANGE);
    }

    @Bean
    public Queue orderCreatedQueue() {
        return new Queue(ORDER_CREATED_QUEUE);
    }

    @Bean
    public Queue orderStatusUpdatedQueue() {
        return new Queue(ORDER_STATUS_UPDATED_QUEUE);
    }

    @Bean
    public Queue paymentCreatedQueue() {
        return new Queue(PAYMENT_CREATED_QUEUE);
    }

    @Bean
    public Queue orderPaymentQueue() {
        return new Queue(ORDER_PAYMENT_QUEUE);
    }

    // Declare the product out of stock queue to prevent startup failures
    @Bean
    public Queue productOutOfStockQueue() {
        return new Queue(PRODUCT_OUTOFSTOCK_QUEUE, true); // durable
    }

    @Bean
    public Binding orderCreatedBinding() {
        return BindingBuilder
                .bind(orderCreatedQueue())
                .to(orderExchange())
                .with(ORDER_CREATED_ROUTING_KEY);
    }

    @Bean
    public Binding orderStatusUpdatedBinding() {
        return BindingBuilder
                .bind(orderStatusUpdatedQueue())
                .to(orderExchange())
                .with(ORDER_STATUS_UPDATED_ROUTING_KEY);
    }

    @Bean
    public Binding paymentCreatedBinding() {
        return BindingBuilder
                .bind(paymentCreatedQueue())
                .to(paymentExchange())
                .with(PAYMENT_CREATED_ROUTING_KEY);
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