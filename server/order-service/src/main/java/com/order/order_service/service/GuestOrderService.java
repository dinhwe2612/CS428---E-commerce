package com.order.order_service.service;

import java.util.List;

import com.order.order_service.DTOs.GuestOrderRequestDTO;
import com.order.order_service.DTOs.GuestOrderResponseDTO;

public interface GuestOrderService {
    GuestOrderResponseDTO createGuestOrder(GuestOrderRequestDTO orderRequest);
    GuestOrderResponseDTO getGuestOrderById(Long id);
    List<GuestOrderResponseDTO> getGuestOrdersByStatus(String status);
    List<GuestOrderResponseDTO> getGuestOrdersByEmail(String email);
    GuestOrderResponseDTO updateGuestOrderStatus(Long id, String status);
    GuestOrderResponseDTO updateGuestOrderDeliveryStatus(Long id, String deliveryStatus);
    List<GuestOrderResponseDTO> getAllGuestOrders();
} 