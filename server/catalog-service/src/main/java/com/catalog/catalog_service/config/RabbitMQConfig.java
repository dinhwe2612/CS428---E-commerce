package com.catalog.catalog_service.config;

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

    public static final String PRODUCT_OUTOFSTOCK_QUEUE = "product.outofstock.queue";
    public static final String PRODUCT_OUTOFSTOCK_EXCHANGE = "product.outofstock.exchange";
    public static final String PRODUCT_OUTOFSTOCK_ROUTING_KEY = "product.outofstock.routingkey";

    @Bean
    public Queue productOutOfStockQueue() {
        return new Queue(PRODUCT_OUTOFSTOCK_QUEUE, true);
    }

 
    @Bean
    public DirectExchange productOutOfStockExchange() {
        return new DirectExchange(PRODUCT_OUTOFSTOCK_EXCHANGE);
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