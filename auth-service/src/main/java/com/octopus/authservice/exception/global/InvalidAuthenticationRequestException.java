package com.octopus.authservice.exception.global;

import org.springframework.security.core.AuthenticationException;

public class InvalidAuthenticationRequestException extends AuthenticationException {
    public InvalidAuthenticationRequestException(String msg) {
        super(msg);
    }
}
