package com.microservice_ecommerce.cart.domain.service.impl;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
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
import com.microservice_ecommerce.integration.catalog.dto.response.ProductResponseSnakeCase;

import lombok.extern.slf4j.Slf4j;

@Service("mlRecommendationService")
@Slf4j
public class MLRecommendationService implements RecommendationService {
    
    private final CartAnalysisRepository cartAnalysisRepository;
    private final CatalogServiceClient catalogServiceClient;
    private final OrderServiceClient orderServiceClient;
    
    @Autowired
    public MLRecommendationService(CartAnalysisRepository cartAnalysisRepository,
                                  CatalogServiceClient catalogServiceClient,
                                  OrderServiceClient orderServiceClient) {
        this.cartAnalysisRepository = cartAnalysisRepository;
        this.catalogServiceClient = catalogServiceClient;
        this.orderServiceClient = orderServiceClient;
    }
    
    @Override
    public RecommendationResponse getRecommendations(RecommendationRequest request) {
        log.info("Getting ML-based recommendations for user: {} with type: {}", 
                request.getUserId(), request.getRecommendationType());
        
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
            // Get user's cart and order data
            Cart userCart = cartAnalysisRepository.findByUserId(userId);
            List<CartItem> cartItems = userCart != null ? userCart.getItems() : new ArrayList<>();
            List<OrderResponseDTO> userOrders = orderServiceClient.getOrdersByUserId(userId.toString());
            
            // Get all products
            List<ProductResponseSnakeCase> allProducts = catalogServiceClient.getAllProductsSnakeCase();
            if (allProducts == null || allProducts.isEmpty()) {
                return createEmptyResponse(userId, "CART_BASED", "No products available");
            }
            
            // Convert to ProductDTO
            List<ProductDTO> allProductDTOs = convertToProductDTOsSnakeCase(allProducts);
            
            // Create user-item interaction matrix
            Map<Long, Map<Long, Double>> userItemMatrix = createUserItemMatrix(userId, cartItems, userOrders, allProductDTOs);
            
            // Apply collaborative filtering
            Map<Long, Double> productScores = collaborativeFiltering(userId, userItemMatrix, allProductDTOs);
            
            // Get recommended products
            List<ProductDTO> recommendedProducts = allProductDTOs.stream()
                .filter(product -> !isInUserData(product.getId(), cartItems, userOrders))
                .sorted(Comparator.comparing((ProductDTO p) -> productScores.getOrDefault(p.getId(), 0.0)).reversed())
                .limit(limit)
                .collect(Collectors.toList());
            
            String reasoning = "ML-based recommendations using collaborative filtering";
            double confidence = calculateMLConfidence(userItemMatrix.size(), cartItems.size());
            
            return new RecommendationResponse(userId, "CART_BASED", recommendedProducts, reasoning, confidence);
            
        } catch (Exception e) {
            log.error("Error getting ML-based recommendations for user {}: {}", userId, e.getMessage());
            return createEmptyResponse(userId, "CART_BASED", "Error processing ML recommendations");
        }
    }
    
    @Override
    public RecommendationResponse getSimilarProducts(Long productId, int limit) {
        try {
            // Get all products
            List<ProductResponseSnakeCase> allProducts = catalogServiceClient.getAllProductsSnakeCase();
            if (allProducts == null || allProducts.isEmpty()) {
                return createEmptyResponse(null, "SIMILAR", "No products available");
            }
            
            List<ProductDTO> allProductDTOs = convertToProductDTOsSnakeCase(allProducts);
            
            // Find the target product
            ProductDTO targetProduct = allProductDTOs.stream()
                .filter(p -> p.getId().equals(productId))
                .findFirst()
                .orElse(null);
            
            if (targetProduct == null) {
                return createEmptyResponse(null, "SIMILAR", "Product not found");
            }
            
            // Calculate similarity scores using cosine similarity
            Map<Long, Double> similarityScores = calculateProductSimilarity(targetProduct, allProductDTOs);
            
            // Get similar products
            List<ProductDTO> similarProducts = allProductDTOs.stream()
                .filter(product -> !product.getId().equals(productId))
                .sorted(Comparator.comparing((ProductDTO p) -> similarityScores.getOrDefault(p.getId(), 0.0)).reversed())
                .limit(limit)
                .collect(Collectors.toList());
            
            String reasoning = "ML-based similarity using cosine similarity and feature vectors";
            return new RecommendationResponse(null, "SIMILAR", similarProducts, reasoning, 0.85);
            
        } catch (Exception e) {
            log.error("Error getting similar products for product {}: {}", productId, e.getMessage());
            return createEmptyResponse(null, "SIMILAR", "Error processing ML recommendations");
        }
    }
    
    @Override
    public RecommendationResponse getPopularProducts(int limit) {
        try {
            // Get all products
            List<ProductResponseSnakeCase> allProducts = catalogServiceClient.getAllProductsSnakeCase();
            if (allProducts == null || allProducts.isEmpty()) {
                return createEmptyResponse(null, "POPULAR", "No products available");
            }
            
            List<ProductDTO> allProductDTOs = convertToProductDTOsSnakeCase(allProducts);
            
            // Calculate popularity using PageRank-like algorithm
            Map<Long, Double> popularityScores = calculatePopularityScores(allProductDTOs);
            
            // Get popular products
            List<ProductDTO> popularProducts = allProductDTOs.stream()
                .sorted(Comparator.comparing((ProductDTO p) -> popularityScores.getOrDefault(p.getId(), 0.0)).reversed())
                .limit(limit)
                .collect(Collectors.toList());
            
            String reasoning = "ML-based popularity using PageRank algorithm";
            return new RecommendationResponse(null, "POPULAR", popularProducts, reasoning, 0.9);
            
        } catch (Exception e) {
            log.error("Error getting popular products: {}", e.getMessage());
            return createEmptyResponse(null, "POPULAR", "Error processing ML recommendations");
        }
    }
    
    // ML Algorithm: Collaborative Filtering
    private Map<Long, Double> collaborativeFiltering(Long userId, Map<Long, Map<Long, Double>> userItemMatrix, 
                                                   List<ProductDTO> allProducts) {
        Map<Long, Double> scores = new HashMap<>();
        
        // Initialize scores
        allProducts.forEach(product -> scores.put(product.getId(), 0.0));
        
        // Get user's interactions
        Map<Long, Double> userInteractions = userItemMatrix.getOrDefault(userId, new HashMap<>());
        
        if (userInteractions.isEmpty()) {
            // Cold start: use content-based filtering
            return contentBasedFiltering(allProducts);
        }
        
        // Calculate user similarity with other users
        Map<Long, Double> userSimilarities = calculateUserSimilarities(userId, userItemMatrix);
        
        // Predict scores using collaborative filtering
        for (ProductDTO product : allProducts) {
            double predictedScore = 0.0;
            double totalSimilarity = 0.0;
            
            for (Map.Entry<Long, Double> entry : userSimilarities.entrySet()) {
                Long similarUserId = entry.getKey();
                Double similarity = entry.getValue();
                
                Map<Long, Double> similarUserInteractions = userItemMatrix.getOrDefault(similarUserId, new HashMap<>());
                Double rating = similarUserInteractions.get(product.getId());
                
                if (rating != null) {
                    predictedScore += similarity * rating;
                    totalSimilarity += Math.abs(similarity);
                }
            }
            
            if (totalSimilarity > 0) {
                scores.put(product.getId(), predictedScore / totalSimilarity);
            }
        }
        
        return scores;
    }
    
    // ML Algorithm: User Similarity (Cosine Similarity)
    private Map<Long, Double> calculateUserSimilarities(Long userId, Map<Long, Map<Long, Double>> userItemMatrix) {
        Map<Long, Double> similarities = new HashMap<>();
        Map<Long, Double> userInteractions = userItemMatrix.getOrDefault(userId, new HashMap<>());
        
        for (Map.Entry<Long, Map<Long, Double>> entry : userItemMatrix.entrySet()) {
            Long otherUserId = entry.getKey();
            if (otherUserId.equals(userId)) continue;
            
            Map<Long, Double> otherUserInteractions = entry.getValue();
            double similarity = cosineSimilarity(userInteractions, otherUserInteractions);
            similarities.put(otherUserId, similarity);
        }
        
        return similarities;
    }
    
    // ML Algorithm: Cosine Similarity
    private double cosineSimilarity(Map<Long, Double> vector1, Map<Long, Double> vector2) {
        Set<Long> commonItems = new HashSet<>(vector1.keySet());
        commonItems.retainAll(vector2.keySet());
        
        if (commonItems.isEmpty()) return 0.0;
        
        double dotProduct = 0.0;
        double norm1 = 0.0;
        double norm2 = 0.0;
        
        for (Long item : commonItems) {
            double val1 = vector1.get(item);
            double val2 = vector2.get(item);
            dotProduct += val1 * val2;
        }
        
        for (double val : vector1.values()) {
            norm1 += val * val;
        }
        
        for (double val : vector2.values()) {
            norm2 += val * val;
        }
        
        norm1 = Math.sqrt(norm1);
        norm2 = Math.sqrt(norm2);
        
        if (norm1 == 0 || norm2 == 0) return 0.0;
        
        return dotProduct / (norm1 * norm2);
    }
    
    // ML Algorithm: Content-Based Filtering
    private Map<Long, Double> contentBasedFiltering(List<ProductDTO> allProducts) {
        Map<Long, Double> scores = new HashMap<>();
        
        // Create feature vectors for products
        Map<Long, double[]> productFeatures = new HashMap<>();
        
        for (ProductDTO product : allProducts) {
            double[] features = new double[3]; // price, category, popularity
            features[0] = product.getPrice() != null ? product.getPrice().doubleValue() / 100.0 : 0.0; // Normalize price
            features[1] = product.getCategoryId() != null ? product.getCategoryId().doubleValue() : 0.0;
            features[2] = calculateProductPopularity(product.getId()); // Simple popularity heuristic
            
            productFeatures.put(product.getId(), features);
        }
        
        // Calculate scores based on feature similarity
        for (ProductDTO product : allProducts) {
            double score = 0.0;
            double[] productFeature = productFeatures.get(product.getId());
            
            // Prefer mid-range products with good features
            if (productFeature[0] > 0.3 && productFeature[0] < 0.7) score += 0.3;
            if (productFeature[2] > 0.5) score += 0.4;
            if (product.getCategoryId() != null && product.getCategoryId() <= 5) score += 0.3; // Prefer first 5 categories
            
            scores.put(product.getId(), score);
        }
        
        return scores;
    }
    
    // ML Algorithm: Product Similarity using Feature Vectors
    private Map<Long, Double> calculateProductSimilarity(ProductDTO targetProduct, List<ProductDTO> allProducts) {
        Map<Long, Double> similarities = new HashMap<>();
        
        // Create feature vector for target product
        double[] targetFeatures = createProductFeatureVector(targetProduct);
        
        for (ProductDTO product : allProducts) {
            if (product.getId().equals(targetProduct.getId())) continue;
            
            double[] productFeatures = createProductFeatureVector(product);
            double similarity = cosineSimilarity(targetFeatures, productFeatures);
            similarities.put(product.getId(), similarity);
        }
        
        return similarities;
    }
    
    // Create feature vector for product
    private double[] createProductFeatureVector(ProductDTO product) {
        double[] features = new double[4];
        features[0] = product.getPrice() != null ? product.getPrice().doubleValue() / 100.0 : 0.0;
        features[1] = product.getCategoryId() != null ? product.getCategoryId().doubleValue() / 10.0 : 0.0;
        features[2] = product.getImageUrls() != null ? product.getImageUrls().size() / 5.0 : 0.0;
        features[3] = product.getName() != null ? product.getName().length() / 50.0 : 0.0;
        return features;
    }
    
    // Cosine similarity for arrays
    private double cosineSimilarity(double[] vector1, double[] vector2) {
        if (vector1.length != vector2.length) return 0.0;
        
        double dotProduct = 0.0;
        double norm1 = 0.0;
        double norm2 = 0.0;
        
        for (int i = 0; i < vector1.length; i++) {
            dotProduct += vector1[i] * vector2[i];
            norm1 += vector1[i] * vector1[i];
            norm2 += vector2[i] * vector2[i];
        }
        
        norm1 = Math.sqrt(norm1);
        norm2 = Math.sqrt(norm2);
        
        if (norm1 == 0 || norm2 == 0) return 0.0;
        
        return dotProduct / (norm1 * norm2);
    }
    
    // ML Algorithm: PageRank-like Popularity
    private Map<Long, Double> calculatePopularityScores(List<ProductDTO> allProducts) {
        Map<Long, Double> popularityScores = new HashMap<>();
        
        // Initialize scores
        allProducts.forEach(product -> popularityScores.put(product.getId(), 1.0));
        
        // Simple PageRank-like algorithm
        int iterations = 10;
        double dampingFactor = 0.85;
        
        for (int i = 0; i < iterations; i++) {
            Map<Long, Double> newScores = new HashMap<>();
            
            for (ProductDTO product : allProducts) {
                double score = (1 - dampingFactor) / allProducts.size();
                
                // Add popularity based on product features
                if (product.getPrice() != null && product.getPrice().doubleValue() > 0) {
                    score += dampingFactor * calculateProductPopularity(product.getId());
                }
                
                newScores.put(product.getId(), score);
            }
            
            // Update the popularity scores
            popularityScores.clear();
            popularityScores.putAll(newScores);
        }
        
        return popularityScores;
    }
    
    // Helper methods
    private Map<Long, Map<Long, Double>> createUserItemMatrix(Long userId, List<CartItem> cartItems, 
                                                             List<OrderResponseDTO> userOrders, List<ProductDTO> allProductDTOs) {
        Map<Long, Map<Long, Double>> matrix = new HashMap<>();
        Map<Long, Double> userInteractions = new HashMap<>();
        
        // Add cart interactions
        for (CartItem item : cartItems) {
            userInteractions.put(item.getProductId(), item.getQuantity() * 1.0);
        }
        
        // Add order interactions
        for (OrderResponseDTO order : userOrders) {
            if (order.getOrder_items() != null) {
                for (OrderItemResponseDTO orderItem : order.getOrder_items()) {
                    try {
                        Long productId = Long.parseLong(orderItem.getProduct_id());
                        double currentScore = userInteractions.getOrDefault(productId, 0.0);
                        userInteractions.put(productId, currentScore + orderItem.getQuantity() * 1.5);
                    } catch (NumberFormatException e) {
                        // Skip invalid product IDs
                    }
                }
            }
        }
        
        matrix.put(userId, userInteractions);
        
        // Add some dummy users for collaborative filtering (in real app, get from database)
        addDummyUsers(matrix, allProductDTOs);
        
        return matrix;
    }
    
    private void addDummyUsers(Map<Long, Map<Long, Double>> matrix, List<ProductDTO> allProductDTOs) {
        // Add some dummy users for demonstration
        for (int i = 1; i <= 5; i++) {
            Map<Long, Double> dummyInteractions = new HashMap<>();
            Random random = new Random(i); // Seed for reproducible results
            
            for (ProductDTO product : allProductDTOs) {
                if (random.nextDouble() < 0.3) { // 30% chance of interaction
                    dummyInteractions.put(product.getId(), random.nextDouble() * 5.0);
                }
            }
            
            matrix.put((long) (1000 + i), dummyInteractions);
        }
    }
    
    private double calculateProductPopularity(Long productId) {
        // Simple popularity heuristic based on product ID
        if (productId <= 20) return 0.9;
        if (productId <= 50) return 0.7;
        if (productId <= 100) return 0.5;
        return 0.3;
    }
    
    private boolean isInUserData(Long productId, List<CartItem> cartItems, List<OrderResponseDTO> userOrders) {
        // Check cart
        boolean inCart = cartItems.stream().anyMatch(item -> item.getProductId().longValue() == productId.longValue());
        
        // Check orders
        boolean inOrders = userOrders.stream()
            .anyMatch(order -> order.getOrder_items() != null && 
                order.getOrder_items().stream()
                    .anyMatch(item -> {
                        try {
                            return Long.parseLong(item.getProduct_id()) == productId.longValue();
                        } catch (NumberFormatException e) {
                            return false;
                        }
                    }));
        
        return inCart || inOrders;
    }
    
    private double calculateMLConfidence(int userInteractions, int cartItems) {
        int totalInteractions = userInteractions + cartItems;
        
        if (totalInteractions == 0) return 0.4; // ML confidence for new users
        if (totalInteractions < 5) return 0.6;  // Medium ML confidence
        if (totalInteractions < 20) return 0.8; // Good ML confidence
        return 0.95; // High ML confidence
    }
    
    private RecommendationResponse createEmptyResponse(Long userId, String type, String reasoning) {
        return new RecommendationResponse(userId, type, new ArrayList<>(), reasoning, 0.0);
    }
    
    private ProductDTO convertToProductDTOSnakeCase(ProductResponseSnakeCase productResponse) {
        Long categoryId = productResponse.getCategory_id();
        if (categoryId == null) categoryId = 1L;
        
        List<String> imageUrls = productResponse.getImage_urls();
        if (imageUrls == null) imageUrls = new ArrayList<>();
        
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
} 