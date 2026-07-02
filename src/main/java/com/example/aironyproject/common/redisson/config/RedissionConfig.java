package com.example.aironyproject.common.redisson.config;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;

@RequiredArgsConstructor
@Configuration
public class RedissionConfig {

	@Value("${spring.data.redis.host}")
	private String host;

	@Value("${spring.data.redis.port}")
	private int port;

	@Bean
	public RedissonClient redissonClient(){
		Config config = new Config();
		config.useSingleServer()
			.setAddress("redis://" + host + ":" + port)
			.setConnectionMinimumIdleSize(5)
			.setConnectionPoolSize(10)
			.setIdleConnectionTimeout(100000)
			.setConnectTimeout(5000)
			.setRetryAttempts(5)
			.setRetryInterval(1500);
		return Redisson.create(config);
	}
}
