package com.microservice_ecommerce.integration;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.client.RestTemplate;

import com.microservice_ecommerce.integration.catalog.config.CatalogServiceConfig;

@SpringBootTest
@ActiveProfiles("test")
public class CatalogServiceConfigTest {
    
    @Autowired
    private ApplicationContext applicationContext;
    
    @Autowired
    private CatalogServiceConfig catalogServiceConfig;
    
    @Test
    public void testCatalogServiceConfigLoads() {
        assertNotNull(catalogServiceConfig);
        assertNotNull(catalogServiceConfig.getBaseUrl());
    }
    
    @Test
    public void testLoadBalancedRestTemplateBean() {
        RestTemplate restTemplate = applicationContext.getBean("catalogRestTemplate", RestTemplate.class);
        assertNotNull(restTemplate);
        
        assertNotNull(catalogServiceConfig.getBaseUrl());
        assertNotNull(catalogServiceConfig.getTimeout());
    }
} 