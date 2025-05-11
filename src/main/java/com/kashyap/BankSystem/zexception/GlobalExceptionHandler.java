package com.kashyap.BankSystem.zexception;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

    // Handles Security & Authorization Exceptions
    @ExceptionHandler(SecurityException.class)
    public ResponseEntity<Map<String, Object>> handleSecurityException(SecurityException ex) {
        return buildErrorResponse(ex.getMessage(), HttpStatus.FORBIDDEN);
    }

    // Handles Entity Not Found (e.g., Account, Customer, Transaction Not Found)
    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleEntityNotFoundException(EntityNotFoundException ex) {
        return buildErrorResponse(ex.getMessage(), HttpStatus.NOT_FOUND);
    }

    // Handles Invalid Arguments in Request (e.g., Missing Fields, Validation Errors)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationException(MethodArgumentNotValidException ex) {
        return buildErrorResponse("Invalid request parameters", HttpStatus.BAD_REQUEST);
    }

    // Handles Access Denied (for role-based access control)
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<Map<String, Object>> handleAccessDeniedException(AccessDeniedException ex) {
        return buildErrorResponse("Access Denied: " + ex.getMessage(), HttpStatus.FORBIDDEN);
    }

    // Handles Generic Exceptions (fallback for all uncaught exceptions)
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGlobalException(Exception ex, WebRequest request) {
        return buildErrorResponse("An unexpected error occurred: " + ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    // Utility method to create a structured error response
    private ResponseEntity<Map<String, Object>> buildErrorResponse(String message, HttpStatus status) {
        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("status", status.value());
        errorResponse.put("error", status.getReasonPhrase());
        errorResponse.put("message", message);
        return new ResponseEntity<>(errorResponse, status);
    }
}
