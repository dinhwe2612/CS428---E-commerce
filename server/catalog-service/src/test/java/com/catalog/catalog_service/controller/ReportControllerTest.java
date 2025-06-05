package com.catalog.catalog_service.controller;

import com.catalog.catalog_service.dto.DailySalesDTO;
import com.catalog.catalog_service.dto.ProductSalesDTO;
import com.catalog.catalog_service.dto.SalesReportDTO;
import com.catalog.catalog_service.security.UserDetailsWithUserId;
import com.catalog.catalog_service.service.ReportService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
@DisplayName("Report Controller Tests")
class ReportControllerTest {

    @Mock
    private ReportService reportService;

    @InjectMocks
    private ReportController reportController;

    private SalesReportDTO mockSalesReport;
    private UserDetailsWithUserId mockUserDetails;

    @BeforeEach
    void setUp() {
        mockSalesReport = createMockSalesReport();
        mockUserDetails = createMockUserDetails();
    }

    @Test
    @DisplayName("Should generate sales report with valid parameters")
    void generateSalesReport_WithValidParameters_ReturnsReport() {
        LocalDate startDate = LocalDate.of(2024, 1, 1);
        LocalDate endDate = LocalDate.of(2024, 1, 31);
        
        when(reportService.generateSalesReport(startDate, endDate)).thenReturn(mockSalesReport);

        ResponseEntity<SalesReportDTO> response = reportController.generateSalesReport(
                startDate, endDate, mockUserDetails);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(mockSalesReport);
        assertThat(response.getBody().getTotalRevenue()).isEqualTo(1000.0);
        assertThat(response.getBody().getTotalOrders()).isEqualTo(5);
        assertThat(response.getBody().getTotalProductsSold()).isEqualTo(15);
        assertThat(response.getBody().getAverageOrderValue()).isEqualTo(200.0);
        assertThat(response.getBody().getPeriod()).isEqualTo("2024-01-01 to 2024-01-31");
        
        verify(reportService).generateSalesReport(startDate, endDate);
    }

    @Test
    @DisplayName("Should generate daily sales report with specific date")
    void generateDailySalesReport_WithSpecificDate_ReturnsReport() {
        LocalDate reportDate = LocalDate.of(2024, 1, 15);
        
        when(reportService.generateDailySalesReport(reportDate)).thenReturn(mockSalesReport);

        ResponseEntity<SalesReportDTO> response = reportController.generateDailySalesReport(
                reportDate, mockUserDetails);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(mockSalesReport);
        assertThat(response.getBody().getTotalRevenue()).isEqualTo(1000.0);
        assertThat(response.getBody().getTotalOrders()).isEqualTo(5);
        
        verify(reportService).generateDailySalesReport(reportDate);
    }

    @Test
    @DisplayName("Should generate daily sales report for current date when no date provided")
    void generateDailySalesReport_WithoutDate_ReturnsReportForToday() {
        LocalDate today = LocalDate.now();
        when(reportService.generateDailySalesReport(today)).thenReturn(mockSalesReport);

        ResponseEntity<SalesReportDTO> response = reportController.generateDailySalesReport(
                null, mockUserDetails);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(mockSalesReport);
        assertThat(response.getBody().getTotalRevenue()).isEqualTo(1000.0);
        
        verify(reportService).generateDailySalesReport(today);
    }

    @Test
    @DisplayName("Should generate weekly sales report")
    void generateWeeklySalesReport_ReturnsReport() {
        when(reportService.generateWeeklySalesReport()).thenReturn(mockSalesReport);

        ResponseEntity<SalesReportDTO> response = reportController.generateWeeklySalesReport(mockUserDetails);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(mockSalesReport);
        assertThat(response.getBody().getTotalRevenue()).isEqualTo(1000.0);
        assertThat(response.getBody().getTotalOrders()).isEqualTo(5);
        
        verify(reportService).generateWeeklySalesReport();
    }

    @Test
    @DisplayName("Should generate monthly sales report")
    void generateMonthlySalesReport_ReturnsReport() {
        when(reportService.generateMonthlySalesReport()).thenReturn(mockSalesReport);

        ResponseEntity<SalesReportDTO> response = reportController.generateMonthlySalesReport(mockUserDetails);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(mockSalesReport);
        assertThat(response.getBody().getTotalRevenue()).isEqualTo(1000.0);
        assertThat(response.getBody().getTotalOrders()).isEqualTo(5);
        assertThat(response.getBody().getAverageOrderValue()).isEqualTo(200.0);
        
        verify(reportService).generateMonthlySalesReport();
    }

