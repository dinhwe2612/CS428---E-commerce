package com.payment.payment_service.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.payment.payment_service.model.PaymentMethod;
import com.payment.payment_service.model.PaymentStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentResponseDTO {
    
    private Long id;
    
    @JsonProperty("transaction_id")
    private String transactionId;
    
    @JsonProperty("order_id")
    private Long orderId;
    
    @JsonProperty("user_id")
    private String userId;
    
    private BigDecimal amount;
    
    @JsonProperty("payment_method")
    private PaymentMethod paymentMethod;
    
    private PaymentStatus status;
    
    private String currency;
    
    private String description;
    
    @JsonProperty("payment_gateway")
    private String paymentGateway;
    
    @JsonProperty("gateway_transaction_id")
    private String gatewayTransactionId;
    
    @JsonProperty("payment_url")
    private String paymentUrl;
    
    @JsonProperty("created_at")
    private LocalDateTime createdAt;
    
    @JsonProperty("updated_at")
    private LocalDateTime updatedAt;
    
    @JsonProperty("completed_at")
    private LocalDateTime completedAt;
} 