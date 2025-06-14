package com.catalog.catalog_service.controller;

import com.catalog.catalog_service.dto.ProductSalesDTO;
import com.catalog.catalog_service.dto.SalesReportDTO;
import com.catalog.catalog_service.security.UserDetailsWithUserId;
import com.catalog.catalog_service.service.ReportService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/v1/reports")
@Tag(name = "Reports", description = "Endpoints for generating sales and product reports")
@RequiredArgsConstructor
@Slf4j
public class ReportController {

    private final ReportService reportService;

    @Operation(
            summary = "Generate sales report",
            description = "Generates a sales report for the given date range",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Sales report generated",
                            content = @io.swagger.v3.oas.annotations.media.Content(
                                    schema = @Schema(implementation = SalesReportDTO.class)))
            }
    )
    @GetMapping("/sales")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    public ResponseEntity<SalesReportDTO> generateSalesReport(
            @Parameter(description = "Start date (YYYY-MM-DD)", required = true, example = "2025-06-01")
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @Parameter(description = "End date (YYYY-MM-DD)", required = true, example = "2025-06-14")
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate,
            @Parameter(description = "Authenticated user details", hidden = true)
            @AuthenticationPrincipal UserDetailsWithUserId userDetails
    ) {
        log.info("User {} generating sales report from {} to {}",
                userDetails.getUserId(), startDate, endDate);
        SalesReportDTO report = reportService.generateSalesReport(startDate, endDate);
        return ResponseEntity.ok(report);
    }

    @Operation(
            summary = "Generate daily sales report",
            description = "Generates a sales report for a single day (defaults to today if not provided)",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Daily sales report generated",
                            content = @io.swagger.v3.oas.annotations.media.Content(
                                    schema = @Schema(implementation = SalesReportDTO.class)))
            }
    )
    @GetMapping("/sales/daily")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    public ResponseEntity<SalesReportDTO> generateDailySalesReport(
            @Parameter(description = "Date for report (YYYY-MM-DD)", required = false, example = "2025-06-14")
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date,
            @Parameter(description = "Authenticated user details", hidden = true)
            @AuthenticationPrincipal UserDetailsWithUserId userDetails
    ) {
        LocalDate reportDate = date != null ? date : LocalDate.now();
        log.info("User {} generating daily sales report for {}",
                userDetails.getUserId(), reportDate);
        SalesReportDTO report = reportService.generateDailySalesReport(reportDate);
        return ResponseEntity.ok(report);
    }

    @Operation(
            summary = "Generate weekly sales report",
            description = "Generates a sales report for the past 7 days",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Weekly sales report generated",
                            content = @io.swagger.v3.oas.annotations.media.Content(
                                    schema = @Schema(implementation = SalesReportDTO.class)))
            }
    )
    @GetMapping("/sales/weekly")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    public ResponseEntity<SalesReportDTO> generateWeeklySalesReport(
            @Parameter(description = "Authenticated user details", hidden = true)
            @AuthenticationPrincipal UserDetailsWithUserId userDetails
    ) {
        log.info("User {} generating weekly sales report", userDetails.getUserId());
        SalesReportDTO report = reportService.generateWeeklySalesReport();
        return ResponseEntity.ok(report);
    }

    @Operation(
            summary = "Generate monthly sales report",
            description = "Generates a sales report for the past month",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Monthly sales report generated",
                            content = @io.swagger.v3.oas.annotations.media.Content(
                                    schema = @Schema(implementation = SalesReportDTO.class)))
            }
    )
    @GetMapping("/sales/monthly")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    public ResponseEntity<SalesReportDTO> generateMonthlySalesReport(
            @Parameter(description = "Authenticated user details", hidden = true)
            @AuthenticationPrincipal UserDetailsWithUserId userDetails
    ) {
        log.info("User {} generating monthly sales report", userDetails.getUserId());
        SalesReportDTO report = reportService.generateMonthlySalesReport();
        return ResponseEntity.ok(report);
    }

    @Operation(
            summary = "Get best-selling products",
            description = "Retrieves top-selling products for the given date range",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Best-selling products retrieved",
                            content = @io.swagger.v3.oas.annotations.media.Content(
                                    array = @io.swagger.v3.oas.annotations.media.ArraySchema(
                                            schema = @Schema(implementation = ProductSalesDTO.class))))
            }
    )
    @GetMapping("/best-selling-products")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    public ResponseEntity<List<ProductSalesDTO>> getBestSellingProducts(
            @Parameter(description = "Start date (YYYY-MM-DD)", required = true, example = "2025-06-01")
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @Parameter(description = "End date (YYYY-MM-DD)", required = true, example = "2025-06-14")
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate,
            @Parameter(description = "Maximum number of products to return", example = "10")
            @RequestParam(defaultValue = "10") int limit,
            @Parameter(description = "Authenticated user details", hidden = true)
            @AuthenticationPrincipal UserDetailsWithUserId userDetails
    ) {
        log.info("User {} generating best-selling products report from {} to {} with limit {}",
                userDetails.getUserId(), startDate, endDate, limit);
        List<ProductSalesDTO> bestSelling = reportService.getBestSellingProducts(startDate, endDate, limit);
        return ResponseEntity.ok(bestSelling);
    }
}
