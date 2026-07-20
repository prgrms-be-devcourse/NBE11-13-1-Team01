package com.composebean.global.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI composeBeanOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Compose Bean API")
                        .description("Compose Bean 상품 및 주문 API 문서")
                        .version("v1"));
    }
}