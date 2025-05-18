package com.microservice_ecommerce.cart.infrastructure.repository;

import com.microservice_ecommerce.cart.domain.model.Cart;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

public interface CartRepository extends JpaRepository<Cart, UUID> {
    Optional<Cart> findByUserId(Long userId);
} 