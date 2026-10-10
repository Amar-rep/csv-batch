package com.example.batch.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    OpenAPI batchImportOpenApi() {
        return new OpenAPI().info(new Info()
                .title("Spring Batch CSV")
                .version("v1")
                .description("Upload a csv file "));
        
    }
}
