package com.catalog.catalog_service.integration.order.constants;

public final class OrderEndpoints {
    public static final String API_PATH = "/api/v1/internal";
    public static final String ORDERS_PATH = API_PATH + "/orders";
    
    public static String getOrderById(Long orderId) {
        return ORDERS_PATH + "/" + orderId;
    }
    
    public static String getOrdersByStatus(String status) {
        return "/api/v1/orders/status/" + status;
    }
    
    public static String getAllOrders() {
        return "/api/v1/orders";
    }
    
    private OrderEndpoints() {
        throw new AssertionError("Utility class should not be instantiated");
    }
}