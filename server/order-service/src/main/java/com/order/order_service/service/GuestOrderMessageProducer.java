package com.order.order_service.service;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.order.order_service.DTOs.GuestOrderResponseDTO;
import com.order.order_service.config.RabbitMQConfig;

@Service
public class GuestOrderMessageProducer {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    public void sendGuestOrderCreatedEvent(GuestOrderResponseDTO order) {
        rabbitTemplate.convertAndSend(
            RabbitMQConfig.ORDER_EXCHANGE,
            RabbitMQConfig.ORDER_CREATED_ROUTING_KEY,
            order
        );
    }

    public void sendGuestOrderStatusUpdatedEvent(GuestOrderResponseDTO order) {
        rabbitTemplate.convertAndSend(
            RabbitMQConfig.ORDER_EXCHANGE,
            RabbitMQConfig.ORDER_STATUS_UPDATED_ROUTING_KEY,
            order
        );
    }
} 