package com.microservice_ecommerce.cart.interfaces.dto.response;

import java.util.List;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CartResponseDTO {
    private UUID id;
    private Long userId;
    private List<CartItemResponseDTO> items;
} 