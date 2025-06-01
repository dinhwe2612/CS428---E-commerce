package com.payment.payment_service.event;

import com.payment.payment_service.model.PaymentMethod;
import com.payment.payment_service.model.PaymentStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentCreatedEvent {
    private String transactionId;
    private Long orderId;
    private String userId;
    private BigDecimal amount;
    private PaymentMethod paymentMethod;
    private PaymentStatus status;
    private String paymentUrl;
} 