package com.zax.maybank_assessment.web;

import com.zax.maybank_assessment.service.TransactionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.Instant;
import java.util.Map;

@ControllerAdvice
public class RestExceptionHandler {

    @ExceptionHandler(TransactionService.NotFoundException.class)
    public ResponseEntity<?> handleNotFound(TransactionService.NotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error("NOT_FOUND", ex.getMessage()));
    }

    @ExceptionHandler(TransactionService.ConcurrencyException.class)
    public ResponseEntity<?> handleConcurrency(TransactionService.ConcurrencyException ex) {
        return ResponseEntity.status(HttpStatus.PRECONDITION_FAILED)
                .body(error("PRECONDITION_FAILED", ex.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleValidation(MethodArgumentNotValidException ex) {
        var msg = ex.getBindingResult().getFieldErrors().stream()
                .map(e -> e.getField() + " " + e.getDefaultMessage()).toList();
        return ResponseEntity.badRequest().body(error("VALIDATION_FAILED", String.join("; ", msg)));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<?> handleBadRequest(IllegalArgumentException ex) {
        return ResponseEntity.badRequest().body(error("BAD_REQUEST", ex.getMessage()));
    }

    private Map<String, Object> error(String code, String message) {
        return Map.of("error", code, "message", message, "timestamp", Instant.now().toString());
    }
}