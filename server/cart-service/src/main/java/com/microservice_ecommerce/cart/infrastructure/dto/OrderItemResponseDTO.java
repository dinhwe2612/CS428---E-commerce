package com.microservice_ecommerce.cart.infrastructure.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderItemResponseDTO {
    private Long id;
    private String product_id;
    private String inventory_id;
    private String product_name;
    private Integer quantity;
    private Double unit_price;
    private Double subtotal;
} 