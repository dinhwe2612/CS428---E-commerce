package com.catalog.catalog_service.repository.jpa;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.catalog.catalog_service.model.PricingRuleProduct;

@Repository
public interface PricingRuleProductRepository extends JpaRepository<PricingRuleProduct, Long> {
    
    List<PricingRuleProduct> findByPricingRuleId(Long pricingRuleId);
    
    List<PricingRuleProduct> findByProductId(Long productId);
    
    @Modifying
    void deleteByPricingRuleId(Long pricingRuleId);
    
    @Query("SELECT prp FROM PricingRuleProduct prp WHERE prp.product.id IN :productIds")
    List<PricingRuleProduct> findByProductIdIn(@Param("productIds") List<Long> productIds);
    
    @Query("SELECT prp.product.id FROM PricingRuleProduct prp WHERE prp.pricingRule.id = :pricingRuleId")
    List<Long> findProductIdsByPricingRuleId(@Param("pricingRuleId") Long pricingRuleId);
}
