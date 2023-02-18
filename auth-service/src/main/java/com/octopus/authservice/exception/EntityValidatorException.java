package com.octopus.authservice.exception;

import java.util.Map;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EntityValidatorException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    private Map<String, String> errors;

    public EntityValidatorException() {
        super();
    }

    public EntityValidatorException(Map<String, String> errors) {
        super();
        this.errors = errors;
    }
}
