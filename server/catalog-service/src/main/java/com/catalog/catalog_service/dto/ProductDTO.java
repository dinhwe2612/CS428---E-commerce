package com.catalog.catalog_service.dto;

import java.util.List;

import com.catalog.catalog_service.model.ProductStatus;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductDTO {
    private Long id;
    private Long categoryId;
    private String productPath;
    private String name;
    private ProductStatus status;
    private String price;
    private String descriptionHtml;
    private String descriptionText;
    private List<String> imageUrls;
}