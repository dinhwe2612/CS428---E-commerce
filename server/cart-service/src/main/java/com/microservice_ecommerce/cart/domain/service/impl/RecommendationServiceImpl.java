package com.microservice_ecommerce.cart.domain.service.impl;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import com.microservice_ecommerce.cart.domain.dto.RecommendationRequest;
import com.microservice_ecommerce.cart.domain.dto.RecommendationResponse;
import com.microservice_ecommerce.cart.domain.dto.RecommendationResponse.ProductDTO;
import com.microservice_ecommerce.cart.domain.model.Cart;
import com.microservice_ecommerce.cart.domain.model.CartItem;
import com.microservice_ecommerce.cart.domain.service.RecommendationService;
import com.microservice_ecommerce.cart.infrastructure.client.OrderServiceClient;
import com.microservice_ecommerce.cart.infrastructure.dto.OrderItemResponseDTO;
import com.microservice_ecommerce.cart.infrastructure.dto.OrderResponseDTO;
import com.microservice_ecommerce.cart.infrastructure.repository.CartAnalysisRepository;
import com.microservice_ecommerce.integration.catalog.client.CatalogServiceClient;
import com.microservice_ecommerce.integration.catalog.dto.response.ProductResponse;
import com.microservice_ecommerce.integration.catalog.dto.response.ProductResponseSnakeCase;

import lombok.extern.slf4j.Slf4j;

@Service
@Primary
@Slf4j
public class RecommendationServiceImpl implements RecommendationService {
    
    private final CartAnalysisRepository cartAnalysisRepository;
    private final CatalogServiceClient catalogServiceClient;
    private final OrderServiceClient orderServiceClient;
    
    @Autowired
    public RecommendationServiceImpl(CartAnalysisRepository cartAnalysisRepository,
                                   CatalogServiceClient catalogServiceClient,
                                   OrderServiceClient orderServiceClient) {
        this.cartAnalysisRepository = cartAnalysisRepository;
        this.catalogServiceClient = catalogServiceClient;
        this.orderServiceClient = orderServiceClient;
    }
    
    @Override
    public RecommendationResponse getRecommendations(RecommendationRequest request) {
        log.info("Getting recommendations for user: {} with type: {}", request.getUserId(), request.getRecommendationType());
        
        switch (request.getRecommendationType().toUpperCase()) {
            case "CART_BASED":
                return getCartBasedRecommendations(request.getUserId(), request.getLimit());
            case "SIMILAR":
                return getSimilarProducts(request.getProductId(), request.getLimit());
            case "POPULAR":
                return getPopularProducts(request.getLimit());
            default:
                return getCartBasedRecommendations(request.getUserId(), request.getLimit());
        }
    }
    
