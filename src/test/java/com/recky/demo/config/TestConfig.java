package com.recky.demo.config;

import org.mockito.Mockito;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.boot.web.servlet.error.ErrorController;
import jakarta.persistence.EntityManagerFactory;

@TestConfiguration
public class TestConfig implements WebMvcConfigurer {
    
    @Bean
    public EntityManagerFactory entityManagerFactory() {
        return Mockito.mock(EntityManagerFactory.class);
    }
    
    @Bean
    public ErrorController errorController() {
        return new ErrorController() {};
    }
}