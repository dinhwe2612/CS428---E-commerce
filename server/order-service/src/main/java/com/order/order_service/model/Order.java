package com.order.order_service.model;

import java.time.LocalDateTime;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import io.swagger.v3.oas.annotations.media.Schema;

@Entity
@Table(name = "orders")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Order entity representing a customer order")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "Unique identifier for the order", example = "1")
    private Long id;

    @Column(nullable = false)
    @Schema(description = "User ID who placed the order", example = "user123")
    private String userId;

    @Column(nullable = false)
    @Schema(description = "Order status", example = "PENDING", allowableValues = {"PENDING", "CONFIRMED", "DELIVERED", "CANCELLED"})
    private String status; 

    @Column(nullable = false)
    @Schema(description = "Total amount of the order", example = "99.99")
    private Double totalAmount;

    @Column(nullable = false)
    @Schema(description = "Shipping address for the order", example = "123 Main St, City, Country")
    private String shippingAddress;

    @Column(nullable = false)
    @Schema(description = "Payment method used", example = "CREDIT_CARD")
    private String paymentMethod;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
    @Schema(description = "List of items in the order")
    private List<OrderItem> orderItems;

    @Column(nullable = false)
    @Schema(description = "Order creation timestamp", example = "2023-12-01T10:30:00")
    private LocalDateTime createdAt;

    @Column
    @Schema(description = "Order last update timestamp", example = "2023-12-01T11:00:00")
    private LocalDateTime updatedAt;

    @Column
    @Schema(description = "Additional message or notes", example = "Special delivery instructions")
    private String message;

    @Column
    @Schema(description = "Payment transaction ID", example = "txn_123456789")
    private String transactionId;

    @Column
    @Schema(description = "Payment URL for external payment processing", example = "https://payment.gateway.com/pay/123")
    private String paymentUrl;

    @Column
    @Schema(description = "Delivery status", example = "IN_TRANSIT")
    private String deliveryStatus;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
} 