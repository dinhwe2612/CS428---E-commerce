package com.catalog.catalog_service.service;

import com.catalog.catalog_service.dto.ProductSalesDTO;
import com.catalog.catalog_service.dto.SalesReportDTO;

import java.time.LocalDate;
import java.util.List;

public interface ReportService {
    SalesReportDTO generateSalesReport(LocalDate startDate, LocalDate endDate);
    SalesReportDTO generateDailySalesReport(LocalDate date);
    SalesReportDTO generateWeeklySalesReport();
    SalesReportDTO generateMonthlySalesReport();
    List<ProductSalesDTO> getBestSellingProducts(LocalDate startDate, LocalDate endDate, int limit);
} 