package com.order.order_service.service.impl;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.order.order_service.DTOs.ListProductResponseDTO;
import com.order.order_service.DTOs.OrderItemResponseDTO;
import com.order.order_service.DTOs.OrderRequestDTO;
import com.order.order_service.DTOs.OrderResponseDTO;
import com.order.order_service.DTOs.ProductDTO;
import com.order.order_service.exception.OrderNotFoundException;
import com.order.order_service.exception.ProductIDNotFoundException;
import com.order.order_service.integration.catalog.client.CatalogServiceClient;
import com.order.order_service.model.Order;
import com.order.order_service.model.OrderItem;
import com.order.order_service.repository.OrderRepository;
import com.order.order_service.service.OrderMessageProducer;
import com.order.order_service.service.OrderService;

@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private CatalogServiceClient catalogServiceClient;

    @Autowired
    private OrderMessageProducer messageProducer;

    @Override
    @Transactional
    public OrderResponseDTO createOrder(OrderRequestDTO orderRequest) {
        Order order = new Order();
        order.setUserId(orderRequest.getUserId());
        order.setShippingAddress(orderRequest.getShippingAddress());
        order.setPaymentMethod(orderRequest.getPaymentMethod());
        order.setStatus("PENDING");
        order.setTotalAmount(orderRequest.getTotalAmount());
        //create a hashset of product ids
        Set<Long> productIds = new HashSet<>();

        //validate productids optimizelyy
        List<OrderItem> orderItems = orderRequest.getOrderItems().stream()
                .map(item -> {
                    OrderItem orderItem = new OrderItem();
                    orderItem.setOrder(order);
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
        List<ProductDTO> productresp =catalogServiceClient.getProductsByIds(productIds.stream().collect(Collectors.toList()));
        //check if the product ids are valid
        if(productresp.size() != productIds.size()){
            throw new ProductIDNotFoundException("Invalid product ids");
        } else {
            for (ProductDTO productDTO : productresp) {
                if (!productIds.contains(productDTO.getId())) {
                    throw new ProductIDNotFoundException("Invalid product ids");
                }
            }
        }

        order.setOrderItems(orderItems);
        
 
        Order savedOrder = orderRepository.save(order);
    
        OrderResponseDTO orderResponse = convertToDTO(savedOrder);
        
        // Send event
        messageProducer.sendOrderCreatedEvent(orderResponse);
        
        return orderResponse;
    }

    @Override
    public OrderResponseDTO getOrderById(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new OrderNotFoundException(id));
        return convertToDTO(order);
    }

    @Override
    public List<OrderResponseDTO> getOrdersByUserId(String userId) {
        return orderRepository.findByUserId(userId)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<OrderResponseDTO> getOrdersByStatus(String status) {
        return orderRepository.findByStatus(status)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public OrderResponseDTO updateOrderStatus(Long id, String status) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new OrderNotFoundException(id));
        order.setStatus(status);
        Order updatedOrder = orderRepository.save(order);
        OrderResponseDTO orderResponse = convertToDTO(updatedOrder);
        
     
        messageProducer.sendOrderStatusUpdatedEvent(orderResponse);
        
        return orderResponse;
    }

    @Override
    @Transactional
    public void deleteOrder(Long id) {
        if (!orderRepository.existsById(id)) {
            throw new OrderNotFoundException(id);
        }
        orderRepository.deleteById(id);
    }

    private OrderResponseDTO convertToDTO(Order order) {
        OrderResponseDTO dto = new OrderResponseDTO();
        dto.setId(order.getId());
        dto.setUserId(order.getUserId());
        dto.setStatus(order.getStatus());
        dto.setTotalAmount(order.getTotalAmount());
        dto.setShippingAddress(order.getShippingAddress());
        dto.setPaymentMethod(order.getPaymentMethod());
        dto.setCreatedAt(order.getCreatedAt());
        dto.setUpdatedAt(order.getUpdatedAt());
        
      
        if (order.getOrderItems() != null) {
            dto.setOrderItems(order.getOrderItems().stream()
                    .map(this::convertToOrderItemDTO)
                    .collect(Collectors.toList()));
        }

        return dto;
    }

    private OrderItemResponseDTO convertToOrderItemDTO(OrderItem orderItem) {
        OrderItemResponseDTO dto = new OrderItemResponseDTO();
        dto.setId(orderItem.getId());
        dto.setProductId(orderItem.getProductId());
        dto.setProductName(orderItem.getProductName());
        dto.setQuantity(orderItem.getQuantity());
        dto.setUnitPrice(orderItem.getUnitPrice());
        dto.setSubtotal(orderItem.getSubtotal());
        dto.setInventoryId(orderItem.getInventoryId());
        return dto;
    }

    @Override
    @Transactional
    public OrderResponseDTO updateOrderMessage(Long id, String message) {
        Order order = orderRepository.findById(id)
            .orElseThrow(() -> new OrderNotFoundException(id));
        order.setMessage(message);
        Order updatedOrder = orderRepository.save(order);
        return convertToDTO(updatedOrder);
    }

    @Override
    public List<ProductDTO> getProductsByIds(List<Long> ids) {
        return catalogServiceClient.getProductsByIds(ids);
    }

    @Override
    public List<OrderResponseDTO> getAllOrders() {
        return orderRepository.findAll()
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public OrderResponseDTO updateDeliveryStatus(Long id, String deliveryStatus) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new OrderNotFoundException(id));
        order.setDeliveryStatus(deliveryStatus);
        Order updatedOrder = orderRepository.save(order);
        return convertToDTO(updatedOrder);
    }
}