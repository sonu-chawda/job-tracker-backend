package com.jobtracker.exception;

import java.time.LocalDateTime;
import java.util.*;
import org.springframework.http.*;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private ResponseEntity<Map<String, Object>> body(HttpStatus s, Object message) {
        return ResponseEntity.status(s).body(Map.of("timestamp", LocalDateTime.now().toString(),
                "status", s.value(), "message", message));
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<?> notFound(ResourceNotFoundException e) { return body(HttpStatus.NOT_FOUND, e.getMessage()); }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> invalid(MethodArgumentNotValidException e) {
        Map<String, String> errors = new LinkedHashMap<>();
        e.getBindingResult().getFieldErrors().forEach(f -> errors.put(f.getField(), f.getDefaultMessage()));
        return body(HttpStatus.BAD_REQUEST, errors);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<?> badRequest(IllegalArgumentException e) { return body(HttpStatus.BAD_REQUEST, e.getMessage()); }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<?> denied(AccessDeniedException e) { return body(HttpStatus.FORBIDDEN, "Access denied"); }

    @ExceptionHandler(AiException.class)
    public ResponseEntity<?> ai(AiException e) { return body(HttpStatus.valueOf(e.getStatus()), e.getMessage()); }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> other(Exception e) {
        e.printStackTrace();
        return body(HttpStatus.INTERNAL_SERVER_ERROR, "Something went wrong");
    }
}
