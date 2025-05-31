package com.order.order_service.DTOs;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderItemRequestDTO {
    @JsonProperty("product_id")
    private String productId;
    
    private Integer quantity;

    @JsonProperty("inventory_id")
    private String inventoryId;
} 