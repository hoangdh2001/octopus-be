package com.octopus.authservice.service;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.context.annotation.Description;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;
@Description(value = "Service for generating and validating verification code.")
@Service
public class VerificationCodeGenerator {
    private static final Integer EXPIRE_MIN = 5;
    private LoadingCache<String, String> verificationCodeCache;

    /**
     * Constructor configuration.
     */
    public VerificationCodeGenerator()
    {
        super();
        verificationCodeCache = CacheBuilder.newBuilder()
                .expireAfterWrite(EXPIRE_MIN, TimeUnit.MINUTES)
                .build(new CacheLoader<String, String>() {
                    @Override
                    public String load(String s) throws Exception {
                        return "0";
                    }
                });
    }

    public String generateVerificationCode(String key)
    {
        String OTP = RandomStringUtils.randomAlphanumeric(124);
        verificationCodeCache.put(key, OTP);

        return OTP;
    }

    public String getVerificationCodeByKey(String key)
    {
        return verificationCodeCache.getIfPresent(key);
    }

    public void clearVerificationCodeFromCache(String key) {
        verificationCodeCache.invalidate(key);
    }
}
