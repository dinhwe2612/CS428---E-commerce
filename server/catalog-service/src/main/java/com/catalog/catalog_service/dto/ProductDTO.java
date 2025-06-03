package com.catalog.catalog_service.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductDTO {
    private Long id;
    private Long categoryId;
    private String productPath;
    private String name;
    private String price;
    private String descriptionHtml;
    private String descriptionText;
    private List<String> imageUrls;
}