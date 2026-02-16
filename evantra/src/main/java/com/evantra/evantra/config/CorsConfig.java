package com.evantra.evantra.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.Arrays;

@Configuration
public class CorsConfig {

    private static final String DEFAULT_ALLOWED_ORIGINS =
            "http://localhost:5173,http://localhost:3000,https://eventra-wheat-omega.vercel.app";

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        String configuredOrigins = System.getenv().getOrDefault("CORS_ALLOWED_ORIGINS", DEFAULT_ALLOWED_ORIGINS);
        String[] allowedOrigins = Arrays.stream(configuredOrigins.split(","))
                .map(String::trim)
                .filter(origin -> !origin.isEmpty())
                .toArray(String[]::new);

        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**")
                        .allowedOriginPatterns(allowedOrigins)
                        .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                        .allowedHeaders("*")
                        .allowCredentials(true);
            }
        };
    }
}
