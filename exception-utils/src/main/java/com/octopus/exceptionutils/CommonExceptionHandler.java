package com.octopus.exceptionutils;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import com.octopus.exceptionutils.error.Error;

import javax.servlet.http.HttpServletRequest;

public interface CommonExceptionHandler {

    @ExceptionHandler(value = {NotFoundException.class})
    @ResponseStatus(HttpStatus.NOT_FOUND)
    default ResponseEntity<Object> handleNotFound(final NotFoundException ex, HttpServletRequest request) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(Error.builder()
                        .status(HttpStatus.NOT_FOUND.value())
                        .detail(ex.getMessage())
                        .path(request.getRequestURI())
                        .title(HttpStatus.NOT_FOUND.getReasonPhrase())
                        .build()
                );
    }

    @ExceptionHandler(value = {AlreadyExistsException.class})
    @ResponseStatus(HttpStatus.CONFLICT)
    default ResponseEntity<Object> handleAlreadyExists(final AlreadyExistsException ex, HttpServletRequest request) {
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(Error.builder()
                        .status(HttpStatus.CONFLICT.value())
                        .detail(ex.getMessage())
                        .path(request.getRequestURI())
                        .title(HttpStatus.CONFLICT.getReasonPhrase())
                        .build()
                );
    }

    @ExceptionHandler(value = {InvalidDataException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    default ResponseEntity<Object> handleInvalidData(final InvalidDataException ex, HttpServletRequest request) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(Error.builder()
                        .status(HttpStatus.BAD_REQUEST.value())
                        .detail(ex.getMessage())
                        .path(request.getRequestURI())
                        .title(HttpStatus.BAD_REQUEST.getReasonPhrase())
                        .build()
                );
    }

    @ExceptionHandler(value = {AuthenticationException.class})
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    default ResponseEntity<Object> handleAuthenticationException(final AuthenticationException ex, HttpServletRequest request) {
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(Error.builder()
                        .status(HttpStatus.UNAUTHORIZED.value())
                        .detail(ex.getMessage())
                        .path(request.getRequestURI())
                        .title(HttpStatus.UNAUTHORIZED.getReasonPhrase())
                        .build()
                );
    }

    @ExceptionHandler(value = {IllegalArgumentException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    default ResponseEntity<Object> handleIllegalArgumentException(final IllegalArgumentException ex, HttpServletRequest request) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(Error.builder()
                        .status(HttpStatus.BAD_REQUEST.value())
                        .detail(ex.getMessage())
                        .path(request.getRequestURI())
                        .title(HttpStatus.BAD_REQUEST.getReasonPhrase())
                        .build()
                );
    }

}
