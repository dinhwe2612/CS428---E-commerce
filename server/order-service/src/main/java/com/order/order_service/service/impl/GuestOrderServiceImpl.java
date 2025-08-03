package com.order.order_service.service.impl;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.order.order_service.DTOs.GuestOrderItemResponseDTO;
import com.order.order_service.DTOs.GuestOrderRequestDTO;
import com.order.order_service.DTOs.GuestOrderResponseDTO;
import com.order.order_service.DTOs.ProductDTO;
import com.order.order_service.exception.GuestOrderNotFoundException;
import com.order.order_service.exception.ProductIDNotFoundException;
import com.order.order_service.integration.catalog.client.CatalogServiceClient;
import com.order.order_service.model.GuestOrder;
import com.order.order_service.model.GuestOrderItem;
import com.order.order_service.repository.GuestOrderRepository;
import com.order.order_service.service.GuestOrderMessageProducer;
import com.order.order_service.service.GuestOrderService;

@Service
public class GuestOrderServiceImpl implements GuestOrderService {

    @Autowired
    private GuestOrderRepository guestOrderRepository;

    @Autowired
    private CatalogServiceClient catalogServiceClient;

    @Autowired
    private GuestOrderMessageProducer messageProducer;

    @Override
    @Transactional
    public GuestOrderResponseDTO createGuestOrder(GuestOrderRequestDTO orderRequest) {
        GuestOrder order = new GuestOrder();
        order.setCustomerName(orderRequest.getCustomerName());
        order.setCustomerEmail(orderRequest.getCustomerEmail());
        order.setCustomerPhone(orderRequest.getCustomerPhone());
        order.setShippingAddress(orderRequest.getShippingAddress());
        order.setPaymentMethod(orderRequest.getPaymentMethod());
        order.setStatus("PENDING");
        order.setTotalAmount(orderRequest.getTotalAmount());
        order.setMessage(orderRequest.getMessage());
        
        //create a hashset of product ids
        Set<Long> productIds = new HashSet<>();
        HashMap<Long,ProductDTO> productMap = new HashMap<>();
        
        //validate productids optimizelyy
        List<GuestOrderItem> orderItems = orderRequest.getOrderItems().stream()
                .map(item -> {
                    GuestOrderItem orderItem = new GuestOrderItem();
                    orderItem.setGuestOrder(order);
                    orderItem.setProductId(item.getProductId());
                    orderItem.setQuantity(item.getQuantity());
                    orderItem.setProductName("Product " + item.getProductId()); // Temporary, should be fetched from product service
                    orderItem.setUnitPrice(0.0); // Should be fetched from product service
                    orderItem.setSubtotal(item.getQuantity() * 0.0); // Should be calculated based on unit price
                    orderItem.setInventoryId(item.getInventoryId());
                    productIds.add(Long.parseLong(item.getProductId()));
                   
                    return orderItem;
                })
                .collect(Collectors.toList());
        
        List<ProductDTO> productresp = catalogServiceClient.getProductsByIds(productIds.stream().collect(Collectors.toList()));
        //check if the product ids are valid
        if(productresp.size() != productIds.size()){
            throw new ProductIDNotFoundException("Invalid product ids");
        } else {
            for (ProductDTO productDTO : productresp) {
                if (!productIds.contains(productDTO.getId())) {
                    throw new ProductIDNotFoundException("Invalid product ids");
                }
                productMap.put(productDTO.getId(),productDTO);
            }
        }
        
        for(GuestOrderItem orderItem : orderItems){
            orderItem.setProductName(productMap.get(Long.parseLong(orderItem.getProductId())).getName());
            orderItem.setUnitPrice(productMap.get(Long.parseLong(orderItem.getProductId())).getPrice());
            orderItem.setSubtotal(orderItem.getQuantity() * productMap.get(Long.parseLong(orderItem.getProductId())).getPrice());
        }

        order.setOrderItems(orderItems);
        
        GuestOrder savedOrder = guestOrderRepository.save(order);
    
        GuestOrderResponseDTO orderResponse = convertToDTO(savedOrder);
        
        // Send event
        messageProducer.sendGuestOrderCreatedEvent(orderResponse);
        
        return orderResponse;
    }

    @Override
    public GuestOrderResponseDTO getGuestOrderById(Long id) {
        GuestOrder order = guestOrderRepository.findById(id)
                .orElseThrow(() -> new GuestOrderNotFoundException(id));
        return convertToDTO(order);
    }

    @Override
    public List<GuestOrderResponseDTO> getGuestOrdersByStatus(String status) {
        return guestOrderRepository.findByStatus(status)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<GuestOrderResponseDTO> getGuestOrdersByEmail(String email) {
        return guestOrderRepository.findByCustomerEmail(email)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public GuestOrderResponseDTO updateGuestOrderStatus(Long id, String status) {
        GuestOrder order = guestOrderRepository.findById(id)
                .orElseThrow(() -> new GuestOrderNotFoundException(id));
        order.setStatus(status);
        GuestOrder updatedOrder = guestOrderRepository.save(order);
        GuestOrderResponseDTO orderResponse = convertToDTO(updatedOrder);
        
        messageProducer.sendGuestOrderStatusUpdatedEvent(orderResponse);
        
        return orderResponse;
    }

    @Override
    @Transactional
    public GuestOrderResponseDTO updateGuestOrderDeliveryStatus(Long id, String deliveryStatus) {
        GuestOrder order = guestOrderRepository.findById(id)
                .orElseThrow(() -> new GuestOrderNotFoundException(id));
        order.setDeliveryStatus(deliveryStatus);
        GuestOrder updatedOrder = guestOrderRepository.save(order);
        return convertToDTO(updatedOrder);
    }

    @Override
    public List<GuestOrderResponseDTO> getAllGuestOrders() {
        return guestOrderRepository.findAll()
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    private GuestOrderResponseDTO convertToDTO(GuestOrder order) {
        GuestOrderResponseDTO dto = new GuestOrderResponseDTO();
        dto.setId(order.getId());
        dto.setCustomerName(order.getCustomerName());
        dto.setCustomerEmail(order.getCustomerEmail());
        dto.setCustomerPhone(order.getCustomerPhone());
        dto.setStatus(order.getStatus());
        dto.setTotalAmount(order.getTotalAmount());
        dto.setShippingAddress(order.getShippingAddress());
        dto.setPaymentMethod(order.getPaymentMethod());
        dto.setCreatedAt(order.getCreatedAt());
        dto.setUpdatedAt(order.getUpdatedAt());
        dto.setMessage(order.getMessage());
        dto.setTransactionId(order.getTransactionId());
        dto.setPaymentUrl(order.getPaymentUrl());
        dto.setDeliveryStatus(order.getDeliveryStatus());
        
        if (order.getOrderItems() != null) {
            dto.setOrderItems(order.getOrderItems().stream()
                    .map(this::convertToOrderItemDTO)
                    .collect(Collectors.toList()));
        }

        return dto;
    }

    private GuestOrderItemResponseDTO convertToOrderItemDTO(GuestOrderItem orderItem) {
        GuestOrderItemResponseDTO dto = new GuestOrderItemResponseDTO();
        dto.setId(orderItem.getId());
        dto.setProductId(orderItem.getProductId());
        dto.setProductName(orderItem.getProductName());
        dto.setQuantity(orderItem.getQuantity());
        dto.setUnitPrice(orderItem.getUnitPrice());
        dto.setSubtotal(orderItem.getSubtotal());
        dto.setInventoryId(orderItem.getInventoryId());
        return dto;
    }
} 