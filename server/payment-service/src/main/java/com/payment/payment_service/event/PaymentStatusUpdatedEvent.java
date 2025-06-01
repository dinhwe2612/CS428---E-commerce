package com.payment.payment_service.event;

import com.payment.payment_service.model.PaymentStatus;
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
    private PaymentStatus oldStatus;
    private PaymentStatus newStatus;
    private LocalDateTime completedAt;
} 