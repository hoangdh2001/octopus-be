package com.octopus.authservice.service;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.context.annotation.Description;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.Random;
import java.util.concurrent.TimeUnit;

@Description(value = "Service for generating and validating OTP.")
@Service
@RequiredArgsConstructor
public class OTPGenerator {

    private static final Long EXPIRE_MIN = 15L;
    private static final String PREFIX_KEY = "-otp";

    private final RedisTemplate<String, Object> redisTemplate;

    /**
     * Method for generating OTP and put it in cache.
     *
     * @param key - cache key
     * @return cache value (generated OTP number)
     */
    public Integer generateOTP(String key)
    {
        Random random = new Random();
        var OTP = 1000 + random.nextInt(9000);
        redisTemplate.opsForValue().set(key + PREFIX_KEY, OTP, EXPIRE_MIN, TimeUnit.MINUTES);
        return OTP;
    }

    /**
     * Method for getting OTP value by key.
     *
     * @param key - target key
     * @return OTP value
     */
    public Integer getOPTByKey(String key)
    {
        var value = redisTemplate.opsForValue().get(key + PREFIX_KEY);
        if (value == null) {
            return null;
        }
        return Integer.parseInt(value.toString());
    }

    /**
     * Method for removing key from cache.
     *
     * @param key - target key
     */
    public void clearOTPFromCache(String key) {
        redisTemplate.delete(key + PREFIX_KEY);
    }
}
