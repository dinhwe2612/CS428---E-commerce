package com.microservice_ecommerce.cart.infrastructure.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.microservice_ecommerce.cart.domain.model.Cart;
import com.microservice_ecommerce.cart.domain.model.CartItem;

@Repository
public interface CartAnalysisRepository extends JpaRepository<Cart, java.util.UUID> {
    
    // Find cart by user ID
    Cart findByUserId(Long userId);
    
    // Find all carts for a user (historical data)
    List<Cart> findByUserIdOrderByCreatedAtDesc(Long userId);
    
    // Find most frequently added products across all users
    @Query("SELECT ci.productId, COUNT(ci) as frequency FROM CartItem ci " +
           "WHERE ci.cart.createdAt >= :since " +
           "GROUP BY ci.productId " +
           "ORDER BY frequency DESC")
    List<Object[]> findMostFrequentProducts(@Param("since") LocalDateTime since);
    
    // Find products frequently bought together with a given product
    @Query("SELECT ci2.productId, COUNT(ci2) as frequency FROM CartItem ci1 " +
           "JOIN CartItem ci2 ON ci1.cart.id = ci2.cart.id " +
           "WHERE ci1.productId = :productId " +
           "AND ci2.productId != :productId " +
           "AND ci1.cart.createdAt >= :since " +
           "GROUP BY ci2.productId " +
           "ORDER BY frequency DESC")
    List<Object[]> findFrequentlyBoughtTogether(@Param("productId") Long productId, 
                                               @Param("since") LocalDateTime since);
    
    // Find recent cart items for a user
    @Query("SELECT ci FROM CartItem ci " +
           "WHERE ci.cart.userId = :userId " +
           "ORDER BY ci.cart.createdAt DESC")
    List<CartItem> findRecentCartItemsByUser(@Param("userId") Long userId);
    
    // Find products in user's current cart
    @Query("SELECT ci FROM CartItem ci " +
           "WHERE ci.cart.userId = :userId " +
           "AND ci.cart.id = (SELECT c.id FROM Cart c WHERE c.userId = :userId ORDER BY c.updatedAt DESC LIMIT 1)")
    List<CartItem> findCurrentCartItemsByUser(@Param("userId") Long userId);
} 