package com.floti.api.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.StringRedisTemplate;

/**
 * RedisConfig
 * - Redis 연결 설정 (환경별 host/port를 yml에서 읽어옴)
 */
@Profile({"dev", "prod"})   // 오직 dev, prod에서만 로딩
@Configuration
public class RedisConfig {

    // yml에서 읽어올 값 (spring.redis.host, spring.redis.port)
    @Value("${spring.data.redis.host}")
    private String redisHost;

    @Value("${spring.data.redis.port}")
    private int redisPort;

    // Redis 연결 팩토리 (Lettuce 사용)
    @Bean
    public RedisConnectionFactory redisConnectionFactory() {
        return new LettuceConnectionFactory(redisHost, redisPort);
    }

    // 문자열 전용 Redis 템플릿 (key=value 저장/조회)
    @Bean
    public StringRedisTemplate stringRedisTemplate(RedisConnectionFactory factory) {
        return new StringRedisTemplate(factory);
    }
}