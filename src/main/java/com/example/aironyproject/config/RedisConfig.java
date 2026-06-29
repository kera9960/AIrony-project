package com.example.aironyproject.config;

import com.example.aironyproject.domain.accommodationLike.dto.PopularAccommodationResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.JacksonJsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import tools.jackson.databind.JavaType;
import tools.jackson.databind.ObjectMapper;

import java.util.List;


@Configuration
public class RedisConfig {

    @Bean
    public RedisTemplate<String, List<PopularAccommodationResponse>> popularAccommodationRedisTemplate(
            RedisConnectionFactory connectionFactory,
            ObjectMapper objectMapper)
    {
        RedisTemplate<String, List<PopularAccommodationResponse>> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);

        JavaType javaType = objectMapper.getTypeFactory()
                .constructCollectionType(List.class, PopularAccommodationResponse.class);

        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new JacksonJsonRedisSerializer<>(objectMapper, javaType));

        template.afterPropertiesSet();
        return template;
    }
}
