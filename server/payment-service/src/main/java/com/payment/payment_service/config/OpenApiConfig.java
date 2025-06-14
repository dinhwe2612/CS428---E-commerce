package com.payment.payment_service.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.servers.Server;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI paymentServiceOpenAPI() {
        Server gatewayServer = new Server()
                .url("http://localhost:8085") // Gateway address
                .description("Gateway");
        return new OpenAPI()
                .servers(List.of(gatewayServer))
                .info(new Info()
                        .title("Payment Service API")
                        .version("1.0.0")
                        .description("APIs for payment processing, webhooks, and callbacks")
                        .contact(new Contact()
                                .name("Payment Team")
                                .email("payments@example.com")
                        )
                );
    }

    @Bean
    public GroupedOpenApi paymentGroup() {
        return GroupedOpenApi.builder()
                .group("payment")
                .pathsToMatch("/api/v1/payments/**")
                .build();
    }
}
