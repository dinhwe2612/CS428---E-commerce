package com.microservice_ecommerce.auth.publisher;

import org.springframework.amqp.rabbit.core.RabbitTemplate; 
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.microservice_ecommerce.auth.DTOs.UserCreatedMessage;
import com.microservice_ecommerce.auth.config.RabbitMqConfig;

@Component
public class UserCreatedPublisher {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    public void publishUserCreated(UserCreatedMessage userCreatedMessage) {
     
        rabbitTemplate.convertAndSend(RabbitMqConfig.EXCHANGE_NAME, RabbitMqConfig.ROUTING_KEY, userCreatedMessage);
    }


}
