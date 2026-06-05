package com.test.xbraintest.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("X-Brain Order Management API")
                        .description("REST API for order management with asynchronous delivery processing via RabbitMQ")
                        .version("1.0.0"));
    }
}
