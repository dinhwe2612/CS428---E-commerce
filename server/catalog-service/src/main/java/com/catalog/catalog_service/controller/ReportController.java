package com.catalog.catalog_service.controller;

import com.catalog.catalog_service.dto.SalesReportDTO;
import com.catalog.catalog_service.security.UserDetailsWithUserId;
import com.catalog.catalog_service.service.ReportService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/v1/reports")
@RequiredArgsConstructor
@Slf4j
public class ReportController {

    private final ReportService reportService;

    @GetMapping("/sales")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    public ResponseEntity<SalesReportDTO> generateSalesReport(
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate,
            @AuthenticationPrincipal UserDetailsWithUserId userDetails) {
        
        log.info("User {} generating sales report from {} to {}", 
                userDetails.getUserId(), startDate, endDate);
        
        SalesReportDTO report = reportService.generateSalesReport(startDate, endDate);
        return ResponseEntity.ok(report);
    }

    @GetMapping("/sales/daily")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    public ResponseEntity<SalesReportDTO> generateDailySalesReport(
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date,
            @AuthenticationPrincipal UserDetailsWithUserId userDetails) {
        
        LocalDate reportDate = date != null ? date : LocalDate.now();
        log.info("User {} generating daily sales report for {}", 
                userDetails.getUserId(), reportDate);
        
        SalesReportDTO report = reportService.generateDailySalesReport(reportDate);
        return ResponseEntity.ok(report);
    }

    @GetMapping("/sales/weekly")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    public ResponseEntity<SalesReportDTO> generateWeeklySalesReport(
            @AuthenticationPrincipal UserDetailsWithUserId userDetails) {
        
        log.info("User {} generating weekly sales report", userDetails.getUserId());
        
        SalesReportDTO report = reportService.generateWeeklySalesReport();
        return ResponseEntity.ok(report);
    }

    @GetMapping("/sales/monthly")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    public ResponseEntity<SalesReportDTO> generateMonthlySalesReport(
            @AuthenticationPrincipal UserDetailsWithUserId userDetails) {
        
        log.info("User {} generating monthly sales report", userDetails.getUserId());
        
        SalesReportDTO report = reportService.generateMonthlySalesReport();
        return ResponseEntity.ok(report);
    }
} 