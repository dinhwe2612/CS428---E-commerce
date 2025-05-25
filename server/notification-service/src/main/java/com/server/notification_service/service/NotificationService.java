package com.server.notification_service.service;

import com.server.notification_service.DTOs.NotificationMessage;
import com.server.notification_service.DTOs.ReadNotificationRequest;
import com.server.notification_service.model.Notification;

import java.util.List;

public interface NotificationService {
    void save(NotificationMessage notificationMessage);
    List<Notification> getNotificationsByUserName(String userName);
    void markNotificationAsRead(ReadNotificationRequest readNotificationRequest);
}
