package com.order.order_service.DTOs;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GuestOrderItemResponseDTO {
    private Long id;
    private String productId;
    private String productName;
    private Integer quantity;
    private Double unitPrice;
    private Double subtotal;
    private String inventoryId;
} 