package com.order.order_service.service;

import java.util.List;

import com.order.order_service.DTOs.OrderRequestDTO;
import com.order.order_service.DTOs.OrderResponseDTO;
import com.order.order_service.DTOs.ProductDTO;

public interface OrderService {
    OrderResponseDTO createOrder(OrderRequestDTO orderRequest);
    OrderResponseDTO getOrderById(Long id);
    List<OrderResponseDTO> getOrdersByUserId(String userId);
    List<OrderResponseDTO> getOrdersByStatus(String status);
    OrderResponseDTO updateOrderStatus(Long id, String status);
    void deleteOrder(Long id);
    OrderResponseDTO updateOrderMessage(Long id, String message);
    List<ProductDTO> getProductsByIds(List<Long> ids);
    List<OrderResponseDTO> getAllOrders();
    OrderResponseDTO updateDeliveryStatus(Long id, String deliveryStatus);
} 