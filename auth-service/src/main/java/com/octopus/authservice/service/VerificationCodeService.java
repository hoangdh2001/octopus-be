package com.octopus.authservice.service;

public interface VerificationCodeService {
    String generateVerificationCode(String key);
    Boolean validateVerificationCode(String key, String verificationCode);
}
