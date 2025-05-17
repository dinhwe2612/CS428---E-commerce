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
import com.microservice_ecommerce.integration.product.client.ProductServiceClient;
import com.microservice_ecommerce.integration.product.dto.response.ProductResponse;
import com.microservice_ecommerce.cart.infrastructure.repository.CartRepository;
import com.microservice_ecommerce.cart.application.dto.*;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CartServiceImpl implements CartService {
    
    private final CartRepository cartRepository;
    private final CartDomainService cartDomainService;
    private final ProductServiceClient productClient;
    
    public CartServiceImpl(CartRepository cartRepository, CartDomainService cartDomainService, 
                          ProductServiceClient productClient) {
        this.cartRepository = cartRepository;
        this.cartDomainService = cartDomainService;
        this.productClient = productClient;
    }

    @Override
    public CartDTO createCart(UUID userId) {
        Cart cart = cartDomainService.createCart(userId);
        cartRepository.save(cart);

        List<CartItemDTO> items = cart.getItems().stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());

        return CartDTO.builder()
            .id(cart.getId())
            .userId(cart.getUserId())
            .items(items)
            .build();
    }

    private CartItemDTO convertToDTO(CartItem item) {
        return CartItemDTO.builder()
            .id(item.getId())
            .productId(item.getProductId())
            .quantity(item.getQuantity())
            .build();
    }
    
    @Override
    public CartDTO addItemToCart(UUID cartId, CartItemDTO itemDTO) {
        Cart cart = cartRepository.findById(cartId)
            .orElseThrow(() -> new CartNotFoundException("Cart not found"));

        ProductResponse product = productClient.detailProduct(itemDTO.getProductId());
        if (product == null) {
            throw new ProductNotFoundException("Product not found: " + itemDTO.getProductId());
        }
        
        if (!product.getAvailable() || product.getStock() < itemDTO.getQuantity()) {
            throw new ProductUnavailableException("Product unavailable or insufficient stock");
        }

        CartItem item = CartItem.builder()
            .productId(itemDTO.getProductId())
            .quantity(itemDTO.getQuantity())
            .build();

        cart = cartDomainService.addItemToCart(cart, item);
        cartRepository.save(cart);

        List<CartItemDTO> items = cart.getItems().stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
            
        return CartDTO.builder()
            .id(cart.getId())
            .userId(cart.getUserId())
            .items(items)
            .build();
    }
    
    @Override
    public CartDTO removeItemFromCart(UUID cartId, UUID itemId) {
        Cart cart = cartRepository.findById(cartId)
            .orElseThrow(() -> new CartNotFoundException("Cart not found"));
            
        cart = cartDomainService.removeItemFromCart(cart, itemId);
        cartRepository.save(cart);

        List<CartItemDTO> items = cart.getItems().stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
            
        return CartDTO.builder()
            .id(cart.getId())
            .userId(cart.getUserId())
            .items(items)
            .build();
    }
    
    @Override
    public CartDTO getCart(UUID cartId) {
        Cart cart = cartRepository.findById(cartId)
            .orElseThrow(() -> new CartNotFoundException("Cart not found"));

        List<CartItemDTO> items = cart.getItems().stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
            
        return CartDTO.builder()
            .id(cart.getId())
            .userId(cart.getUserId())
            .items(items)
            .build();
    }
    
    @Override
    public void clearCart(UUID cartId) {
        Cart cart = cartRepository.findById(cartId)
            .orElseThrow(() -> new CartNotFoundException("Cart not found"));
            
        cartDomainService.clearCart(cart);
        cartRepository.save(cart);
    }
} 