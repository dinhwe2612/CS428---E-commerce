package com.order.order_service.DTOs;

import java.time.LocalDateTime;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GuestOrderResponseDTO {
    private Long id;
    private String customerName;
    private String customerEmail;
    private String customerPhone;
    private String status;
    private Double totalAmount;
    private String shippingAddress;
    private String paymentMethod;
    private List<GuestOrderItemResponseDTO> orderItems;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String message;
    private String transactionId;
    private String paymentUrl;
    private String deliveryStatus;
} 