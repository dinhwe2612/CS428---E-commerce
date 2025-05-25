package com.server.notification_service.mapper;

import com.server.notification_service.DTOs.NotificationMessage;
import com.server.notification_service.model.Notification;
import org.springframework.stereotype.Component;

@Component
public class NotificationMapper {

    public Notification mapToNotification(NotificationMessage notificationMessage) {
        return Notification.builder()
                .userName(notificationMessage.getUserName())
                .title(notificationMessage.getTitle())
                .content(notificationMessage.getContent())
                .link(notificationMessage.getLink())
                .build();
    }

}
