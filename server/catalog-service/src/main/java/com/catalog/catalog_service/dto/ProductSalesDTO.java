package com.catalog.catalog_service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductSalesDTO {
    private Long productId;
    private String productName;
    private Integer quantitySold;
    private Double totalRevenue;
    private Double unitPrice;
} 