package com.octopus.authservice.service;

public interface OTPService {
    Integer generateOTP(String key);
    Boolean validateOTP(String key, Integer otp);
}
