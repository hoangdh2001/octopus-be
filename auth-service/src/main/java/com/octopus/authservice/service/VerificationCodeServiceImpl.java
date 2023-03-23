package com.octopus.authservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class VerificationCodeServiceImpl implements VerificationCodeService {

    private final VerificationCodeGenerator verificationCodeGenerator;
    @Override
    public String generateVerificationCode(String key) {
        String cacheVerificationCode = verificationCodeGenerator.getVerificationCodeByKey(key);
        if (cacheVerificationCode != null) {
            verificationCodeGenerator.clearVerificationCodeFromCache(key);
        }
        String verificationCodeValue = verificationCodeGenerator.generateVerificationCode(key);
        if (verificationCodeValue.isEmpty()) {
            log.error("verificationCode generator is not working...");
            return null;
        }
        log.info("Generator VerificationCode: {}", verificationCodeValue);

        return verificationCodeValue;
    }

    @Override
    public Boolean validateVerificationCode(String key, String verificationCode) {
        String cacheVerificationCode = verificationCodeGenerator.getVerificationCodeByKey(key);
        if (cacheVerificationCode != null && cacheVerificationCode.equals(verificationCode))
        {
            log.info("verify verificationCode " + verificationCode);
            verificationCodeGenerator.clearVerificationCodeFromCache(key);
            return true;
        }
        log.error("error verificationCode " + verificationCode);
        return false;
    }
}
