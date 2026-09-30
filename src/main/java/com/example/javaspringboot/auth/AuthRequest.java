package com.example.javaspringboot.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.util.Locale;

public record AuthRequest(
    @NotBlank(message = "Email is required.")
    @Email(message = "Email must be valid.")
    @Size(max = 254, message = "Email must be at most 254 characters.") String email,
    @NotBlank(message = "Password is required.")
    @Size(min = 8, max = 72, message = "Password must contain 8 to 72 characters.") String password
) {
    public AuthRequest {
        if (email != null) email = email.trim().toLowerCase(Locale.ROOT);
    }
}
