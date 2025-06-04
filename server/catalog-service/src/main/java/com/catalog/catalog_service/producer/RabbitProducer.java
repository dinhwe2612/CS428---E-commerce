package com.catalog.catalog_service.producer;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.catalog.catalog_service.config.RabbitMQConfig;
import com.catalog.catalog_service.event.OutOfStockEvent;
import com.catalog.catalog_service.event.ProductCreatedEvent;
import com.catalog.catalog_service.event.ProductDeletedEvent;
@Component
public class RabbitProducer {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    public void sendMessage(String exchange, String routingKey, Object message) {
        rabbitTemplate.convertAndSend(exchange, routingKey, message);
    }

    public void sendProductOutOfStockMessage(OutOfStockEvent outOfStockEvent) {
        rabbitTemplate.convertAndSend(RabbitMQConfig.PRODUCT_OUTOFSTOCK_EXCHANGE, RabbitMQConfig.PRODUCT_OUTOFSTOCK_ROUTING_KEY, outOfStockEvent);
    }
    public void sendProductCreatedEvent(ProductCreatedEvent productCreatedEvent) {
        rabbitTemplate.convertAndSend(RabbitMQConfig.PRODUCT_SYNC_EXCHANGE, "product.created", productCreatedEvent);
    }
    public void sendProductDeletedEvent(ProductDeletedEvent productDeletedEvent) {
        rabbitTemplate.convertAndSend(RabbitMQConfig.PRODUCT_SYNC_EXCHANGE, "product.deleted", productDeletedEvent);
    }
}
