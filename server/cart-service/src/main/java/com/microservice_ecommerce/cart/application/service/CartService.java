package com.microservice_ecommerce.cart.application.service;

import com.microservice_ecommerce.cart.application.dto.CartDTO;
import com.microservice_ecommerce.cart.application.dto.CartItemDTO;

import java.util.UUID;

public interface CartService {
    CartDTO createCart(UUID userId);
    CartDTO addItemToCart(UUID cartId, CartItemDTO itemDTO);
    CartDTO removeItemFromCart(UUID cartId, UUID itemId);
    CartDTO getCart(UUID cartId);
    void clearCart(UUID cartId);
} 