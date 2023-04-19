package com.octopus.authservice.service;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.context.annotation.Description;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;
@Description(value = "Service for generating and validating verification code.")
@Service
@RequiredArgsConstructor
public class VerificationCodeGenerator {
    private static final Long EXPIRE_MIN = 15L;
    private static final String PREFIX_KEY = "-code";
    private final RedisTemplate<String, Object> redisTemplate;

    public String generateVerificationCode(String key)
    {
        String OTP = RandomStringUtils.randomAlphanumeric(124);
        redisTemplate.opsForValue().set(key + PREFIX_KEY, OTP, EXPIRE_MIN, TimeUnit.MINUTES);

        return OTP;
    }

    public String getVerificationCodeByKey(String key)
    {
        return (String) redisTemplate.opsForValue().get(key + PREFIX_KEY);
    }

    public void clearVerificationCodeFromCache(String key) {
        redisTemplate.delete(key + PREFIX_KEY);
    }
}
