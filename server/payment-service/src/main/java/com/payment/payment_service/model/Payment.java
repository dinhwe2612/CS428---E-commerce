package com.payment.payment_service.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "payments")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Payment {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, unique = true)
    private String transactionId;
    
    @Column(nullable = false)
    private Long orderId;
    
    @Column
    private String userId;
    
    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal amount;
    
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private PaymentMethod paymentMethod;
    
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private PaymentStatus status;
    
    @Column(nullable = false)
    private String currency;
    
    @Column
    private String description;
    
    @Column
    private String paymentGateway;
    
    @Column
    private String gatewayTransactionId;
    
    @Column
    private String gatewayResponse;
    
    @Column
    private LocalDateTime createdAt;
    
    @Column
    private LocalDateTime updatedAt;
    
    @Column
    private LocalDateTime completedAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        if (currency == null) {
            currency = "VND";
        }
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
        if (status == PaymentStatus.COMPLETED && completedAt == null) {
            completedAt = LocalDateTime.now();
        }
    }
} 