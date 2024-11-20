package com.peach.careerfit.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import com.peach.careerfit.meal.model.dto.Food;

@Configuration
public class RedisConfig {

    @Bean(name = "foodRedisTemplate")
    public RedisTemplate<String, Food> foodRedisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, Food> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new GenericJackson2JsonRedisSerializer());
        return template;
    }
}
