package com.recky.demo.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;

@TestConfiguration
public class TestWebConfig {

    @Bean
    public DispatcherServlet dispatcherServlet(WebApplicationContext webApplicationContext) {
        return new DispatcherServlet(webApplicationContext);
    }
}