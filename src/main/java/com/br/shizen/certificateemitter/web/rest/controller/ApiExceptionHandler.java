package com.br.shizen.certificateemitter.web.rest.controller;

import com.br.shizen.certificateemitter.web.rest.dto.ErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.io.IOException;
import java.io.UncheckedIOException;

@RestControllerAdvice
@Slf4j
public class ApiExceptionHandler {

    @ExceptionHandler({IOException.class, UncheckedIOException.class, IllegalStateException.class})
    public ResponseEntity<ErrorResponse> handleInternalServerError(Exception e) {
        log.error("Request failed", e);
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ErrorResponse.error(resolveMessage(e)));
    }

    private String resolveMessage(Exception e) {
        Throwable cause = e instanceof UncheckedIOException uncheckedIOException
                ? uncheckedIOException.getCause()
                : e;
        return cause.getMessage() == null ? "Internal server error" : cause.getMessage();
    }
}
