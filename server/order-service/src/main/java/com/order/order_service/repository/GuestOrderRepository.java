package com.order.order_service.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.order.order_service.model.GuestOrder;

@Repository
public interface GuestOrderRepository extends JpaRepository<GuestOrder, Long> {
    List<GuestOrder> findByStatus(String status);
    List<GuestOrder> findByCustomerEmail(String customerEmail);
} 