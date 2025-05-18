package com.microservice_ecommerce.cart.domain.service;

import com.microservice_ecommerce.cart.domain.model.Cart;
import com.microservice_ecommerce.cart.domain.model.CartItem;

import java.util.UUID;

public interface CartDomainService {
    Cart createCart(Long userId);
    Cart addItemToCart(Cart cart, CartItem item);
    Cart removeItemFromCart(Cart cart, UUID itemId);
    void clearCart(Cart cart);
} 