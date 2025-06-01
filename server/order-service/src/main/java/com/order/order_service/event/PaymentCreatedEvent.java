package com.order.order_service.event;

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
    private String paymentMethod;
    private String status;
    private String paymentUrl;
} 