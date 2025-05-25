package com.server.notification_service.controller;

import com.server.notification_service.DTOs.ApiResponse;
import com.server.notification_service.DTOs.ReadNotificationRequest;
import com.server.notification_service.exception.NotificationNotFoundException;
import com.server.notification_service.listener.NotificationListener;
import com.server.notification_service.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageExceptionHandler;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.security.Principal;

@Controller
@RequiredArgsConstructor
public class NotificationWSController {

    private final SimpMessagingTemplate simpMessagingTemplate;

    private final NotificationService notificationService;

    @MessageMapping("/notification/read")
    public void markNotificationAsRead(ReadNotificationRequest readNotificationRequest, Principal principal) {
        notificationService.markNotificationAsRead(readNotificationRequest);
    }

    @MessageExceptionHandler(NotificationNotFoundException.class)
    public String handleNotificationNotFoundException(NotificationNotFoundException e) {
        return e.getMessage();
    }
}
