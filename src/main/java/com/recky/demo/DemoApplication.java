package com.recky.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import io.github.cdimascio.dotenv.Dotenv;




@EnableJpaRepositories(basePackages = "com.recky.demo.dao") // Specifies the package for JPA repositories
@SpringBootApplication(scanBasePackages = "com.recky.demo") // Specifies base package scanning
public class DemoApplication {

	public static void main(String[] args) {
		Dotenv dotenv = Dotenv.load(); // Load .env file
		System.setProperty("server.port", dotenv.get("SERVER_PORT", "8080")); // Set server.port
		SpringApplication.run(DemoApplication.class, args);
		// SpringApplication.run(DemoApplication.class, args);
	}

}
