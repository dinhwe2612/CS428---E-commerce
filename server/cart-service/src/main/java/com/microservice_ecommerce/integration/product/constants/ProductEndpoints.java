package com.microservice_ecommerce.integration.product.constants;

public class ProductEndpoints {
    
    private static final String BASE_PATH = "/api/v1";
    private static final String INTERNAL_PATH = BASE_PATH + "/internal/products";
    
    public static String getProductDetail(String productId) {
        return BASE_PATH + INTERNAL_PATH + "/products/" + productId;
    }
    
    private ProductEndpoints() {
        throw new AssertionError("Utility class should not be instantiated");
    }
}