package com.microservice_ecommerce.cart.application.dto;

import java.util.UUID;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CartItemDTO {
    private UUID id;
    private Long productId;
    private int quantity;
} 