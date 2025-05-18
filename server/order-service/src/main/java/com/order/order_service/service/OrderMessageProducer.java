package com.order.order_service.service;

import com.order.order_service.DTOs.OrderResponseDTO;
import com.order.order_service.config.RabbitMQConfig;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class OrderMessageProducer {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    public void sendOrderCreatedEvent(OrderResponseDTO order) {
        rabbitTemplate.convertAndSend(
            RabbitMQConfig.ORDER_EXCHANGE,
            RabbitMQConfig.ORDER_CREATED_ROUTING_KEY,
            order
        );
    }

    public void sendOrderStatusUpdatedEvent(OrderResponseDTO order) {
        rabbitTemplate.convertAndSend(
            RabbitMQConfig.ORDER_EXCHANGE,
            RabbitMQConfig.ORDER_STATUS_UPDATED_ROUTING_KEY,
            order
        );
    }
} 