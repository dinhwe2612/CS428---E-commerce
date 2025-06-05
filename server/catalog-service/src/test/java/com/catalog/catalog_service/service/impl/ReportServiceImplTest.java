package com.catalog.catalog_service.service.impl;

import com.catalog.catalog_service.dto.DailySalesDTO;
import com.catalog.catalog_service.dto.ProductSalesDTO;
import com.catalog.catalog_service.dto.SalesReportDTO;
import com.catalog.catalog_service.integration.order.client.OrderServiceClient;
import com.catalog.catalog_service.integration.order.dto.OrderItemResponseDTO;
import com.catalog.catalog_service.integration.order.dto.OrderResponseDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("ReportService Implementation Tests")
class ReportServiceImplTest {

    @Mock
    private OrderServiceClient orderServiceClient;

    @InjectMocks
    private ReportServiceImpl reportService;

    private List<OrderResponseDTO> mockOrders;
    private LocalDate startDate;
    private LocalDate endDate;

    @BeforeEach
    void setUp() {
        startDate = LocalDate.of(2024, 1, 1);
        endDate = LocalDate.of(2024, 1, 31);
        mockOrders = createMockOrders();
    }

    @Test
    @DisplayName("Should generate sales report with valid date range")
    void generateSalesReport_WithValidDateRange_ReturnsCorrectReport() {
        when(orderServiceClient.getOrdersByStatus("COMPLETED")).thenReturn(mockOrders);

        SalesReportDTO result = reportService.generateSalesReport(startDate, endDate);

        assertThat(result).isNotNull();
        assertThat(result.getTotalRevenue()).isEqualTo(300.0);
        assertThat(result.getTotalOrders()).isEqualTo(2);
        assertThat(result.getTotalProductsSold()).isEqualTo(4);
        assertThat(result.getAverageOrderValue()).isEqualTo(150.0);
        assertThat(result.getPeriod()).isEqualTo("2024-01-01 to 2024-01-31");
        assertThat(result.getReportGeneratedAt()).isNotNull();
    }

    @Test
    @DisplayName("Should filter orders by date range correctly")
    void generateSalesReport_WithFilteredDateRange_FiltersOrdersCorrectly() {
        List<OrderResponseDTO> ordersWithDifferentDates = createOrdersWithDifferentDates();
        when(orderServiceClient.getOrdersByStatus("COMPLETED")).thenReturn(ordersWithDifferentDates);

        LocalDate filterStart = LocalDate.of(2024, 1, 15);
        LocalDate filterEnd = LocalDate.of(2024, 1, 20);

        SalesReportDTO result = reportService.generateSalesReport(filterStart, filterEnd);

        assertThat(result.getTotalOrders()).isEqualTo(1);
        assertThat(result.getTotalRevenue()).isEqualTo(200.0);
    }

    @Test
    @DisplayName("Should handle empty orders list")
    void generateSalesReport_WithEmptyOrders_ReturnsEmptyReport() {
        when(orderServiceClient.getOrdersByStatus("COMPLETED")).thenReturn(Collections.emptyList());

        SalesReportDTO result = reportService.generateSalesReport(startDate, endDate);

        assertThat(result.getTotalRevenue()).isEqualTo(0.0);
        assertThat(result.getTotalOrders()).isEqualTo(0);
        assertThat(result.getTotalProductsSold()).isEqualTo(0);
        assertThat(result.getAverageOrderValue()).isEqualTo(0.0);
        assertThat(result.getTopSellingProducts()).isEmpty();
        assertThat(result.getDailySales()).isEmpty();
    }

    @Test
    @DisplayName("Should generate top selling products correctly")
    void generateSalesReport_WithMultipleProducts_ReturnsTopSellingProducts() {
        when(orderServiceClient.getOrdersByStatus("COMPLETED")).thenReturn(mockOrders);

        SalesReportDTO result = reportService.generateSalesReport(startDate, endDate);

        assertThat(result.getTopSellingProducts()).hasSize(2);
        
        ProductSalesDTO firstProduct = result.getTopSellingProducts().get(0);
        assertThat(firstProduct.getQuantitySold()).isEqualTo(3);
        assertThat(firstProduct.getProductName()).isEqualTo("Product A");
        assertThat(firstProduct.getTotalRevenue()).isEqualTo(150.0);
        
        ProductSalesDTO secondProduct = result.getTopSellingProducts().get(1);
        assertThat(secondProduct.getQuantitySold()).isEqualTo(1);
        assertThat(secondProduct.getProductName()).isEqualTo("Product B");
        assertThat(secondProduct.getTotalRevenue()).isEqualTo(150.0);
    }

