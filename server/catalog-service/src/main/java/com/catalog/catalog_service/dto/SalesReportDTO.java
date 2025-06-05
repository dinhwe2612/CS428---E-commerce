package com.catalog.catalog_service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SalesReportDTO {
    private LocalDateTime reportGeneratedAt;
    private String period;
    private Double totalRevenue;
    private Integer totalOrders;
    private Integer totalProductsSold;
    private Double averageOrderValue;
    private List<ProductSalesDTO> topSellingProducts;
    private List<DailySalesDTO> dailySales;
} 