package com.order.order_service.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI orderServiceOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Order Service API")
                        .version("1.0.0")
                        .description("Endpoints for creating, querying, and managing orders")
                        .contact(new Contact()
                                .name("Order Service Team")
                                .email("orders@example.com")
                        )
                );
    }

    @Bean
    public GroupedOpenApi ordersGroup() {
        return GroupedOpenApi.builder()
                .group("orders")
                .pathsToMatch("/api/v1/orders/**")
                .build();
    }
}
