package com.server.notification_service.controller;

import com.server.notification_service.DTOs.ApiResponse;
import com.server.notification_service.DTOs.NotificationMessage;
import com.server.notification_service.model.Notification;
import com.server.notification_service.service.NotificationService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.*;

import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/notification")
@Tag(name = "Notification", description = "Endpoints for sending and retrieving notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;
    private final SimpMessagingTemplate simpMessagingTemplate;

    @Operation(
            summary = "Test protected notification endpoint",
            description = "Verifies that the current user has USER authority",
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Authorized",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = ApiResponse.class)))
            }
    )
    @GetMapping("/test")
    @PreAuthorize("hasAuthority('USER')")
    public ApiResponse<String> test() {
        return new ApiResponse<>(true, "test successfully", "you are authorized");
    }

    @Operation(
            summary = "Get notifications for a user",
            description = "Retrieves all notifications for the specified username",
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Notifications retrieved",
                            content = @Content(mediaType = "application/json",
                                    array = @ArraySchema(schema = @Schema(implementation = Notification.class))))
            }
    )
    @GetMapping("/user/{username}")
    @PreAuthorize("hasAuthority('ADMIN') or #username == authentication.name")
    public ApiResponse<List<Notification>> getUser(
            @Parameter(description = "Username to retrieve notifications for", required = true)
            @PathVariable String username
    ) {
        return new ApiResponse<>(
                true,
                "Get list of notifications successfully",
                notificationService.getNotificationsByUserName(username)
        );
    }

    @Operation(
            summary = "Send a notification to a user",
            description = "Saves and dispatches a notification message via WebSocket",
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Notification sent",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = ApiResponse.class)))
            }
    )
    @PostMapping("/send")
    public ApiResponse<String> sendNotification(
            @Parameter(description = "Notification payload", required = true)
            @RequestBody NotificationMessage notificationMessage
    ) {
        notificationService.save(notificationMessage);
        simpMessagingTemplate.convertAndSendToUser(
                notificationMessage.getUserName(),
                "/queue/notification",
                notificationMessage
        );
        return new ApiResponse<>(true, "Notification sent successfully", null);
    }
}