    @Test
    @DisplayName("Should generate daily sales correctly")
    void generateSalesReport_WithOrdersOnDifferentDays_GeneratesDailySales() {
        when(orderServiceClient.getOrdersByStatus("COMPLETED")).thenReturn(mockOrders);

        SalesReportDTO result = reportService.generateSalesReport(startDate, endDate);

        assertThat(result.getDailySales()).hasSize(2);
        
        DailySalesDTO firstDay = result.getDailySales().get(0);
        assertThat(firstDay.getDate()).isEqualTo(LocalDate.of(2024, 1, 15));
        assertThat(firstDay.getRevenue()).isEqualTo(100.0);
        assertThat(firstDay.getOrderCount()).isEqualTo(1);
        
        DailySalesDTO secondDay = result.getDailySales().get(1);
        assertThat(secondDay.getDate()).isEqualTo(LocalDate.of(2024, 1, 20));
        assertThat(secondDay.getRevenue()).isEqualTo(200.0);
        assertThat(secondDay.getOrderCount()).isEqualTo(1);
    }

    @Test
    @DisplayName("Should generate daily sales report for specific date")
    void generateDailySalesReport_WithSpecificDate_ReturnsReportForThatDate() {
        when(orderServiceClient.getOrdersByStatus("COMPLETED")).thenReturn(mockOrders);

        LocalDate specificDate = LocalDate.of(2024, 1, 15);
        SalesReportDTO result = reportService.generateDailySalesReport(specificDate);

        assertThat(result.getPeriod()).isEqualTo("2024-01-15 to 2024-01-15");
        assertThat(result.getTotalOrders()).isEqualTo(1);
        assertThat(result.getTotalRevenue()).isEqualTo(100.0);
    }

    @Test
    @DisplayName("Should generate weekly sales report for last 7 days")
    void generateWeeklySalesReport_ReturnsReportForLastSevenDays() {
        LocalDate today = LocalDate.now();
        LocalDate weekStart = today.minusDays(6);
        
        List<OrderResponseDTO> weeklyOrders = createOrdersForDateRange(weekStart, today);
        when(orderServiceClient.getOrdersByStatus("COMPLETED")).thenReturn(weeklyOrders);

        SalesReportDTO result = reportService.generateWeeklySalesReport();

        assertThat(result.getPeriod()).isEqualTo(weekStart + " to " + today);
        assertThat(result).isNotNull();
    }

    @Test
    @DisplayName("Should generate monthly sales report for current month")
    void generateMonthlySalesReport_ReturnsReportForCurrentMonth() {
        LocalDate today = LocalDate.now();
        LocalDate monthStart = today.withDayOfMonth(1);
        
        List<OrderResponseDTO> monthlyOrders = createOrdersForDateRange(monthStart, today);
        when(orderServiceClient.getOrdersByStatus("COMPLETED")).thenReturn(monthlyOrders);

        SalesReportDTO result = reportService.generateMonthlySalesReport();

        assertThat(result.getPeriod()).isEqualTo(monthStart + " to " + today);
        assertThat(result).isNotNull();
    }

    @Test
    @DisplayName("Should calculate unit price correctly for products")
    void generateSalesReport_WithProductSales_CalculatesUnitPriceCorrectly() {
        when(orderServiceClient.getOrdersByStatus("COMPLETED")).thenReturn(mockOrders);

        SalesReportDTO result = reportService.generateSalesReport(startDate, endDate);

        ProductSalesDTO firstProduct = result.getTopSellingProducts().get(0);
        assertThat(firstProduct.getUnitPrice()).isEqualTo(50.0);
        
        ProductSalesDTO secondProduct = result.getTopSellingProducts().get(1);
        assertThat(secondProduct.getUnitPrice()).isEqualTo(150.0);
    }