    @Override
    public RecommendationResponse getCartBasedRecommendations(Long userId, int limit) {
        try {
            // Get user's cart data
            Cart userCart = cartAnalysisRepository.findByUserId(userId);
            List<CartItem> cartItems = userCart != null ? userCart.getItems() : new ArrayList<>();
            
            // Get user's order history
            List<OrderResponseDTO> userOrders = orderServiceClient.getOrdersByUserId(userId.toString());
            
            // Debug logging for orders
            log.info("User {} - Found {} orders", userId, userOrders.size());
            for (OrderResponseDTO order : userOrders) {
                log.info("Order {} - Items: {}", order.getId(), 
                    order.getOrder_items() != null ? order.getOrder_items().size() : "null");
                if (order.getOrder_items() != null) {
                    for (OrderItemResponseDTO item : order.getOrder_items()) {
                        log.info("  - Item: ProductId={}, Quantity={}, Name={}", 
                            item.getProduct_id(), item.getQuantity(), item.getProduct_name());
                    }
                }
            }
            
            List<OrderItemResponseDTO> orderItems = userOrders.stream()
                .filter(order -> order.getOrder_items() != null) // Filter out orders with null order items
                .flatMap(order -> order.getOrder_items().stream())
                .collect(Collectors.toList());
            
            // Debug logging
            log.info("User {} - Cart items: {}, Orders: {}, Order items: {}", 
                userId, cartItems.size(), userOrders.size(), orderItems.size());
            
            // Get all available products in snake_case format
            List<ProductResponseSnakeCase> allProductResponses = catalogServiceClient.getAllProductsSnakeCase();
            if (allProductResponses == null || allProductResponses.isEmpty()) {
                return createEmptyResponse(userId, "CART_BASED", "No products available");
            }
            
            // Debug logging for raw product responses
            log.info("Received {} product responses from catalog service", allProductResponses.size());
            for (ProductResponseSnakeCase product : allProductResponses.stream().limit(3).collect(Collectors.toList())) {
                log.info("Raw Product {} - Name: {}, Price: {}, CategoryId: {}, ImageUrls: {}", 
                    product.getId(), product.getName(), product.getPrice(), product.getCategory_id(), product.getImage_urls());
            }
            
            // Convert to ProductDTO
            List<ProductDTO> allProducts = convertToProductDTOsSnakeCase(allProductResponses);
            
            // Debug logging for products
            log.info("Converted {} products", allProducts.size());
            for (ProductDTO product : allProducts.stream().limit(3).collect(Collectors.toList())) {
                log.info("Product {} - Name: {}, Price: {}, CategoryId: {}, ImageUrls: {}", 
                    product.getId(), product.getName(), product.getPrice(), 
                    product.getCategoryId(), product.getImageUrls());
            }
            
            // Calculate product scores based on cart and order data
            Map<Long, Double> productScores = calculateProductScores(cartItems, orderItems, allProducts);
            
            // Debug: Check what products are being filtered out
            List<Long> userProductIds = new ArrayList<>();
            for (CartItem item : cartItems) {
                userProductIds.add(item.getProductId());
            }
            for (OrderItemResponseDTO item : orderItems) {
                try {
                    userProductIds.add(Long.parseLong(item.getProduct_id()));
                } catch (NumberFormatException e) {
                    // Skip invalid IDs
                }
            }
            log.info("User's products (to be filtered out): {}", userProductIds);
            
            // Get top recommended products
            List<ProductDTO> recommendedProducts = allProducts.stream()
                .filter(product -> !isInUserData(product.getId(), cartItems, orderItems))
                .sorted(Comparator.comparing((ProductDTO p) -> productScores.getOrDefault(p.getId(), 0.0)).reversed())
                .limit(limit)
                .collect(Collectors.toList());
            
            // Debug logging for scoring
            log.info("Product scores for top 5 products:");
            recommendedProducts.stream().limit(5).forEach(product -> {
                double score = productScores.getOrDefault(product.getId(), 0.0);
                log.info("Product {} - Score: {}", product.getId(), score);
            });
            
            String reasoning = generateReasoning(cartItems, orderItems, userOrders.size());
            double confidence = calculateConfidence(cartItems.size(), orderItems.size());
            
            return new RecommendationResponse(userId, "CART_BASED", recommendedProducts, reasoning, confidence);
            
        } catch (Exception e) {
            log.error("Error getting cart-based recommendations for user {}: {}", userId, e.getMessage());
            return createEmptyResponse(userId, "CART_BASED", "Error processing recommendations");
        }
    }
    
    @Override
    public RecommendationResponse getSimilarProducts(Long productId, int limit) {
        try {
            // Get the target product
            ProductResponse targetProductResponse = catalogServiceClient.getProductDetails(productId);
            if (targetProductResponse == null) {
                return createEmptyResponse(null, "SIMILAR", "Product not found");
            }
            
            // Convert to ProductDTO
            ProductDTO targetProduct = convertToProductDTO(targetProductResponse);
            
            // Get all products in snake_case format
            List<ProductResponseSnakeCase> allProductResponses = catalogServiceClient.getAllProductsSnakeCase();
            if (allProductResponses == null || allProductResponses.isEmpty()) {
                return createEmptyResponse(null, "SIMILAR", "No products available");
            }
            
            // Convert to ProductDTO
            List<ProductDTO> allProducts = convertToProductDTOsSnakeCase(allProductResponses);
            
                         // Find products in the same category (simple similarity)
             List<ProductDTO> similarProducts = allProducts.stream()
                 .filter(product -> !product.getId().equals(productId))
                 .filter(product -> product.getCategoryId() != null && 
                                  product.getCategoryId().equals(targetProduct.getCategoryId()))
                 .limit(limit)
                 .collect(Collectors.toList());
             
             String reasoning = "Similar products in the same category: " + targetProduct.getCategoryId();
            return new RecommendationResponse(null, "SIMILAR", similarProducts, reasoning, 0.8);
            
        } catch (Exception e) {
            log.error("Error getting similar products for product {}: {}", productId, e.getMessage());
            return createEmptyResponse(null, "SIMILAR", "Error processing recommendations");
        }
    }
    
