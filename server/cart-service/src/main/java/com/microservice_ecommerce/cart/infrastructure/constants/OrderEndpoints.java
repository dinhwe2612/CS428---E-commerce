package com.microservice_ecommerce.cart.infrastructure.constants;

public final class OrderEndpoints {
    public static final String API_PATH = "/api/v1/internal";
    public static final String ORDERS_PATH = API_PATH + "/orders";
    
    public static String getOrderById(Long orderId) {
        return ORDERS_PATH + "/" + orderId;
    }
    
    public static String getOrdersByUserId(String userId) {
        return ORDERS_PATH + "/user/" + userId;
    }
    
    private OrderEndpoints() {
        throw new AssertionError("Utility class should not be instantiated");
    }
} 