package com.code81.onlinestore.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI onlineStoreOpenApi() {
        return new OpenAPI()
                .info(new Info()
                        .title("Online Store API")
                        .description("Take-home assessment - Catalog, customers, staff, and order management")
                        .version("v0.1.0")
                        .contact(new Contact().name("Maysara")));
    }
}