    @Override
    public RecommendationResponse getPopularProducts(int limit) {
        try {
            // Get most frequently added products from cart data (last 30 days)
            LocalDateTime since = LocalDateTime.now().minusDays(30);
            List<Object[]> popularProductIds = cartAnalysisRepository.findMostFrequentProducts(since);
            
            // Get all products in snake_case format
            List<ProductResponseSnakeCase> allProductResponses = catalogServiceClient.getAllProductsSnakeCase();
            if (allProductResponses == null || allProductResponses.isEmpty()) {
                return createEmptyResponse(null, "POPULAR", "No products available");
            }
            
            // Convert to ProductDTO
            List<ProductDTO> allProducts = convertToProductDTOsSnakeCase(allProductResponses);
            
            // Map product IDs to products
            Map<Long, ProductDTO> productMap = allProducts.stream()
                .collect(Collectors.toMap(ProductDTO::getId, product -> product));
            
            // Get popular products
            List<ProductDTO> popularProducts = popularProductIds.stream()
                .map(result -> {
                    Long productId = (Long) result[0];
                    return productMap.get(productId);
                })
                .filter(product -> product != null)
                .limit(limit)
                .collect(Collectors.toList());
            
            String reasoning = "Most frequently added products in the last 30 days";
            return new RecommendationResponse(null, "POPULAR", popularProducts, reasoning, 0.9);
            
        } catch (Exception e) {
            log.error("Error getting popular products: {}", e.getMessage());
            return createEmptyResponse(null, "POPULAR", "Error processing recommendations");
        }
    }
    
