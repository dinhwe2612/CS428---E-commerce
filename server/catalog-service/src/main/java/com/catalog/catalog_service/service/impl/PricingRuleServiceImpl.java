package com.catalog.catalog_service.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.catalog.catalog_service.dto.PageDTO;
import com.catalog.catalog_service.dto.PricingRuleDTO;
import com.catalog.catalog_service.dto.request.CreatePricingRuleRequest;
import com.catalog.catalog_service.dto.request.UpdatePricingRuleRequest;
import com.catalog.catalog_service.exception.ResourceNotFoundException;
import com.catalog.catalog_service.mapper.EntityMapper;
import com.catalog.catalog_service.model.Inventory;
import com.catalog.catalog_service.model.PricingRule;
import com.catalog.catalog_service.model.Product;
import com.catalog.catalog_service.repository.jpa.InventoryRepository;
import com.catalog.catalog_service.repository.jpa.PricingRuleRepository;
import com.catalog.catalog_service.repository.jpa.ProductRepository;
import com.catalog.catalog_service.service.PricingRuleService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PricingRuleServiceImpl implements PricingRuleService {
    
    private static final Logger logger = LoggerFactory.getLogger(PricingRuleServiceImpl.class);
    
    private final PricingRuleRepository pricingRuleRepository;
    private final ProductRepository productRepository;
    private final InventoryRepository inventoryRepository;
    private final EntityMapper entityMapper;
    
    @Override
    public PageDTO<PricingRuleDTO> getAllPricingRules(Pageable pageable, String ruleName, Boolean isActive, PricingRule.TriggerType triggerType) {
        Page<PricingRule> pricingRulePage = pricingRuleRepository.findWithFilters(ruleName, isActive, triggerType, pageable);
        
        List<PricingRuleDTO> pricingRuleDTOs = pricingRulePage.getContent().stream()
                .map(entityMapper::toPricingRuleDTO)
                .collect(Collectors.toList());
        
        return new PageDTO<>(
            pricingRuleDTOs,
            pricingRulePage.getNumber(),
            pricingRulePage.getSize(),
            pricingRulePage.getTotalElements(),
            pricingRulePage.getTotalPages(),
            pricingRulePage.isLast(),
            pricingRulePage.isFirst()
        );
    }
    
    @Override
    public PricingRuleDTO getPricingRuleById(Long id) {
        PricingRule pricingRule = pricingRuleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Pricing rule not found with id: " + id));
        return entityMapper.toPricingRuleDTO(pricingRule);
    }
    
    @Override
    @Transactional
    public PricingRuleDTO createPricingRule(CreatePricingRuleRequest request) {
        PricingRule pricingRule = new PricingRule();
        mapRequestToEntity(request, pricingRule);
        
        if (request.getProductId() != null) {
            Product product = productRepository.findById(request.getProductId())
                    .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + request.getProductId()));
            pricingRule.setProduct(product);
        }
        
        if (request.getInventoryId() != null) {
            Inventory inventory = inventoryRepository.findById(request.getInventoryId())
                    .orElseThrow(() -> new ResourceNotFoundException("Inventory not found with id: " + request.getInventoryId()));
            pricingRule.setInventory(inventory);
        }
        
        PricingRule savedPricingRule = pricingRuleRepository.save(pricingRule);
        logger.info("Created pricing rule with id: {}", savedPricingRule.getId());
        
        return entityMapper.toPricingRuleDTO(savedPricingRule);
    }
    
    @Override
    @Transactional
    public PricingRuleDTO updatePricingRule(Long id, UpdatePricingRuleRequest request) {
        PricingRule pricingRule = pricingRuleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Pricing rule not found with id: " + id));
        
        mapUpdateRequestToEntity(request, pricingRule);
        
        if (request.getProductId() != null) {
            Product product = productRepository.findById(request.getProductId())
                    .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + request.getProductId()));
            pricingRule.setProduct(product);
        }
        
        if (request.getInventoryId() != null) {
            Inventory inventory = inventoryRepository.findById(request.getInventoryId())
                    .orElseThrow(() -> new ResourceNotFoundException("Inventory not found with id: " + request.getInventoryId()));
            pricingRule.setInventory(inventory);
        }
        
        PricingRule savedPricingRule = pricingRuleRepository.save(pricingRule);
        logger.info("Updated pricing rule with id: {}", savedPricingRule.getId());
        
        return entityMapper.toPricingRuleDTO(savedPricingRule);
    }
    
    @Override
    @Transactional
    public void deletePricingRule(Long id) {
        if (!pricingRuleRepository.existsById(id)) {
            throw new ResourceNotFoundException("Pricing rule not found with id: " + id);
        }
        pricingRuleRepository.deleteById(id);
        logger.info("Deleted pricing rule with id: {}", id);
    }
    
    @Override
    public List<PricingRuleDTO> getActivePricingRules() {
        List<PricingRule> pricingRules = pricingRuleRepository.findByIsActiveTrueOrderByPriorityAsc();
        return pricingRules.stream()
                .map(entityMapper::toPricingRuleDTO)
                .collect(Collectors.toList());
    }
    
    @Override
    public List<PricingRuleDTO> getPricingRulesByProductId(Long productId) {
        List<PricingRule> pricingRules = pricingRuleRepository.findByProductIdAndIsActiveTrueOrderByPriorityAsc(productId);
        return pricingRules.stream()
                .map(entityMapper::toPricingRuleDTO)
                .collect(Collectors.toList());
    }
    
    @Override
    public List<PricingRuleDTO> getPricingRulesByCategoryId(Long categoryId) {
        List<PricingRule> pricingRules = pricingRuleRepository.findByCategoryIdAndIsActiveTrueOrderByPriorityAsc(categoryId);
        return pricingRules.stream()
                .map(entityMapper::toPricingRuleDTO)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional
    public void activatePricingRule(Long id) {
        PricingRule pricingRule = pricingRuleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Pricing rule not found with id: " + id));
        pricingRule.setIsActive(true);
        pricingRuleRepository.save(pricingRule);
        logger.info("Activated pricing rule with id: {}", id);
    }
    
    @Override
    @Transactional
    public void deactivatePricingRule(Long id) {
        PricingRule pricingRule = pricingRuleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Pricing rule not found with id: " + id));
        pricingRule.setIsActive(false);
        pricingRuleRepository.save(pricingRule);
        logger.info("Deactivated pricing rule with id: {}", id);
    }
    
    @Override
    public List<PricingRuleDTO> getApplicableRulesForProduct(Long productId, Long categoryId) {
        List<PricingRule> pricingRules = pricingRuleRepository.findApplicableRulesForProduct(productId, categoryId);
        return pricingRules.stream()
                .map(entityMapper::toPricingRuleDTO)
                .collect(Collectors.toList());
    }
    
    private void mapRequestToEntity(CreatePricingRuleRequest request, PricingRule pricingRule) {
        pricingRule.setRuleName(request.getRuleName());
        pricingRule.setDescription(request.getDescription());
        pricingRule.setTriggerType(request.getTriggerType());
        pricingRule.setStartDate(request.getStartDate());
        pricingRule.setEndDate(request.getEndDate());
        pricingRule.setStartTime(request.getStartTime());
        pricingRule.setEndTime(request.getEndTime());
        pricingRule.setSpecialDayName(request.getSpecialDayName());
        pricingRule.setProductCondition(request.getProductCondition());
        pricingRule.setType(request.getType());
        pricingRule.setModifierValue(request.getModifierValue());
        pricingRule.setMaxDiscountAmount(request.getMaxDiscountAmount());
        pricingRule.setMinPrice(request.getMinPrice());
        pricingRule.setPriority(request.getPriority());
        pricingRule.setIsActive(request.getIsActive());
        pricingRule.setApplyToAllProducts(request.getApplyToAllProducts());
        pricingRule.setCategoryId(request.getCategoryId());
    }
    
    private void mapUpdateRequestToEntity(UpdatePricingRuleRequest request, PricingRule pricingRule) {
        if (request.getRuleName() != null) {
            pricingRule.setRuleName(request.getRuleName());
        }
        if (request.getDescription() != null) {
            pricingRule.setDescription(request.getDescription());
        }
        if (request.getTriggerType() != null) {
            pricingRule.setTriggerType(request.getTriggerType());
        }
        if (request.getStartDate() != null) {
            pricingRule.setStartDate(request.getStartDate());
        }
        if (request.getEndDate() != null) {
            pricingRule.setEndDate(request.getEndDate());
        }
        if (request.getStartTime() != null) {
            pricingRule.setStartTime(request.getStartTime());
        }
        if (request.getEndTime() != null) {
            pricingRule.setEndTime(request.getEndTime());
        }
        if (request.getSpecialDayName() != null) {
            pricingRule.setSpecialDayName(request.getSpecialDayName());
        }
        if (request.getProductCondition() != null) {
            pricingRule.setProductCondition(request.getProductCondition());
        }
        if (request.getType() != null) {
            pricingRule.setType(request.getType());
        }
        if (request.getModifierValue() != null) {
            pricingRule.setModifierValue(request.getModifierValue());
        }
        if (request.getMaxDiscountAmount() != null) {
            pricingRule.setMaxDiscountAmount(request.getMaxDiscountAmount());
        }
        if (request.getMinPrice() != null) {
            pricingRule.setMinPrice(request.getMinPrice());
        }
        if (request.getPriority() != null) {
            pricingRule.setPriority(request.getPriority());
        }
        if (request.getIsActive() != null) {
            pricingRule.setIsActive(request.getIsActive());
        }
        if (request.getApplyToAllProducts() != null) {
            pricingRule.setApplyToAllProducts(request.getApplyToAllProducts());
        }
        if (request.getCategoryId() != null) {
            pricingRule.setCategoryId(request.getCategoryId());
        }
    }
}
