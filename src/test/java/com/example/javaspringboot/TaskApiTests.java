package com.example.javaspringboot;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import tools.jackson.databind.ObjectMapper;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class TaskApiTests {
    @LocalServerPort
    int port;
    private final HttpClient client = HttpClient.newHttpClient();
    private final ObjectMapper json = new ObjectMapper();

    @Test
    void crudAndMissingTask() throws Exception {
        var created = send("POST", "/tasks", "{\"title\":\"Belajar Java\"}");
        assertThat(created.statusCode()).isEqualTo(201);
        var task = json.readTree(created.body());
        String path = "/tasks/" + task.get("id").asLong();
        assertThat(created.headers().firstValue("Location")).contains(path);
        assertThat(task.get("completed").asBoolean()).isFalse();
        assertThat(task.get("description").asText()).isEmpty();
        assertThat(send("GET", path, null).statusCode()).isEqualTo(200);
        var listed = send("GET", "/tasks", null);
        assertThat(listed.statusCode()).isEqualTo(200);
        assertThat(listed.body()).contains("Belajar Java");

        var updated = send("PUT", path,
            "{\"title\":\"Selesai\",\"description\":\"CRUD\",\"completed\":true}");
        assertThat(updated.statusCode()).isEqualTo(200);
        var saved = json.readTree(send("GET", path, null).body());
        assertThat(saved.get("title").asText()).isEqualTo("Selesai");
        assertThat(saved.get("description").asText()).isEqualTo("CRUD");
        assertThat(saved.get("completed").asBoolean()).isTrue();
        assertThat(send("DELETE", path, null).statusCode()).isEqualTo(204);
        assertThat(send("GET", path, null).statusCode()).isEqualTo(404);
        assertThat(send("PUT", path, "{\"title\":\"Missing\"}").statusCode()).isEqualTo(404);
        assertThat(send("DELETE", path, null).statusCode()).isEqualTo(404);
    }

    @Test
    void rejectsInvalidInput() throws Exception {
        assertThat(send("POST", "/tasks", "{\"title\":\" \"}").statusCode()).isEqualTo(400);
        assertThat(send("POST", "/tasks", "{}").statusCode()).isEqualTo(400);
        assertThat(send("POST", "/tasks", "{\"title\":\"" + "x".repeat(201) + "\"}").statusCode()).isEqualTo(400);
        assertThat(send("POST", "/tasks", "invalid json").statusCode()).isEqualTo(400);
    }

    private HttpResponse<String> send(String method, String path, String body) throws Exception {
        return client.send(HttpRequest.newBuilder(URI.create("http://localhost:" + port + path))
            .header("Content-Type", "application/json")
            .method(method, body == null ? HttpRequest.BodyPublishers.noBody()
                : HttpRequest.BodyPublishers.ofString(body))
            .build(), HttpResponse.BodyHandlers.ofString());
    }
}