    @Test
    @DisplayName("Should include top selling products in response")
    void generateSalesReport_IncludesTopSellingProducts() {
        LocalDate startDate = LocalDate.of(2024, 1, 1);
        LocalDate endDate = LocalDate.of(2024, 1, 31);
        
        when(reportService.generateSalesReport(startDate, endDate)).thenReturn(mockSalesReport);

        ResponseEntity<SalesReportDTO> response = reportController.generateSalesReport(
                startDate, endDate, mockUserDetails);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().getTopSellingProducts()).isNotEmpty();
        assertThat(response.getBody().getTopSellingProducts()).hasSize(1);
        assertThat(response.getBody().getTopSellingProducts().get(0).getProductName()).isEqualTo("Product A");
        assertThat(response.getBody().getTopSellingProducts().get(0).getQuantitySold()).isEqualTo(10);
    }

    @Test
    @DisplayName("Should include daily sales in response")
    void generateSalesReport_IncludesDailySales() {
        LocalDate startDate = LocalDate.of(2024, 1, 1);
        LocalDate endDate = LocalDate.of(2024, 1, 31);
        
        when(reportService.generateSalesReport(startDate, endDate)).thenReturn(mockSalesReport);

        ResponseEntity<SalesReportDTO> response = reportController.generateSalesReport(
                startDate, endDate, mockUserDetails);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().getDailySales()).isNotEmpty();
        assertThat(response.getBody().getDailySales()).hasSize(1);
        assertThat(response.getBody().getDailySales().get(0).getDate()).isEqualTo(LocalDate.of(2024, 1, 15));
        assertThat(response.getBody().getDailySales().get(0).getRevenue()).isEqualTo(500.0);
    }

    @Test
    @DisplayName("Should handle empty report gracefully")
    void generateSalesReport_WithEmptyData_ReturnsEmptyReport() {
        SalesReportDTO emptyReport = createEmptyReport();
        LocalDate startDate = LocalDate.of(2024, 1, 1);
        LocalDate endDate = LocalDate.of(2024, 1, 31);
        
        when(reportService.generateSalesReport(startDate, endDate)).thenReturn(emptyReport);

        ResponseEntity<SalesReportDTO> response = reportController.generateSalesReport(
                startDate, endDate, mockUserDetails);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().getTotalRevenue()).isEqualTo(0.0);
        assertThat(response.getBody().getTotalOrders()).isEqualTo(0);
        assertThat(response.getBody().getTopSellingProducts()).isEmpty();
        assertThat(response.getBody().getDailySales()).isEmpty();
    }

    @Test
    @DisplayName("Should return report generated timestamp")
    void generateSalesReport_IncludesGeneratedTimestamp() {
        LocalDate startDate = LocalDate.of(2024, 1, 1);
        LocalDate endDate = LocalDate.of(2024, 1, 31);
        
        when(reportService.generateSalesReport(startDate, endDate)).thenReturn(mockSalesReport);

        ResponseEntity<SalesReportDTO> response = reportController.generateSalesReport(
                startDate, endDate, mockUserDetails);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().getReportGeneratedAt()).isNotNull();
    }

    @Test
    @DisplayName("Should delegate to service correctly")
    void generateWeeklySalesReport_DelegatesToService() {
        when(reportService.generateWeeklySalesReport()).thenReturn(mockSalesReport);

        reportController.generateWeeklySalesReport(mockUserDetails);

        verify(reportService).generateWeeklySalesReport();
    }

    @Test
    @DisplayName("Should delegate monthly report to service correctly")
    void generateMonthlySalesReport_DelegatesToService() {
        when(reportService.generateMonthlySalesReport()).thenReturn(mockSalesReport);

        reportController.generateMonthlySalesReport(mockUserDetails);

        verify(reportService).generateMonthlySalesReport();
    }

    private SalesReportDTO createMockSalesReport() {
        SalesReportDTO report = new SalesReportDTO();
        report.setReportGeneratedAt(LocalDateTime.now());
        report.setPeriod("2024-01-01 to 2024-01-31");
        report.setTotalRevenue(1000.0);
        report.setTotalOrders(5);
        report.setTotalProductsSold(15);
        report.setAverageOrderValue(200.0);
        
        ProductSalesDTO topProduct = new ProductSalesDTO(1L, "Product A", 10, 500.0, 50.0);
        report.setTopSellingProducts(Arrays.asList(topProduct));
        
        DailySalesDTO dailySales = new DailySalesDTO(LocalDate.of(2024, 1, 15), 500.0, 2, 8);
        report.setDailySales(Arrays.asList(dailySales));
        
        return report;
    }

    private SalesReportDTO createEmptyReport() {
        SalesReportDTO report = new SalesReportDTO();
        report.setReportGeneratedAt(LocalDateTime.now());
        report.setPeriod("2024-01-01 to 2024-01-31");
        report.setTotalRevenue(0.0);
        report.setTotalOrders(0);
        report.setTotalProductsSold(0);
        report.setAverageOrderValue(0.0);
        report.setTopSellingProducts(Collections.emptyList());
        report.setDailySales(Collections.emptyList());
        
        return report;
    }

    private UserDetailsWithUserId createMockUserDetails() {
        return new UserDetailsWithUserId("testuser", "password", Collections.emptyList(), "user123", "ADMIN");
    }
} 