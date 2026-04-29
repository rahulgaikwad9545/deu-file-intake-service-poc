package com.deupoc.fileintakeservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Web configuration class for CORS settings and other web-related configurations.
 * This class configures Cross-Origin Resource Sharing (CORS) to allow frontend
 * applications running on different ports/origins to communicate with this API.
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    /**
     * Configures CORS settings for the application.
     * Allows requests from common frontend development servers and supports
     * all necessary HTTP methods and headers for file operations.
     *
     * @return CorsConfigurationSource with CORS settings
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        // Add your frontend URLs here - common development ports
        configuration.addAllowedOrigin("http://localhost:3000");      // React default
        configuration.addAllowedOrigin("http://localhost:4200");      // Angular default
        configuration.addAllowedOrigin("http://localhost:5173");      // Vite default
        configuration.addAllowedOrigin("http://localhost:8080");      // Other frontend
        // For production, replace above with: configuration.addAllowedOrigin("https://yourdomain.com");

        // Allow these HTTP methods for file operations
        configuration.addAllowedMethod("GET");
        configuration.addAllowedMethod("POST");
        configuration.addAllowedMethod("PUT");
        configuration.addAllowedMethod("DELETE");
        configuration.addAllowedMethod("OPTIONS");

        // Allow all headers
        configuration.addAllowedHeader("*");

        // Allow credentials (cookies, authorization headers)
        configuration.setAllowCredentials(true);

        // Max age for preflight cache (1 hour)
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/api/**", configuration);

        return source;
    }
}
