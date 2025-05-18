package com.catalog.catalog_service.RabbitMessage;

import java.time.LocalDateTime;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class OrderCreatedMessage {
    private Long id;
    private String userId;
    private String status;
    private Double totalAmount;
    private String shippingAddress;
    private String paymentMethod;
    private List<OrderItemResponseDTO> orderItems;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}