    @Test
    @DisplayName("Should handle single order with multiple items")
    void generateSalesReport_WithSingleOrderMultipleItems_ProcessesCorrectly() {
        OrderResponseDTO singleOrder = createSingleOrderWithMultipleItems();
        when(orderServiceClient.getOrdersByStatus("COMPLETED")).thenReturn(Arrays.asList(singleOrder));

        SalesReportDTO result = reportService.generateSalesReport(startDate, endDate);

        assertThat(result.getTotalOrders()).isEqualTo(1);
        assertThat(result.getTotalProductsSold()).isEqualTo(5);
        assertThat(result.getTopSellingProducts()).hasSize(2);
        assertThat(result.getDailySales()).hasSize(1);
    }

    private List<OrderResponseDTO> createMockOrders() {
        OrderItemResponseDTO item1 = new OrderItemResponseDTO(1L, "1", "Product A", 2, 50.0, 100.0, "inv1");
        OrderItemResponseDTO item2 = new OrderItemResponseDTO(2L, "1", "Product A", 1, 50.0, 50.0, "inv2");
        OrderItemResponseDTO item3 = new OrderItemResponseDTO(3L, "2", "Product B", 1, 150.0, 150.0, "inv3");

        OrderResponseDTO order1 = new OrderResponseDTO();
        order1.setId(1L);
        order1.setUserId("user1");
        order1.setStatus("COMPLETED");
        order1.setTotalAmount(100.0);
        order1.setOrderItems(Arrays.asList(item1));
        order1.setCreatedAt(LocalDateTime.of(2024, 1, 15, 10, 0));

        OrderResponseDTO order2 = new OrderResponseDTO();
        order2.setId(2L);
        order2.setUserId("user2");
        order2.setStatus("COMPLETED");
        order2.setTotalAmount(200.0);
        order2.setOrderItems(Arrays.asList(item2, item3));
        order2.setCreatedAt(LocalDateTime.of(2024, 1, 20, 14, 30));

        return Arrays.asList(order1, order2);
    }

    private List<OrderResponseDTO> createOrdersWithDifferentDates() {
        OrderResponseDTO order1 = new OrderResponseDTO();
        order1.setId(1L);
        order1.setTotalAmount(100.0);
        order1.setOrderItems(Collections.emptyList());
        order1.setCreatedAt(LocalDateTime.of(2024, 1, 10, 10, 0));

        OrderResponseDTO order2 = new OrderResponseDTO();
        order2.setId(2L);
        order2.setTotalAmount(200.0);
        order2.setOrderItems(Collections.emptyList());
        order2.setCreatedAt(LocalDateTime.of(2024, 1, 18, 14, 30));

        OrderResponseDTO order3 = new OrderResponseDTO();
        order3.setId(3L);
        order3.setTotalAmount(300.0);
        order3.setOrderItems(Collections.emptyList());
        order3.setCreatedAt(LocalDateTime.of(2024, 1, 25, 16, 0));

        return Arrays.asList(order1, order2, order3);
    }

    private List<OrderResponseDTO> createOrdersForDateRange(LocalDate start, LocalDate end) {
        OrderResponseDTO order = new OrderResponseDTO();
        order.setId(1L);
        order.setTotalAmount(100.0);
        order.setOrderItems(Collections.emptyList());
        order.setCreatedAt(start.atTime(12, 0));
        
        return Arrays.asList(order);
    }

    private OrderResponseDTO createSingleOrderWithMultipleItems() {
        OrderItemResponseDTO item1 = new OrderItemResponseDTO(1L, "1", "Product A", 3, 50.0, 150.0, "inv1");
        OrderItemResponseDTO item2 = new OrderItemResponseDTO(2L, "2", "Product B", 2, 75.0, 150.0, "inv2");

        OrderResponseDTO order = new OrderResponseDTO();
        order.setId(1L);
        order.setUserId("user1");
        order.setStatus("COMPLETED");
        order.setTotalAmount(300.0);
        order.setOrderItems(Arrays.asList(item1, item2));
        order.setCreatedAt(LocalDateTime.of(2024, 1, 15, 10, 0));

        return order;
    }
} 