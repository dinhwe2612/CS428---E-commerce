package com.server.user_service.listener;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import com.server.user_service.DTOs.UserCreatedMessage;
import com.server.user_service.model.Role;
import com.server.user_service.model.User;
import com.server.user_service.service.UserService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserSagaListener {

    private final UserService userService;

    @RabbitListener(queues = "user.created.queue")
    public void handleUserCreated(UserCreatedMessage userCreatedMessage) {
       
        
        try {
            log.info("Received user created event: {}", userCreatedMessage);
            Role role = Role.valueOf(userCreatedMessage.getRole());
            User user = new User();
            user.setId(userCreatedMessage.getId());
            user.setUsername(userCreatedMessage.getUsername());
            user.setEmail(userCreatedMessage.getEmail());
            user.setPassword(userCreatedMessage.getPassword());
            user.setRole(role);
            userService.createUser(user);
            log.info("Successfully created user: {}", user.getUsername());
        } catch (Exception e) {
            log.error("Error processing user creation event: {}", e.getMessage(), e);
           
            throw e;
        }
    }


} 