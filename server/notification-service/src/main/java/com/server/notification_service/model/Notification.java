package com.server.notification_service.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Builder
@Entity
@Table(name = "notification")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Notification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String userName;

    private String title;

    private String content;

    @Builder.Default
    private Boolean isRead = false;

    private String link;

    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();
}
