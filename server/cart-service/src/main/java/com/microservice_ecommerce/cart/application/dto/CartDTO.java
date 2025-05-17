package com.microservice_ecommerce.cart.application.dto;

import java.util.List;
import java.util.UUID;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CartDTO {
    private UUID id;
    private UUID userId;
    private List<CartItemDTO> items;
} 