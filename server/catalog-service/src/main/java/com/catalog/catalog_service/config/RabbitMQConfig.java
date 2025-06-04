package com.catalog.catalog_service.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    public static final String PRODUCT_OUTOFSTOCK_QUEUE = "product.outofstock.queue";
    public static final String PRODUCT_OUTOFSTOCK_EXCHANGE = "product.outofstock.exchange";
    public static final String PRODUCT_OUTOFSTOCK_ROUTING_KEY = "product.outofstock.routingkey";
    public static final String PRODUCT_SYNC_QUEUE = "product.sync.queue";
    public static final String PRODUCT_SYNC_EXCHANGE = "product.sync.exchange";
    public static final String PRODUCT_SYNC_ROUTING_KEY = "product.*";

    @Bean
    public Queue productOutOfStockQueue() {
        return new Queue(PRODUCT_OUTOFSTOCK_QUEUE, true);
    }
    @Bean
    public Queue productSyncQueue() {
        return new Queue(PRODUCT_SYNC_QUEUE, true);
    }
    @Bean
    public TopicExchange productSyncExchange() {
        return new TopicExchange(PRODUCT_SYNC_EXCHANGE);
    }


 
    @Bean
    public DirectExchange productOutOfStockExchange() {
        return new DirectExchange(PRODUCT_OUTOFSTOCK_EXCHANGE);
    }

    @Bean
    public Binding productSyncBinding() {
        return BindingBuilder
                .bind(productSyncQueue())
                .to(productSyncExchange())
                .with(PRODUCT_SYNC_ROUTING_KEY);
    }
    @Bean
    public Binding productOutOfStockBinding() {
        return BindingBuilder
                .bind(productOutOfStockQueue())
                .to(productOutOfStockExchange())
                .with(PRODUCT_OUTOFSTOCK_ROUTING_KEY);
    }
    @Bean
    public Queue orderCreatedQueue() {
        return new Queue("order.created.queue", true); // durable
    }
    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

   
    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(jsonMessageConverter());
        return rabbitTemplate;
    }
} 