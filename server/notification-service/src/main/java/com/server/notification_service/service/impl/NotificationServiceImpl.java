package com.server.notification_service.service.impl;

import com.server.notification_service.DTOs.NotificationMessage;
import com.server.notification_service.mapper.NotificationMapper;
import com.server.notification_service.repository.NotificationRepository;
import com.server.notification_service.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;

    private final NotificationMapper notificationMapper;

    @Override
    public void save(NotificationMessage notificationMessage) {
        notificationRepository.save(notificationMapper.mapToNotification(notificationMessage));
    }
}
