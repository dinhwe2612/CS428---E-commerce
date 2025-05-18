package com.microservice_ecommerce.cart.application.service.impl;

import java.util.UUID;

import org.springframework.stereotype.Service;

import com.microservice_ecommerce.cart.application.service.CartService;
import com.microservice_ecommerce.cart.domain.exception.CartNotFoundException;
import com.microservice_ecommerce.cart.domain.exception.ProductNotFoundException;
import com.microservice_ecommerce.cart.domain.exception.ProductUnavailableException;
import com.microservice_ecommerce.cart.domain.model.Cart;
import com.microservice_ecommerce.cart.domain.model.CartItem;
import com.microservice_ecommerce.cart.domain.service.CartDomainService;
import com.microservice_ecommerce.cart.security.JwtService;
import com.microservice_ecommerce.integration.catalog.client.CatalogServiceClient;
import com.microservice_ecommerce.integration.catalog.dto.response.ProductResponse;
import com.microservice_ecommerce.cart.infrastructure.repository.CartRepository;
import com.microservice_ecommerce.cart.application.dto.*;

import java.util.List;
import java.util.stream.Collectors;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

@Service
@RequiredArgsConstructor
public class CartServiceImpl implements CartService {
    
    private final CartRepository cartRepository;
    private final CartDomainService cartDomainService;
    private final CatalogServiceClient catalogClient;
    private final JwtService jwtService;

    private CartDTO convertToDTO(Cart cart) {
        List<CartItemDTO> items = cart.getItems().stream()
            .map(this::convertToCartItemDTO)
            .collect(Collectors.toList());
            
        return CartDTO.builder()
            .id(cart.getId())
            .userId(cart.getUserId())
            .items(items)
            .build();
    }
    
    private CartItemDTO convertToCartItemDTO(CartItem item) {
        return CartItemDTO.builder()
            .id(item.getId())
            .productId(item.getProductId())
            .quantity(item.getQuantity())
            .build();
    }
    
    private Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new RuntimeException("User not authenticated");
        }
        
        String token = authentication.getCredentials().toString();
        return jwtService.extractUserId(token);
    }
    
    private Cart getOrCreateCart() {
        Long userId = getCurrentUserId();
        return cartRepository.findByUserId(userId)
            .orElseGet(() -> {
                Cart newCart = cartDomainService.createCart(userId);
                return cartRepository.save(newCart);
            });
    }

    @Override
    public CartDTO addItemToCart(CartItemDTO itemDTO) {
        Cart cart = getOrCreateCart();

        ProductResponse product = catalogClient.getProductDetails(itemDTO.getProductId());
        if (product == null) {
            throw new ProductNotFoundException("Product not found: " + itemDTO.getProductId());
        }
        
        CartItem item = CartItem.builder()
            .productId(itemDTO.getProductId())
            .quantity(itemDTO.getQuantity())
            .build();

        cart = cartDomainService.addItemToCart(cart, item);
        cartRepository.save(cart);

        return convertToDTO(cart);
    }
    
    @Override
    public CartDTO removeItemFromCart(UUID itemId) {
        Cart cart = getOrCreateCart();
            
        cart = cartDomainService.removeItemFromCart(cart, itemId);
        cartRepository.save(cart);

        return convertToDTO(cart);
    }
    
    @Override
    public CartDTO getCart() {
        Cart cart = getOrCreateCart();
        return convertToDTO(cart);
    }
    
    @Override
    public void clearCart() {
        Cart cart = getOrCreateCart();
            
        cartDomainService.clearCart(cart);
        cartRepository.save(cart);
    }
} 