package com.catalog.catalog_service.service;

import java.util.List;

import org.springframework.data.domain.Pageable;

import com.catalog.catalog_service.dto.PageDTO;
import com.catalog.catalog_service.dto.PricingRuleDTO;
import com.catalog.catalog_service.dto.request.CreatePricingRuleRequest;
import com.catalog.catalog_service.dto.request.UpdatePricingRuleRequest;
import com.catalog.catalog_service.model.PricingRule;

public interface PricingRuleService {
    
    PageDTO<PricingRuleDTO> getAllPricingRules(Pageable pageable, String ruleName, Boolean isActive, PricingRule.TriggerType triggerType);
    
    PricingRuleDTO getPricingRuleById(Long id);
    
    PricingRuleDTO createPricingRule(CreatePricingRuleRequest request);
    
    PricingRuleDTO updatePricingRule(Long id, UpdatePricingRuleRequest request);
    
    void deletePricingRule(Long id);
    
    List<PricingRuleDTO> getActivePricingRules();
    
    List<PricingRuleDTO> getPricingRulesByProductId(Long productId);
    
    List<PricingRuleDTO> getPricingRulesByCategoryId(Long categoryId);
    
    void activatePricingRule(Long id);
    
    void deactivatePricingRule(Long id);
    
    List<PricingRuleDTO> getApplicableRulesForProduct(Long productId, Long categoryId);
}
