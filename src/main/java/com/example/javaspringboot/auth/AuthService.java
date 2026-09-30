package com.example.javaspringboot.auth;

import java.nio.charset.StandardCharsets;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class AuthService {
    private final UserAccountRepository repository;
    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(12);
    private final String dummyHash = encoder.encode("dummy-password-for-timing");

    public AuthService(UserAccountRepository repository) {
        this.repository = repository;
    }

    public void register(AuthRequest request) {
        validatePassword(request.password());
        if (repository.existsByEmail(request.email())) throw duplicateEmail();
        try {
            repository.saveAndFlush(new UserAccount(request.email(), encoder.encode(request.password())));
        } catch (DataIntegrityViolationException exception) {
            // The unique database constraint also handles simultaneous registrations.
            if (repository.existsByEmail(request.email())) throw duplicateEmail();
            throw exception;
        }
    }

    public void login(AuthRequest request) {
        validatePassword(request.password());
        var account = repository.findByEmail(request.email());
        boolean matches = encoder.matches(request.password(),
            account.map(UserAccount::getPasswordHash).orElse(dummyHash));
        if (account.isEmpty() || !matches) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid email or password.");
        }
    }

    private void validatePassword(String password) {
        if (password.getBytes(StandardCharsets.UTF_8).length > 72) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Password must be at most 72 UTF-8 bytes.");
        }
    }

    private ResponseStatusException duplicateEmail() {
        return new ResponseStatusException(HttpStatus.CONFLICT, "Email is already registered.");
    }
}
