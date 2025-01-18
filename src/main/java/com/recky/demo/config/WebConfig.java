package com.recky.demo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import io.github.cdimascio.dotenv.Dotenv;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                Dotenv dotenv = Dotenv.load(); // Load the .env file
                String allowedOrigin = dotenv.get("ALLOWED_ORIGIN", "http://localhost:3000"); // Default origin
                
                registry.addMapping("/**") // Apply CORS to all endpoints
                        .allowedOrigins(allowedOrigin) // Use origin from .env
                        .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS") // Allowed HTTP methods
                        .allowedHeaders("*") // Allow all headers
                        .allowCredentials(true) // Allow credentials like cookies
                        .maxAge(3600); // Cache preflight response for 1 hour
            }
        };
    }
}
