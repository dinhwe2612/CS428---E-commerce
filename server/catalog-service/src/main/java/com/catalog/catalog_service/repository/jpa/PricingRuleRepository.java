package com.catalog.catalog_service.repository.jpa;

import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.catalog.catalog_service.model.PricingRule;

@Repository
public interface PricingRuleRepository extends JpaRepository<PricingRule, Long> {
    
    List<PricingRule> findByIsActiveTrueOrderByPriorityAsc();
    
    List<PricingRule> findByApplyToAllProductsTrueAndIsActiveTrueOrderByPriorityAsc();
    
    @Query("SELECT DISTINCT pr FROM PricingRule pr JOIN pr.pricingRuleProducts prp WHERE prp.product.id = :productId AND pr.isActive = true ORDER BY pr.priority ASC")
    List<PricingRule> findByProductIdAndIsActiveTrueOrderByPriorityAsc(@Param("productId") Long productId);
    
    @Query("SELECT DISTINCT pr FROM PricingRule pr JOIN pr.pricingRuleCategories prc WHERE prc.categoryId = :categoryId AND pr.isActive = true ORDER BY pr.priority ASC")
    List<PricingRule> findByCategoryIdAndIsActiveTrueOrderByPriorityAsc(@Param("categoryId") Long categoryId);
    
    @Query("SELECT DISTINCT pr FROM PricingRule pr LEFT JOIN pr.pricingRuleProducts prp LEFT JOIN pr.pricingRuleCategories prc WHERE pr.isActive = true AND (pr.applyToAllProducts = true OR prp.product.id IN :productIds OR prc.categoryId IN :categoryIds) ORDER BY pr.priority ASC")
    List<PricingRule> findByProductIdsOrCategoryIdsAndIsActiveTrueOrderByPriorityAsc(@Param("productIds") List<Long> productIds, @Param("categoryIds") List<Long> categoryIds);
    
    @Query("SELECT DISTINCT pr FROM PricingRule pr LEFT JOIN pr.pricingRuleProducts prp LEFT JOIN pr.pricingRuleCategories prc WHERE pr.isActive = true AND (pr.applyToAllProducts = true OR prp.product.id = :productId OR prc.categoryId = :categoryId) ORDER BY pr.priority ASC")
    List<PricingRule> findApplicableRulesForProduct(@Param("productId") Long productId, 
                                                   @Param("categoryId") Long categoryId);
    
    @Query("SELECT pr FROM PricingRule pr WHERE pr.isActive = true " +
           "AND pr.triggerType = 'TIME_OF_DAY' " +
           "AND ((pr.startTime <= pr.endTime AND :currentTime BETWEEN pr.startTime AND pr.endTime) " +
           "OR (pr.startTime > pr.endTime AND (:currentTime >= pr.startTime OR :currentTime <= pr.endTime))) " +
           "ORDER BY pr.priority ASC")
    List<PricingRule> findActiveTimeBasedRules(@Param("currentTime") LocalTime currentTime);
    
    @Query("SELECT pr FROM PricingRule pr WHERE pr.isActive = true " +
           "AND pr.triggerType = 'SPECIAL_DAY' " +
           "AND pr.specialDayName = :specialDay " +
           "AND CURRENT_DATE BETWEEN DATE(pr.startDate) AND DATE(pr.endDate) " +
           "ORDER BY pr.priority ASC")
    List<PricingRule> findActiveSpecialDayRules(@Param("specialDay") String specialDay);
    
    @Query("SELECT pr FROM PricingRule pr " +
           "WHERE (:ruleName IS NULL OR pr.ruleName LIKE %:ruleName%) " +
           "AND (:isActive IS NULL OR pr.isActive = :isActive) " +
           "AND (:triggerType IS NULL OR pr.triggerType = :triggerType)")
    Page<PricingRule> findWithFilters(@Param("ruleName") String ruleName,
                                     @Param("isActive") Boolean isActive,
                                     @Param("triggerType") PricingRule.TriggerType triggerType,
                                     Pageable pageable);
    
    @Query("SELECT DISTINCT pr FROM PricingRule pr " +
           "LEFT JOIN FETCH pr.pricingRuleProducts " +
           "LEFT JOIN FETCH pr.pricingRuleCategories " +
           "WHERE pr.id = :id")
    Optional<PricingRule> findByIdWithRelationships(@Param("id") Long id);
    
    @Query("SELECT DISTINCT pr FROM PricingRule pr " +
           "LEFT JOIN FETCH pr.pricingRuleProducts " +
           "LEFT JOIN FETCH pr.pricingRuleCategories " +
           "WHERE pr.isActive = true " +
           "ORDER BY pr.priority ASC")
    List<PricingRule> findByIsActiveTrueWithRelationshipsOrderByPriorityAsc();


}
