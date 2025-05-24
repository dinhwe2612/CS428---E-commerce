package com.server.notification_service.listener;

import com.server.notification_service.DTOs.NotificationMessage;
import com.server.notification_service.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class NotificationListener {

    private final SimpMessagingTemplate simpMessagingTemplate;

    private final NotificationService notificationService;

    @RabbitListener(queues = "notification.queue")
    public void receiveMessage(NotificationMessage message) {
        simpMessagingTemplate.convertAndSend("/topic/notification", message);
        notificationService.save(message);
    }
}
