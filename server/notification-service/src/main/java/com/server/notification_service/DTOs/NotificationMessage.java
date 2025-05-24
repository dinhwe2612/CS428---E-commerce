package com.server.notification_service.DTOs;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class NotificationMessage {
    private String title;
    private String content;
    private String link;
    private Long userId;
}