package com.microservice_ecommerce.integration.catalog.dto.response;

import java.util.List;

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
    private String status;
    private String price;
    private String descriptionHtml;
    private String descriptionText;
    private List<String> imageUrls;
} 