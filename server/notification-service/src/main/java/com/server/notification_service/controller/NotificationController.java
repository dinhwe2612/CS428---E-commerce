package com.server.notification_service.controller;

import com.server.notification_service.DTOs.ApiResponse;
import com.server.notification_service.DTOs.NotificationMessage;
import com.server.notification_service.model.Notification;
import com.server.notification_service.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/notification")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    private final SimpMessagingTemplate simpMessagingTemplate;

    @GetMapping("/test")
    @PreAuthorize("hasAuthority('USER')")
    public ApiResponse<String> test() {
        return new ApiResponse<>(true, "test successfully", "you are authorized");
    }

    @GetMapping("/user/{username}")
    @PreAuthorize("hasAuthority('ADMIN') or #username == authentication.name")
    public ApiResponse<List<Notification>> getUser(@PathVariable String username) {
        return new ApiResponse<>(
                true,
                "Get list of notifications successfully",
                notificationService.getNotificationsByUserName(username)
        );
    }

    @PostMapping("/send")
    public ApiResponse<String> sendNotification(@RequestBody NotificationMessage notificationMessage) {
        notificationService.save(notificationMessage);
        simpMessagingTemplate.convertAndSendToUser(
                notificationMessage.getUserName(),
                "/queue/notification",
                notificationMessage
        );
        return new ApiResponse<>(
                true,
                "Notification sent successfully",
                null
        );
    }
}