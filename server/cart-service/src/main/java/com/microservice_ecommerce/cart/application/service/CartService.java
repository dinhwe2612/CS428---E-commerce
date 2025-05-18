package com.microservice_ecommerce.cart.application.service;

import com.microservice_ecommerce.cart.application.dto.CartDTO;
import com.microservice_ecommerce.cart.application.dto.CartItemDTO;

import java.util.UUID;

public interface CartService {
    CartDTO addItemToCart(CartItemDTO itemDTO);
    CartDTO removeItemFromCart(UUID itemId);
    CartDTO getCart();
    void clearCart();
} 