package com.octopus.authservice.exception.global;

import com.octopus.exceptionutils.CommonExceptionHandler;
import com.octopus.exceptionutils.error.Violation;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingPathVariableException;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import com.octopus.exceptionutils.error.Error;

import java.util.*;

@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler implements CommonExceptionHandler {

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        Set<Violation> validationErrors = new HashSet<>();
        ex.getBindingResult().getFieldErrors()
                .forEach(fieldError -> validationErrors.stream()
                        .filter(validationError -> validationError.getField().equals(fieldError.getField()))
                        .findAny()
                        .ifPresentOrElse(validationError -> {
                                    validationError.getMessage().add(fieldError.getDefaultMessage());
                                },
                                () -> {
                                    validationErrors.add(new Violation(fieldError.getField(),
                                            new ArrayList<>(Collections.singletonList(fieldError.getDefaultMessage())))
                                    );
                                }
                        )
                );

        return ResponseEntity
                .badRequest()
                .body(Error.builder()
                        .status(HttpStatus.BAD_REQUEST.value())
                        .title(HttpStatus.BAD_REQUEST.getReasonPhrase())
                        .violations(validationErrors)
                        .detail(ex.getMessage())
                        .build()
                );
    }

    @Override
    protected ResponseEntity<Object> handleExceptionInternal(Exception ex, Object body, HttpHeaders headers, HttpStatus status, WebRequest request) {
        return ResponseEntity
                .internalServerError()
                .body(Error.builder()
                        .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                        .title(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase())
                        .detail(ex.getMessage())
                        .build()
                );
    }

    @Override
    protected ResponseEntity<Object> handleMissingPathVariable(MissingPathVariableException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(Error.builder()
                        .status(HttpStatus.NOT_FOUND.value())
                        .title(HttpStatus.NOT_FOUND.getReasonPhrase())
                        .detail(ex.getMessage())
                        .build()
                );
    }
}