        private Map<Long, Double> calculateProductScores(List<CartItem> cartItems, 
                                                   List<OrderItemResponseDTO> orderItems,
                                                   List<ProductDTO> allProducts) {
        Map<Long, Double> scores = new HashMap<>();
        
        // Initialize scores
        allProducts.forEach(product -> scores.put(product.getId(), 0.0));
        
        // PRIORITY 1: Quantity-based scoring (higher weights for quantity)
        // Score based on cart items (current interest) - PRIORITIZE QUANTITY
        for (CartItem cartItem : cartItems) {
            Long productId = cartItem.getProductId();
            // Higher weight for quantity: quantity * 1.0 (was 0.3)
            double cartScore = cartItem.getQuantity() * 1.0;
            scores.merge(productId, cartScore, Double::sum);
            log.info("Cart scoring product {}: quantity={}, score=+{}", productId, cartItem.getQuantity(), cartScore);
        }
        
        // Score based on order history (past purchases) - PRIORITIZE QUANTITY
        for (OrderItemResponseDTO orderItem : orderItems) {
            try {
                Long productId = Long.parseLong(orderItem.getProduct_id());
                // Higher weight for quantity: quantity * 1.5 (was 0.5)
                double orderScore = orderItem.getQuantity() * 1.5;
                scores.merge(productId, orderScore, Double::sum);
                log.info("Order scoring product {}: quantity={}, score=+{}", productId, orderItem.getQuantity(), orderScore);
            } catch (NumberFormatException e) {
                log.warn("Invalid product ID in order: {}", orderItem.getProduct_id());
            }
        }
        
        // PRIORITY 2: Category-based scoring (much higher weights for category preferences)
        Map<String, Double> categoryScores = new HashMap<>();
        
        // Calculate category scores from cart - PRIORITIZE CATEGORY
        for (CartItem cartItem : cartItems) {
            ProductDTO product = allProducts.stream()
                .filter(p -> p.getId().equals(cartItem.getProductId()))
                .findFirst()
                .orElse(null);
            if (product != null && product.getCategoryId() != null) {
                // Higher weight for category: quantity * 2.0 (was 0.2)
                double categoryScore = cartItem.getQuantity() * 2.0;
                categoryScores.merge(product.getCategoryId().toString(), categoryScore, Double::sum);
                log.info("Category scoring from cart: category={}, quantity={}, score=+{}", 
                    product.getCategoryId(), cartItem.getQuantity(), categoryScore);
            }
        }
        
        // Calculate category scores from orders - PRIORITIZE CATEGORY
        for (OrderItemResponseDTO orderItem : orderItems) {
            try {
                Long productId = Long.parseLong(orderItem.getProduct_id());
                ProductDTO product = allProducts.stream()
                    .filter(p -> p.getId().equals(productId))
                    .findFirst()
                    .orElse(null);
                if (product != null && product.getCategoryId() != null) {
                    // Higher weight for category: quantity * 3.0 (was 0.3)
                    double categoryScore = orderItem.getQuantity() * 3.0;
                    categoryScores.merge(product.getCategoryId().toString(), categoryScore, Double::sum);
                    log.info("Category scoring from order: category={}, quantity={}, score=+{}", 
                        product.getCategoryId(), orderItem.getQuantity(), categoryScore);
                }
            } catch (NumberFormatException e) {
                // Skip invalid product IDs
            }
        }
        
        // Apply category bonus to products - PRIORITIZE CATEGORY
        for (ProductDTO product : allProducts) {
            if (product.getCategoryId() != null) {
                // Higher category bonus multiplier: 0.5 (was 0.1)
                double categoryBonus = categoryScores.getOrDefault(product.getCategoryId().toString(), 0.0) * 0.5;
                scores.merge(product.getId(), categoryBonus, Double::sum);
                if (categoryBonus > 0) {
                    log.info("Category bonus for product {} (category {}): +{} points", 
                        product.getId(), product.getCategoryId(), categoryBonus);
                }
            }
        }
        
        // Only add fallback scoring if no category preferences exist
        if (categoryScores.isEmpty()) {
            log.info("No category preferences found, adding fallback scoring");
            
            // Add price-based scoring (prefer mid-range products)
            for (ProductDTO product : allProducts) {
                if (product.getPrice() != null) {
                    double priceScore = calculatePriceScore(product.getPrice().doubleValue());
                    scores.merge(product.getId(), priceScore, Double::sum);
                }
            }
            
            // Add popularity based on product ID (simulate popularity)
            for (ProductDTO product : allProducts) {
                double popularityScore = calculatePopularityScore(product.getId());
                scores.merge(product.getId(), popularityScore, Double::sum);
            }
        } else {
            log.info("Category preferences found, skipping fallback scoring to prioritize category-based recommendations");
        }
        
        log.info("Category scores: {}", categoryScores);
        
        // Debug: Show top 10 scores
        log.info("Top 10 product scores:");
        scores.entrySet().stream()
            .sorted(Map.Entry.<Long, Double>comparingByValue().reversed())
            .limit(10)
            .forEach(entry -> log.info("Product {}: {} points", entry.getKey(), entry.getValue()));
        
        return scores;
    }
    
    private boolean isInUserData(Long productId, List<CartItem> cartItems, List<OrderItemResponseDTO> orderItems) {
        // Check if product is in cart
        boolean inCart = cartItems.stream()
            .anyMatch(item -> item.getProductId().equals(productId));
        
        // Check if product is in order history
        boolean inOrders = orderItems.stream()
            .anyMatch(item -> {
                try {
                    return Long.parseLong(item.getProduct_id()) == productId;
                } catch (NumberFormatException e) {
                    return false;
                }
            });
        
        return inCart || inOrders;
    }
    
