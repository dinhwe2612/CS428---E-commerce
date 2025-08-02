package com.order.order_service.config;

import java.util.List;

import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI orderServiceOpenAPI() {
        Server gatewayServer = new Server()
                .url("http://localhost:8085") // Gateway address
                .description("Gateway");
        return new OpenAPI()
                .servers(List.of(gatewayServer))
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
    public GroupedOpenApi orderGroup() {
        return GroupedOpenApi.builder()
                .group("order")
                .pathsToMatch("/api/v1/orders/**", "/api/v1/guest-orders/**")
           
                .build();
    }


    @Bean
    public GroupedOpenApi guestOrderGroup() {
        return GroupedOpenApi.builder()
                .group("guest-order")
                .pathsToMatch("/api/v1/guest-orders", "/api/v1/guest-orders/**")
                .build();
    }
}
