package com.deupoc.fileintakeservice.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration class for OpenAPI/Swagger documentation.
 * Provides API documentation for the File Intake Service.
 */
@Configuration
public class OpenApiConfig {

    /**
     * Configure OpenAPI documentation
     * @return OpenAPI configuration
     */
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("File Intake Service API")
                        .version("1.0.0")
                        .description("A comprehensive file intake service that supports blob and CLOB storage, " +
                                "text-to-JSON parsing, and file management operations. " +
                                "Features include file upload/download, parsing various text formats to JSON, " +
                                "and export functionality in multiple formats.")
                        .contact(new Contact()
                                .name("DEU POC Team")
                                .email("support@deupoc.com"))
                        .license(new License()
                                .name("Apache 2.0")
                                .url("http://www.apache.org/licenses/LICENSE-2.0")));
    }
}