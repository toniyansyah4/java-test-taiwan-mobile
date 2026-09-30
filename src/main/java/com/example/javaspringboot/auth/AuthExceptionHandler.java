package com.example.javaspringboot.auth;

import java.util.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

@RestControllerAdvice(assignableTypes = AuthController.class)
public class AuthExceptionHandler {
    @ExceptionHandler(value = MethodArgumentNotValidException.class, produces = "text/plain")
    public ResponseEntity<String> invalidInput(MethodArgumentNotValidException exception) {
        String message = exception.getBindingResult().getFieldErrors().stream()
            .map(error -> error.getDefaultMessage()).distinct().sorted()
            .reduce((first, next) -> first + " " + next).orElse("Invalid input.");
        return ResponseEntity.badRequest().body(message);
    }

    @ExceptionHandler(value = ResponseStatusException.class, produces = "text/plain")
    public ResponseEntity<String> authError(ResponseStatusException exception) {
        return ResponseEntity.status(exception.getStatusCode()).body(exception.getReason());
    }

    @ExceptionHandler(value = MethodArgumentNotValidException.class, produces = "application/json")
    public ResponseEntity<Map<String, String>> invalidJsonInput(MethodArgumentNotValidException exception) {
        return ResponseEntity.badRequest().body(Map.of("message", invalidInput(exception).getBody()));
    }

    @ExceptionHandler(value = ResponseStatusException.class, produces = "application/json")
    public ResponseEntity<Map<String, String>> jsonAuthError(ResponseStatusException exception) {
        return ResponseEntity.status(exception.getStatusCode()).body(Map.of("message", exception.getReason()));
    }
}
