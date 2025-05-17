package com.microservice_ecommerce.cart.domain.service.impl;

import java.util.UUID;

import org.springframework.stereotype.Service;

import com.microservice_ecommerce.cart.domain.model.Cart;
import com.microservice_ecommerce.cart.domain.model.CartItem;
import com.microservice_ecommerce.cart.domain.service.CartDomainService;

@Service
public class CartDomainServiceImpl implements CartDomainService {
    
    public CartDomainServiceImpl() {
    }

    @Override
    public Cart createCart(UUID userId) {
        Cart cart = Cart.builder()
            .userId(userId)
            .build();
        return cart;
    }

    @Override
    public Cart addItemToCart(Cart cart, CartItem item) {
        cart.addItem(item);
        return cart;
    }

    @Override
    public Cart removeItemFromCart(Cart cart, UUID itemId) {
        cart.removeItem(itemId);
        return cart;
    }

    @Override
    public void clearCart(Cart cart) {
        cart.clear();
    }
} 