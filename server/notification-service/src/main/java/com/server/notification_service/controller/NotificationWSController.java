package com.server.notification_service.controller;

import com.server.notification_service.DTOs.ApiResponse;
import com.server.notification_service.DTOs.ReadNotificationRequest;
import com.server.notification_service.exception.NotificationNotFoundException;
import com.server.notification_service.service.NotificationService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;

import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageExceptionHandler;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.security.Principal;

@Controller
@Tag(name = "Notification WS", description = "WebSocket endpoints for notification operations")
@RequiredArgsConstructor
public class NotificationWSController {

    private final SimpMessagingTemplate simpMessagingTemplate;
    private final NotificationService notificationService;

    @Operation(
            summary = "Mark notification as read",
            description = "Marks the specified notification as read for the authenticated user",
            requestBody = @RequestBody(
                    description = "Notification read request",
                    required = true,
                    content = @Content(
                            schema = @Schema(implementation = ReadNotificationRequest.class)
                    )
            )
    )
    @MessageMapping("/notification/read")
    public void markNotificationAsRead(
            ReadNotificationRequest readNotificationRequest,
            Principal principal
    ) {
        notificationService.markNotificationAsRead(readNotificationRequest);
        // Optionally notify user of success
        simpMessagingTemplate.convertAndSendToUser(
                principal.getName(),
                "/queue/notification/read-confirmation",
                new ApiResponse<>(true, "Notification marked as read", null)
        );
    }

    @Operation(
            summary = "Handle notification not found error",
            description = "Handles errors when a notification cannot be found"
    )
    @MessageExceptionHandler(NotificationNotFoundException.class)
    public String handleNotificationNotFoundException(NotificationNotFoundException e) {
        return e.getMessage();
    }
}
