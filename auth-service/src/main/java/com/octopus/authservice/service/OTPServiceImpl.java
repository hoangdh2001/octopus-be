package com.octopus.authservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class OTPServiceImpl implements OTPService {

    private final OTPGenerator otpGenerator;

    @Override
    public Integer generateOTP(String key) {
        Integer cacheOTP = otpGenerator.getOPTByKey(key);
        if (cacheOTP != null) otpGenerator.clearOTPFromCache(key);
        Integer otpValue = otpGenerator.generateOTP(key);
        if (otpValue == -1) {
            log.error("OTP generator is not working...");
            return null;
        }
        log.info("Generator OTP: {}", otpValue);

        return otpValue;
    }

    @Override
    public Boolean validateOTP(String key, Integer otp) {
        Integer cacheOTP = otpGenerator.getOPTByKey(key);
        if (cacheOTP != null && cacheOTP.equals(otp))
        {
            log.info("verify otp " + otp);
            otpGenerator.clearOTPFromCache(key);
            return true;
        }
        log.error("otp error" + otp);
        return false;
    }
}