    private String generateReasoning(List<CartItem> cartItems, List<OrderItemResponseDTO> orderItems, int totalOrders) {
        // Debug logging
        log.info("Generating reasoning - Cart items: {}, Order items: {}, Total orders: {}", 
            cartItems.size(), orderItems.size(), totalOrders);
        
        if (cartItems.isEmpty() && orderItems.isEmpty()) {
            if (totalOrders > 0) {
                return "Based on popular products and your order history";
            } else {
                return "Based on popular products since you're new to our platform";
            }
        }
        
        StringBuilder reasoning = new StringBuilder("Based on your ");
        
        if (!cartItems.isEmpty()) {
            reasoning.append("current cart items");
            if (!orderItems.isEmpty()) {
                reasoning.append(" and ");
            }
        }
        
        if (!orderItems.isEmpty()) {
            reasoning.append("purchase history");
        } else if (totalOrders > 0) {
            reasoning.append("order history");
        }
        
        reasoning.append(" and similar user preferences");
        return reasoning.toString();
    }
    
    private double calculateConfidence(int cartItemCount, int orderItemCount) {
        int totalInteractions = cartItemCount + orderItemCount;
        
        if (totalInteractions == 0) return 0.3; // Low confidence for new users
        if (totalInteractions < 5) return 0.5;  // Medium confidence
        if (totalInteractions < 20) return 0.7; // Good confidence
        return 0.9; // High confidence
    }
    
    private RecommendationResponse createEmptyResponse(Long userId, String type, String reasoning) {
        return new RecommendationResponse(userId, type, new ArrayList<>(), reasoning, 0.0);
    }
    
    private ProductDTO convertToProductDTO(ProductResponse productResponse) {
        // Handle null categoryId by providing a default
        Long categoryId = productResponse.getCategoryId();
        if (categoryId == null) {
            categoryId = 1L; // Default category ID
        }
        
        // Handle null imageUrls by providing empty list
        List<String> imageUrls = productResponse.getImageUrls();
        if (imageUrls == null) {
            imageUrls = new ArrayList<>();
        }
        
        return new ProductDTO(
            productResponse.getId(),
            productResponse.getName(),
            productResponse.getDescription(),
            categoryId,
            productResponse.getImageIds(),
            imageUrls,
            productResponse.getPrice()
        );
    }
    
    private List<ProductDTO> convertToProductDTOs(List<ProductResponse> productResponses) {
        return productResponses.stream()
            .map(this::convertToProductDTO)
            .collect(Collectors.toList());
    }
    
    private ProductDTO convertToProductDTOSnakeCase(ProductResponseSnakeCase productResponse) {
        // Handle null categoryId by providing a default
        Long categoryId = productResponse.getCategory_id();
        if (categoryId == null) {
            categoryId = 1L; // Default category ID
        }
        
        // Handle null imageUrls by providing empty list
        List<String> imageUrls = productResponse.getImage_urls();
        if (imageUrls == null) {
            imageUrls = new ArrayList<>();
        }
        
        return new ProductDTO(
            productResponse.getId(),
            productResponse.getName(),
            productResponse.getDescription(),
            categoryId,
            productResponse.getImage_ids(),
            imageUrls,
            productResponse.getPrice()
        );
    }
    
    private List<ProductDTO> convertToProductDTOsSnakeCase(List<ProductResponseSnakeCase> productResponses) {
        return productResponses.stream()
            .map(this::convertToProductDTOSnakeCase)
            .collect(Collectors.toList());
    }
    
    private double calculatePriceScore(Double price) {
        if (price == null || price <= 0) return 0.0;
        
        // Prefer mid-range products (50-150 range gets higher scores)
        if (price >= 50.0 && price <= 150.0) {
            return 0.5; // High score for mid-range
        } else if (price >= 30.0 && price <= 200.0) {
            return 0.3; // Medium score for reasonable range
        } else {
            return 0.1; // Low score for very cheap or expensive
        }
    }
    
    private double calculatePopularityScore(Long productId) {
        if (productId == null) return 0.0;
        
        // Simulate popularity based on product ID (lower IDs are more popular)
        // This is a simple heuristic - in real world, you'd use actual popularity data
        if (productId <= 20) {
            return 0.4; // Very popular (first 20 products)
        } else if (productId <= 50) {
            return 0.3; // Popular (products 21-50)
        } else if (productId <= 100) {
            return 0.2; // Moderately popular
        } else {
            return 0.1; // Less popular
        }
    }
} 