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

@RestController
@RequestMapping("/api/v1/cart")
@Tag(name = "AI/ML Product Recommendations", 
     description = "Smart product recommendation system that combines cart and order data using simple AI/ML algorithms")
public class RecommendationController {

    private final RecommendationService recommendationService;

    @Autowired
    public RecommendationController(RecommendationService recommendationService) {
        this.recommendationService = recommendationService;
    }

    @PostMapping("/recommendations")
    @Operation(
        summary = "Get AI-powered product recommendations",
        description = """
            This endpoint provides intelligent product recommendations using a simple AI/ML algorithm that combines:
            
            **Data Sources:**
            - User's current cart items
            - User's purchase history from orders
            - Popular products across all users
            - Category-based similarity
            
            **Algorithm Features:**
            - Collaborative filtering based on cart and order patterns
            - Category preference analysis
            - Purchase frequency weighting
            - Confidence scoring based on data availability
            
            **Recommendation Types:**
            - **CART_BASED**: Personalized recommendations based on user's cart and order history
            - **SIMILAR**: Products similar to a specific product (same category)
            - **POPULAR**: Most frequently added products across all users
            
            **Scoring System:**
            - Cart items: 30% weight (current interest)
            - Order history: 50% weight (past purchases)
            - Category preference: 20% weight (similar categories)
            
            **Confidence Levels:**
            - 0-5 interactions: 50% confidence
            - 5-20 interactions: 70% confidence
            - 20+ interactions: 90% confidence
            - New users: 30% confidence (fallback to popular products)
            """,
        requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Recommendation request parameters",
            required = true,
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = RecommendationRequest.class),
                examples = {
                    @ExampleObject(
                        name = "Cart-based recommendations",
                        value = """
                        {
                            "user_id": 123,
                            "recommendation_type": "CART_BASED",
                            "limit": 10
                        }
                        """
                    ),
                    @ExampleObject(
                        name = "Similar products",
                        value = """
                        {
                            "product_id": 456,
                            "recommendation_type": "SIMILAR",
                            "limit": 5
                        }
                        """
                    ),
                    @ExampleObject(
                        name = "Popular products",
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
            description = "Successfully retrieved recommendations",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ApiResponse.class),
                examples = {
                    @ExampleObject(
                        name = "Successful cart-based recommendations",
                        value = """
                        {
                            "success": true,
                            "message": "Recommendations retrieved successfully",
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
                                        "image_urls": ["https://example.com/rose-bouquet.jpg"],
                                        "category": "Flowers"
                                    }
                                ],
                                "reasoning": "Based on your current cart items and purchase history and similar user preferences",
                                "confidence": 0.85
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
                            "message": "Error processing recommendations: Unable to fetch product data from catalog service",
                            "data": null
                        }
                        """
                    )
                }
            )
        )
    })
    public ResponseEntity<ApiResponse<RecommendationResponse>> getRecommendations(
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
            RecommendationResponse response = recommendationService.getRecommendations(request);
            return ResponseEntity.ok(new ApiResponse<>(
                true,
                "Recommendations retrieved successfully",
                response
            ));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                .body(new ApiResponse<>(
                    false,
                    "Error processing recommendations: " + e.getMessage(),
                    null
                ));
        }
    }
} 