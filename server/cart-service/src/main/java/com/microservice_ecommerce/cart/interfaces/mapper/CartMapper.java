package com.microservice_ecommerce.cart.interfaces.mapper;

import java.util.stream.Collectors;

import com.microservice_ecommerce.cart.application.dto.CartDTO;
import com.microservice_ecommerce.cart.application.dto.CartItemDTO;
import com.microservice_ecommerce.cart.interfaces.dto.request.AddItemRequestDTO;
import com.microservice_ecommerce.cart.interfaces.dto.response.CartItemResponseDTO;
import com.microservice_ecommerce.cart.interfaces.dto.response.CartResponseDTO;

import org.springframework.stereotype.Component;

@Component
public class CartMapper {
    
    public CartItemDTO toCartItemDTO(AddItemRequestDTO requestDTO) {
        return CartItemDTO.builder()
                .productId(requestDTO.getProductId())
                .quantity(requestDTO.getQuantity())
                .build();
    }
    
    public CartResponseDTO toCartResponseDTO(CartDTO cartDTO) {
        return CartResponseDTO.builder()
                .id(cartDTO.getId())
                .userId(cartDTO.getUserId())
                .items(cartDTO.getItems().stream()
                        .map(this::toCartItemResponseDTO)
                        .collect(Collectors.toList()))
                .build();
    }
    
    public CartItemResponseDTO toCartItemResponseDTO(CartItemDTO itemDTO) {
        return CartItemResponseDTO.builder()
                .id(itemDTO.getId())
                .productId(itemDTO.getProductId())
                .quantity(itemDTO.getQuantity())
                .build();
    }
} 