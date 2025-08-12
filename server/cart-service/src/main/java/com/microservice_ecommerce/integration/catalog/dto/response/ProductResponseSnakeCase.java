package com.microservice_ecommerce.integration.catalog.dto.response;

import java.math.BigDecimal;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProductResponseSnakeCase {
    private Long id;
    private String name;
    private String description;
    private Long category_id;
    private List<Long> image_ids;
    private List<String> image_urls;
    private BigDecimal price;
} 