package com.btcorrie.reviewsystem.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<Map<String, Object>> handleAccessDenied(AccessDeniedException e) {
        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("error", "Access Denied");
        errorResponse.put("message", "You don't have permission to access this resource");
        errorResponse.put("status", HttpStatus.FORBIDDEN.value());
        errorResponse.put("timestamp", LocalDateTime.now());

        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(errorResponse);
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String, Object>> handleRuntimeException(RuntimeException e) {
        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("error", "Bad Request");
        errorResponse.put("message", e.getMessage());
        errorResponse.put("status", HttpStatus.BAD_REQUEST.value());
        errorResponse.put("timestamp", LocalDateTime.now());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationExceptions(MethodArgumentNotValidException e) {
        Map<String, Object> errorResponse = new HashMap<>();
        Map<String, String> validationErrors = new HashMap<>();

        e.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            validationErrors.put(fieldName, errorMessage);
        });

        errorResponse.put("error", "Validation Failed");
        errorResponse.put("message", "Request validation failed");
        errorResponse.put("validationErrors", validationErrors);
        errorResponse.put("status", HttpStatus.BAD_REQUEST.value());
        errorResponse.put("timestamp", LocalDateTime.now());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, Object>> handleIllegalArgument(IllegalArgumentException e) {
        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("error", "Invalid Argument");
        errorResponse.put("message", e.getMessage());
        errorResponse.put("status", HttpStatus.BAD_REQUEST.value());
        errorResponse.put("timestamp", LocalDateTime.now());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }
}
