package com.catalog.catalog_service.mapper;

import com.catalog.catalog_service.dto.CategoryDTO;
import com.catalog.catalog_service.dto.ProductDTO;
import com.catalog.catalog_service.dto.InventoryDTO;
import com.catalog.catalog_service.dto.ProductImageDTO;
import com.catalog.catalog_service.dto.PricingRuleDTO;
import com.catalog.catalog_service.dto.request.CreateCategoryRequest;
import com.catalog.catalog_service.dto.request.CreateProductImageRequest;
import com.catalog.catalog_service.model.Category;
import com.catalog.catalog_service.model.Inventory;
import com.catalog.catalog_service.model.Product;
import com.catalog.catalog_service.model.ProductImage;
import com.catalog.catalog_service.model.PricingRule;
import com.catalog.catalog_service.model.PricingRuleProduct;
import com.catalog.catalog_service.model.PricingRuleCategory;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class EntityMapper {

    public CategoryDTO toCategoryDTO(Category category) {
        if (category == null) return null;
        
        CategoryDTO dto = new CategoryDTO();
        dto.setId(category.getId());
        dto.setName(category.getName());
        dto.setDescription(category.getDescription());
        dto.setTitle(category.getTitle());
        dto.setCategoryPath(category.getCategoryPath());
        dto.setImageUrl(category.getImageUrl());
        return dto;
    }

    public Category toCategory(CategoryDTO dto) {
        if (dto == null) return null;

        Category category = new Category();
        category.setId(dto.getId());
        category.setName(dto.getName());
        category.setDescription(dto.getDescription());
        category.setTitle(dto.getTitle());
        category.setCategoryPath(dto.getCategoryPath());
        category.setImageUrl(dto.getImageUrl());
        return category;
    }

    public Category toCategory(CreateCategoryRequest request) {
        Category category = new Category();
        category.setName(request.getName());
        category.setDescription(request.getDescription());
        category.setTitle(request.getTitle());
        category.setCategoryPath(request.getCategoryPath());
        category.setImageUrl(request.getImageUrl());
        return category;
    }

    public ProductDTO toProductDTO(Product product) {
        if (product == null) {
            return null;
        }

        ProductDTO dto = new ProductDTO();
        dto.setId(product.getId());
        dto.setCategoryId(product.getCategory().getId());
        dto.setCategoryName(product.getCategory().getName());
        dto.setProductPath(product.getProductPath());
        dto.setName(product.getName());
        dto.setStatus(product.getStatus());
        dto.setPrice(product.getPrice());
        dto.setDescriptionHtml(product.getDescriptionHtml());
        dto.setDescriptionText(product.getDescriptionText());

        List<String> urls = new ArrayList<>();
        if (product.getImages() != null) {
            for (ProductImage img : product.getImages()) {
                urls.add(img.getImageUrl());
            }
        }
        dto.setImageUrls(urls);

        return dto;
    }

    public ProductDTO toProductDTOWithInventory(Product product, List<Inventory> inventories) {
        if (product == null) {
            return null;
        }

        ProductDTO dto = new ProductDTO();
        dto.setId(product.getId());
        dto.setCategoryId(product.getCategory().getId());
        dto.setCategoryName(product.getCategory().getName());
        dto.setProductPath(product.getProductPath());
        dto.setName(product.getName());
        dto.setStatus(product.getStatus());
        dto.setPrice(product.getPrice());
        dto.setDescriptionHtml(product.getDescriptionHtml());
        dto.setDescriptionText(product.getDescriptionText());

        List<String> urls = new ArrayList<>();
        if (product.getImages() != null) {
            for (ProductImage img : product.getImages()) {
                urls.add(img.getImageUrl());
            }
        }
        dto.setImageUrls(urls);

        List<InventoryDTO> inventoryDTOs = new ArrayList<>();
        if (inventories != null) {
            for (Inventory inventory : inventories) {
                inventoryDTOs.add(toInventoryDTO(inventory));
            }
        }
        dto.setInventories(inventoryDTOs);

        return dto;
    }

    public ProductDTO toProductDTOForList(Product product) {
        if (product == null) {
            return null;
        }

        ProductDTO dto = new ProductDTO();
        dto.setId(product.getId());
        dto.setCategoryId(product.getCategory().getId());
        dto.setCategoryName(product.getCategory().getName());
        dto.setProductPath(product.getProductPath());
        dto.setName(product.getName());
        dto.setStatus(product.getStatus());
        dto.setPrice(product.getPrice());

        List<String> urls = new ArrayList<>();
        if (product.getImages() != null) {
            for (ProductImage img : product.getImages()) {
                urls.add(img.getImageUrl());
            }
        }
        dto.setImageUrls(urls);

        return dto;
    }

    public Product toProduct(ProductDTO dto) {
        if (dto == null) {
            return null;
        }

        Product product = new Product();
        product.setId(dto.getId());

        // Associate the category by ID only; full Category object will be managed by JPA
        Category category = new Category();
        category.setId(dto.getCategoryId());
        product.setCategory(category);

        product.setProductPath(dto.getProductPath());
        product.setName(dto.getName());
        product.setPrice(dto.getPrice());
        product.setDescriptionHtml(dto.getDescriptionHtml());
        product.setDescriptionText(dto.getDescriptionText());

        // Map image URLs into ProductImage entities
        List<ProductImage> images = new ArrayList<>();
        if (dto.getImageUrls() != null) {
            for (int i = 0; i < dto.getImageUrls().size(); i++) {
                String url = dto.getImageUrls().get(i);
                ProductImage img = new ProductImage();
                img.setImageUrl(url);
                img.setImageOrder(i);
                img.setProduct(product);
                images.add(img);
            }
        }
        product.setImages(images);

        return product;
    }

    public InventoryDTO toInventoryDTO(Inventory inventory) {
        if (inventory == null) {
            return null;
        }

        InventoryDTO dto = new InventoryDTO();
        dto.setId(inventory.getId());
        
        if (inventory.getProduct() != null) {
            dto.setProductId(inventory.getProduct().getId());
        }
        
        dto.setQuantity(inventory.getQuantity());
        dto.setArrivalDate(inventory.getArrivalDate());
        dto.setDescription(inventory.getDescription());
        dto.setVersion(inventory.getVersion());
        return dto;
    }

    public Inventory toInventory(InventoryDTO dto) {
        if (dto == null) {
            return null;
        }

        Inventory inventory = new Inventory();
        inventory.setId(dto.getId());

        // Associate the product by ID only; JPA will manage the relationship
        Product product = new Product();
        product.setId(dto.getProductId());
        inventory.setProduct(product);

        inventory.setQuantity(dto.getQuantity());
        inventory.setArrivalDate(dto.getArrivalDate());
        inventory.setDescription(dto.getDescription());
        // Do not set version manually; JPA will handle it on persistence

        return inventory;
    }

    public ProductImageDTO toProductImageDTO(ProductImage productImage) {
        if (productImage == null) {
            return null;
        }
        ProductImageDTO dto = new ProductImageDTO();
        dto.setId(productImage.getId());
        dto.setImageUrl(productImage.getImageUrl());
        dto.setImageOrder(productImage.getImageOrder());
        dto.setProductId(productImage.getProduct().getId());
        return dto;
    }

    public PricingRuleDTO toPricingRuleDTO(PricingRule pricingRule) {
        if (pricingRule == null) {
            return null;
        }
        
        PricingRuleDTO dto = new PricingRuleDTO();
        dto.setId(pricingRule.getId());
        
        if (pricingRule.getPricingRuleProducts() != null) {
            List<Long> productIds = pricingRule.getPricingRuleProducts().stream()
                    .map(prp -> prp.getProduct().getId())
                    .collect(Collectors.toList());
            dto.setProductIds(productIds);
        } else {
            dto.setProductIds(new ArrayList<>());
        }
        
        if (pricingRule.getPricingRuleCategories() != null) {
            List<Long> categoryIds = pricingRule.getPricingRuleCategories().stream()
                    .map(PricingRuleCategory::getCategoryId)
                    .collect(Collectors.toList());
            dto.setCategoryIds(categoryIds);
        } else {
            dto.setCategoryIds(new ArrayList<>());
        }
        
        dto.setRuleName(pricingRule.getRuleName());
        dto.setDescription(pricingRule.getDescription());
        dto.setTriggerType(pricingRule.getTriggerType());
        dto.setStartDate(pricingRule.getStartDate());
        dto.setEndDate(pricingRule.getEndDate());
        dto.setStartTime(pricingRule.getStartTime());
        dto.setEndTime(pricingRule.getEndTime());
        dto.setSpecialDayName(pricingRule.getSpecialDayName());
        dto.setProductCondition(pricingRule.getProductCondition());
        dto.setType(pricingRule.getType());
        dto.setModifierValue(pricingRule.getModifierValue());
        dto.setMaxDiscountAmount(pricingRule.getMaxDiscountAmount());
        dto.setMinPrice(pricingRule.getMinPrice());
        dto.setPriority(pricingRule.getPriority());
        dto.setIsActive(pricingRule.getIsActive());
        dto.setApplyToAllProducts(pricingRule.getApplyToAllProducts());
        dto.setCreatedAt(pricingRule.getCreatedAt());
        dto.setUpdatedAt(pricingRule.getUpdatedAt());
        dto.setVersion(pricingRule.getVersion());
        
        return dto;
    }
}