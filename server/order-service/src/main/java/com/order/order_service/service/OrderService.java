package com.order.order_service.service;

import com.order.order_service.DTOs.OrderRequestDTO;
import com.order.order_service.DTOs.OrderResponseDTO;

import java.util.List;

public interface OrderService {
    OrderResponseDTO createOrder(OrderRequestDTO orderRequest);
    OrderResponseDTO getOrderById(Long id);
    List<OrderResponseDTO> getOrdersByUserId(String userId);
    List<OrderResponseDTO> getOrdersByStatus(String status);
    OrderResponseDTO updateOrderStatus(Long id, String status);
    void deleteOrder(Long id);
} 