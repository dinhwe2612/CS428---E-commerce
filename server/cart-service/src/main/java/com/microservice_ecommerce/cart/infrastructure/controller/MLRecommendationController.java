package com.microservice_ecommerce.cart.infrastructure.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.microservice_ecommerce.cart.application.dto.ApiResponse;
import com.microservice_ecommerce.cart.domain.dto.RecommendationRequest;
import com.microservice_ecommerce.cart.domain.dto.RecommendationResponse;
import com.microservice_ecommerce.cart.domain.service.RecommendationService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController("mlRecommendationController")
@RequestMapping("/api/v1/cart/ml")
@Tag(name = "ML-Based Product Recommendations", 
     description = "Advanced ML-powered product recommendation system using collaborative filtering, content-based filtering, and PageRank algorithms")
public class MLRecommendationController {

    private final RecommendationService mlRecommendationService;

    @Autowired
    public MLRecommendationController(@org.springframework.beans.factory.annotation.Qualifier("mlRecommendationService") RecommendationService mlRecommendationService) {
        this.mlRecommendationService = mlRecommendationService;
    }

    @PostMapping("/recommendations")
    @Operation(
        summary = "Get ML-powered product recommendations",
        description = """
            This endpoint provides advanced ML-based product recommendations using sophisticated algorithms:
            
            **ML Algorithms Used:**
            - **Collaborative Filtering**: Finds similar users and predicts preferences
            - **Content-Based Filtering**: Analyzes product features and user preferences
            - **Cosine Similarity**: Calculates similarity between users and products
            - **PageRank Algorithm**: Determines product popularity scores
            
            **Data Sources:**
            - User's current cart items (weighted by quantity)
            - User's purchase history from orders (weighted by frequency)
            - Product feature vectors (price, category, images, description)
            - User interaction patterns across the platform
            
            **Recommendation Types:**
            - **CART_BASED**: Personalized recommendations using collaborative filtering
            - **SIMILAR**: Products similar to a specific product using feature vectors
            - **POPULAR**: Most popular products using PageRank algorithm
            
            **ML Confidence Scoring:**
            - New users (0 interactions): 40% confidence
            - Limited data (1-5 interactions): 60% confidence
            - Good data (5-20 interactions): 80% confidence
            - Rich data (20+ interactions): 95% confidence
            
            **Algorithm Features:**
            - Cold start handling for new users
            - Feature vector normalization
            - Iterative PageRank convergence
            - User similarity clustering
            """,
        requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "ML recommendation request parameters",
            required = true,
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = RecommendationRequest.class),
                examples = {
                    @ExampleObject(
                        name = "ML Cart-based recommendations",
                        value = """
                        {
                            "user_id": 123,
                            "recommendation_type": "CART_BASED",
                            "limit": 10
                        }
                        """
                    ),
                    @ExampleObject(
                        name = "ML Similar products",
                        value = """
                        {
                            "product_id": 456,
                            "recommendation_type": "SIMILAR",
                            "limit": 5
                        }
                        """
                    ),
                    @ExampleObject(
                        name = "ML Popular products",
                        value = """
                        {
                            "recommendation_type": "POPULAR",
                            "limit": 8
                        }
                        """
                    )
                }
            )
        )
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "Successfully retrieved ML recommendations",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ApiResponse.class),
                examples = {
                    @ExampleObject(
                        name = "Successful ML cart-based recommendations",
                        value = """
                        {
                            "success": true,
                            "message": "ML recommendations retrieved successfully",
                            "data": {
                                "user_id": 123,
                                "recommendation_type": "CART_BASED",
                                "recommended_products": [
                                    {
                                        "id": 789,
                                        "name": "Premium Rose Bouquet",
                                        "description": "Beautiful red roses for special occasions",
                                        "price": 49.99,
                                        "category_id": 1,
                                        "image_ids": [1, 2],
                                        "image_urls": ["https://example.com/rose-bouquet.jpg"]
                                    }
                                ],
                                "reasoning": "ML-based recommendations using collaborative filtering",
                                "confidence": 0.95
                            }
                        }
                        """
                    )
                }
            )
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "400",
            description = "Invalid request parameters",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ApiResponse.class),
                examples = {
                    @ExampleObject(
                        value = """
                        {
                            "success": false,
                            "message": "Invalid recommendation type. Must be one of: CART_BASED, SIMILAR, POPULAR",
                            "data": null
                        }
                        """
                    )
                }
            )
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "500",
            description = "Internal server error",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ApiResponse.class),
                examples = {
                    @ExampleObject(
                        value = """
                        {
                            "success": false,
                            "message": "Error processing ML recommendations: Unable to fetch product data from catalog service",
                            "data": null
                        }
                        """
                    )
                }
            )
        )
    })
    public ResponseEntity<ApiResponse<RecommendationResponse>> getMLRecommendations(
            @RequestBody RecommendationRequest request) {
        
        // Validate request
        if (request.getRecommendationType() == null) {
            request.setRecommendationType("CART_BASED");
        }
        
        if (request.getLimit() == null || request.getLimit() <= 0) {
            request.setLimit(10);
        }
        
        if (request.getLimit() > 50) {
            request.setLimit(50); // Cap at 50 recommendations
        }
        
        // Validate recommendation type
        String type = request.getRecommendationType().toUpperCase();
        if (!type.matches("CART_BASED|SIMILAR|POPULAR")) {
            return ResponseEntity.badRequest()
                .body(new ApiResponse<>(
                    false,
                    "Invalid recommendation type. Must be one of: CART_BASED, SIMILAR, POPULAR",
                    null
                ));
        }
        
        // Validate required parameters
        if ("CART_BASED".equals(type) && request.getUserId() == null) {
            return ResponseEntity.badRequest()
                .body(new ApiResponse<>(
                    false,
                    "user_id is required for CART_BASED recommendations",
                    null
                ));
        }
        
        if ("SIMILAR".equals(type) && request.getProductId() == null) {
            return ResponseEntity.badRequest()
                .body(new ApiResponse<>(
                    false,
                    "product_id is required for SIMILAR recommendations",
                    null
                ));
        }
        
        try {
            RecommendationResponse response = mlRecommendationService.getRecommendations(request);
            return ResponseEntity.ok(new ApiResponse<>(
                true,
                "ML recommendations retrieved successfully",
                response
            ));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                .body(new ApiResponse<>(
                    false,
                    "Error processing ML recommendations: " + e.getMessage(),
                    null
                ));
        }
    }
} 