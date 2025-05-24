package com.server.notification_service.service;

import com.server.notification_service.DTOs.NotificationMessage;

public interface NotificationService {
    void save(NotificationMessage notificationMessage);
}
