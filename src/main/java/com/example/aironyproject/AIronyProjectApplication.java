package com.example.aironyproject;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import org.springframework.boot.security.autoconfigure.SecurityAutoConfiguration;
import org.springframework.boot.security.autoconfigure.UserDetailsServiceAutoConfiguration;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableCaching
@EnableJpaAuditing
@SpringBootApplication(exclude = {
	SecurityAutoConfiguration.class,
	UserDetailsServiceAutoConfiguration.class
})
public class AIronyProjectApplication {

	public static void main(String[] args) {
		SpringApplication.run(AIronyProjectApplication.class, args);
	}

}
