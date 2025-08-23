package com.catalog.catalog_service.repository.jpa;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.catalog.catalog_service.model.PricingRuleCategory;

@Repository
public interface PricingRuleCategoryRepository extends JpaRepository<PricingRuleCategory, Long> {
    
    List<PricingRuleCategory> findByPricingRuleId(Long pricingRuleId);
    
    List<PricingRuleCategory> findByCategoryId(Long categoryId);
    
    void deleteByPricingRuleId(Long pricingRuleId);
    
    @Query("SELECT prc FROM PricingRuleCategory prc WHERE prc.categoryId IN :categoryIds")
    List<PricingRuleCategory> findByCategoryIdIn(@Param("categoryIds") List<Long> categoryIds);
}
