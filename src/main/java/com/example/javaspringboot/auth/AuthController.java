package com.example.javaspringboot.auth;

import jakarta.validation.Valid;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@RestController
public class AuthController {
    private final AuthService service;

    public AuthController(AuthService service) { this.service = service; }

    @PostMapping(value = "/register", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE,
        produces = MediaType.TEXT_PLAIN_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public String register(@Valid @ModelAttribute AuthRequest request) {
        service.register(request);
        return "User registered successfully.";
    }

    @PostMapping(value = "/login", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE,
        produces = MediaType.TEXT_PLAIN_VALUE)
    public String login(@Valid @ModelAttribute AuthRequest request) {
        service.login(request);
        return "Login successful";
    }

    @PostMapping(value = "/register", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public Map<String, String> registerJson(@Valid @ModelAttribute AuthRequest request) {
        service.register(request);
        return Map.of("message", "User registered successfully.");
    }

    @PostMapping(value = "/login", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE,
        produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, String> loginJson(@Valid @ModelAttribute AuthRequest request) {
        service.login(request);
        return Map.of("message", "Login successful");
    }
}
