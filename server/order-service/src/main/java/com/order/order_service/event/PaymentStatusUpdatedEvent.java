package com.order.order_service.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentStatusUpdatedEvent {
    private String transactionId;
    private Long orderId;
    private String userId;
    private String oldStatus;
    private String newStatus;
    private LocalDateTime completedAt;
} 