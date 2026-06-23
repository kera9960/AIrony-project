package com.example.aironyproject;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class AIronyProjectApplication {

	public static void main(String[] args) {
		SpringApplication.run(AIronyProjectApplication.class, args);
	}

}
