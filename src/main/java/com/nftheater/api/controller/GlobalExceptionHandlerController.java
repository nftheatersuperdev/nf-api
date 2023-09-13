package com.nftheater.api.controller;

import com.nftheater.api.constant.ErrorCode;
import com.nftheater.api.constant.ResponseStatus;
import com.nftheater.api.controller.response.GeneralResponse;
import com.nftheater.api.exception.BadCredentialsException;
import com.nftheater.api.exception.DataNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Optional;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandlerController {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<GeneralResponse<String>> handleGlobalException(Exception e) {
        final GeneralResponse<String> generalResponse = new GeneralResponse<>(ResponseStatus.FAILED, null, e.getMessage());
        log.error(e.getMessage(), e);
        return ResponseEntity.internalServerError().body(generalResponse);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<GeneralResponse<String>> handleAccessDeniedException(AccessDeniedException e) {
        String code = ResponseStatus.FAILED;
        final GeneralResponse<String> generalResponse = new GeneralResponse<>(code, null, e.getMessage());
        return new ResponseEntity<>(generalResponse, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<GeneralResponse<String>> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        FieldError error = Optional.ofNullable(e.getFieldError()).orElseGet(() -> new FieldError(e.getObjectName(), "Unknown", "Invalid Request"));
        String errorMessage = String.format("[%s] parameter error. Description: [%s]", error.getField(), error.getDefaultMessage());

        final GeneralResponse<String> generalResponse = new GeneralResponse<>(ErrorCode.INVALID_REQUEST, null, errorMessage);

        log.warn("Method argument not valid exception: {}", e.getMessage());
        return ResponseEntity.badRequest().body(generalResponse);
    }

    @ExceptionHandler({DataNotFoundException.class})
    public ResponseEntity<GeneralResponse<String>> handleDataNotFoundException(DataNotFoundException e) {
        String code = ResponseStatus.FAILED;

        if (e.getCode() != null) {
            code = e.getCode();
        }

        final GeneralResponse<String> generalResponse = new GeneralResponse<>(code, null, e.getMessage());
        return new ResponseEntity<>(generalResponse, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(BindException.class)
    public ResponseEntity<GeneralResponse<String>> handleBindException(BindException e) {
        FieldError error = Optional.ofNullable(e.getFieldError()).orElseGet(() -> new FieldError(e.getObjectName(), "Unknown", "Invalid Request"));
        String errorMessage = String.format("[%s] invalid parameter.", error.getField());

        final GeneralResponse<String> generalResponse = new GeneralResponse<>(ErrorCode.INVALID_REQUEST, null, errorMessage);
        return ResponseEntity.badRequest().body(generalResponse);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<GeneralResponse<String>> handleBadCredentialsException(BadCredentialsException e) {
        String code = ResponseStatus.FAILED;

        if (e.getCode() != null) {
            code = e.getCode();
        }

        final GeneralResponse<String> generalResponse = new GeneralResponse<>(code, null, e.getMessage());
        return new ResponseEntity<>(generalResponse, HttpStatus.UNAUTHORIZED);
    }

}
