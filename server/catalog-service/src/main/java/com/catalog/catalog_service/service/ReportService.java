package com.catalog.catalog_service.service;

import com.catalog.catalog_service.dto.SalesReportDTO;

import java.time.LocalDate;

public interface ReportService {
    SalesReportDTO generateSalesReport(LocalDate startDate, LocalDate endDate);
    SalesReportDTO generateDailySalesReport(LocalDate date);
    SalesReportDTO generateWeeklySalesReport();
    SalesReportDTO generateMonthlySalesReport();
} 