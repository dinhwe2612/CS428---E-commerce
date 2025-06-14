package com.server.notification_service.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;

import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI notificationServiceOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Notification Service API")
                        .version("1.0.0")
                        .description("All Notification REST and WebSocket operations merged on one page")
                        .contact(new Contact()
                                .name("Notification Team")
                                .email("notifications@example.com")
                        )
                );
    }

    @Bean
    public GroupedOpenApi notificationGroup() {
        return GroupedOpenApi.builder()
                .group("notification")
                // include both the REST endpoints and the WS mapping in a single group
                .pathsToMatch(
                        "/api/v1/notification/**",
                        "/notification/read"
                )
                .build();
    }
}
