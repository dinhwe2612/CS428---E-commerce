package com.server.notification_service.service.impl;

import com.server.notification_service.DTOs.NotificationMessage;
import com.server.notification_service.DTOs.ReadNotificationRequest;
import com.server.notification_service.exception.NotificationNotFoundException;
import com.server.notification_service.mapper.NotificationMapper;
import com.server.notification_service.model.Notification;
import com.server.notification_service.repository.NotificationRepository;
import com.server.notification_service.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;

    private final NotificationMapper notificationMapper;

    @Override
    public void save(NotificationMessage notificationMessage) {
        notificationRepository.save(notificationMapper.mapToNotification(notificationMessage));
    }

    @Override
    public List<Notification> getNotificationsByUserName(String userName) {
        return notificationRepository.findNotificationsByUserName(userName);
    }

    @Override
    public void markNotificationAsRead(ReadNotificationRequest readNotificationRequest) {
        Notification notification = notificationRepository.findById(readNotificationRequest.getNotificationId())
                .orElseThrow(() -> new NotificationNotFoundException("Notification with ID " + readNotificationRequest.getNotificationId() + " not found"));
        notification.setIsRead(true);
        notificationRepository.save(notification);
    }
}
