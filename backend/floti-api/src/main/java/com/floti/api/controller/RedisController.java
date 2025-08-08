package com.floti.api.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RedisController {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @GetMapping("/redis-test")
    public String redisTest() {
        // Redis에 값 저장
        redisTemplate.opsForValue().set("mykey", "Hello Redis");

        // Redis에서 값 가져오기
        Object value = redisTemplate.opsForValue().get("mykey");
        return "Redis에서 가져온 값: " + value;
    }
}
