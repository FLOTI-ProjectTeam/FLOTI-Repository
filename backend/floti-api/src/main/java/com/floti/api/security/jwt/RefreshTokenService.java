package com.floti.api.security.jwt;


import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

/**
 * RefreshTokenService
 * - Redis에 Refresh 토큰을 저장/조회/삭제하는 서비스
 */
@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    private final StringRedisTemplate redisTemplate;

    // Refresh 토큰 저장 (username 기반 key)
    public void save(String username, String refreshToken, long ttlMinutes) {
        String key = buildKey(username);
        redisTemplate.opsForValue().set(key, refreshToken, ttlMinutes, TimeUnit.MINUTES);
    }

    // Refresh 토큰 조회
    public String get(String username) {
        return redisTemplate.opsForValue().get(buildKey(username));
    }

    // Refresh 토큰 삭제 (로그아웃 시 사용)
    public void delete(String username) {
        redisTemplate.delete(buildKey(username));
    }

    private String buildKey(String username) {
        return "refresh:" + username;
    }
}