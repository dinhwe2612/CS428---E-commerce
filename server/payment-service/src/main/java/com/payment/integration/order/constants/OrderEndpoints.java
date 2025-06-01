package com.payment.integration.order.constants;

public final class OrderEndpoints {
    public static final String API_PATH = "/api/v1/internal";
    public static final String ORDERS_PATH = API_PATH + "/orders";
    
    public static String getOrderById(Long orderId) {
        return ORDERS_PATH + "/" + orderId;
    }
    
    private OrderEndpoints() {
        throw new AssertionError("Utility class should not be instantiated");
    }
} 