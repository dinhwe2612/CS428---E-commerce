package com.order.order_service.DTOs;

import java.time.LocalDateTime;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderResponseDTO {
    private Long id;
    private String userId;
    private String status;
    private Double totalAmount;
    private String shippingAddress;
    private String paymentMethod;
    private List<OrderItemResponseDTO> orderItems;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String message;
} 