package com.payment.payment_service.repository;

import com.payment.payment_service.model.Payment;
import com.payment.payment_service.model.PaymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
    
    Optional<Payment> findByTransactionId(String transactionId);
    
    List<Payment> findByOrderId(Long orderId);
    
    List<Payment> findByUserId(String userId);
    
    List<Payment> findByStatus(PaymentStatus status);
    
    List<Payment> findByUserIdAndStatus(String userId, PaymentStatus status);
    
    Optional<Payment> findByGatewayTransactionId(String gatewayTransactionId);
    
    boolean existsByTransactionId(String transactionId);
} 