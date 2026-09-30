package com.example.javaspringboot;

import com.example.javaspringboot.auth.UserAccountRepository;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class AuthApiTests {
    @LocalServerPort int port;
    @Autowired UserAccountRepository repository;
    private final HttpClient client = HttpClient.newHttpClient();

    @Test
    void registersHashesAndAuthenticates() throws Exception {
        String email = UUID.randomUUID() + "@example.com";
        var registered = send("/register", " " + email.toUpperCase() + " ", "mypassword");
        assertThat(registered.statusCode()).isEqualTo(201);
        assertThat(registered.body()).isEqualTo("User registered successfully.");
        String hash = repository.findByEmail(email).orElseThrow().getPasswordHash();
        assertThat(hash).isNotEqualTo("mypassword");
        assertThat(new BCryptPasswordEncoder().matches("mypassword", hash)).isTrue();
        var login = send("/login", email, "mypassword");
        assertThat(login.statusCode()).isEqualTo(200);
        assertThat(login.body()).isEqualTo("Login successful");
        assertThat(send("/register", email.toUpperCase(), "anotherpassword").statusCode()).isEqualTo(409);
        var wrong = send("/login", email, "wrongpassword");
        var missing = send("/login", "missing-" + email, "wrongpassword");
        assertThat(wrong.statusCode()).isEqualTo(401);
        assertThat(missing.statusCode()).isEqualTo(401);
        assertThat(wrong.body()).isEqualTo(missing.body()).isEqualTo("Invalid email or password.");
    }

    @Test
    void validatesFormInputsAndBcryptByteLimit() throws Exception {
        for (String path : new String[]{"/register", "/login"}) {
            assertThat(send(path, "invalid", "mypassword").statusCode()).isEqualTo(400);
            assertThat(send(path, "", "").statusCode()).isEqualTo(400);
            assertThat(send(path, "test@example.com", "short").statusCode()).isEqualTo(400);
            assertThat(send(path, "test@example.com", "x".repeat(73)).statusCode()).isEqualTo(400);
            assertThat(send(path, "test@example.com", "é".repeat(37)).statusCode()).isEqualTo(400);
        }
    }

    private HttpResponse<String> send(String path, String email, String password) throws Exception {
        return send(path, email, password, "text/plain");
    }

    @Test
    void supportsJsonAcceptHeaderForSuccessAndErrors() throws Exception {
        String email = UUID.randomUUID() + "@example.com";
        var registered = send("/register", email, "mypassword", "application/json");
        assertThat(registered.statusCode()).isEqualTo(201);
        assertThat(registered.headers().firstValue("Content-Type")).hasValueSatisfying(value ->
            assertThat(value).startsWith("application/json"));
        var json = new tools.jackson.databind.ObjectMapper();
        assertThat(json.readTree(registered.body()).get("message").asText()).isEqualTo("User registered successfully.");
        assertThat(send("/login", email, "mypassword", "application/json").statusCode()).isEqualTo(200);
        var duplicate = send("/register", email, "mypassword", "application/json");
        assertThat(duplicate.statusCode()).isEqualTo(409);
        assertThat(json.readTree(duplicate.body()).get("message").asText()).isEqualTo("Email is already registered.");
        assertThat(send("/register", "invalid", "mypassword", "application/json").statusCode()).isEqualTo(400);
        var wrong = send("/login", email, "wrongpassword", "application/json");
        assertThat(wrong.statusCode()).isEqualTo(401);
        assertThat(json.readTree(wrong.body()).get("message").asText()).isEqualTo("Invalid email or password.");
        assertThat(send("/register", UUID.randomUUID() + "@example.com", "mypassword", "*/*").statusCode()).isEqualTo(201);
    }

    private HttpResponse<String> send(String path, String email, String password, String accept) throws Exception {
        String body = "email=" + URLEncoder.encode(email, StandardCharsets.UTF_8)
            + "&password=" + URLEncoder.encode(password, StandardCharsets.UTF_8);
        return client.send(HttpRequest.newBuilder(URI.create("http://localhost:" + port + path))
            .header("Content-Type", "application/x-www-form-urlencoded")
            .header("Accept", accept)
            .POST(HttpRequest.BodyPublishers.ofString(body)).build(), HttpResponse.BodyHandlers.ofString());
    }
}
