package com.example.javaspringboot.task;

import java.net.URI;
import java.util.List;

import jakarta.validation.Valid;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/tasks")
public class TaskController {
    private final TaskRepository repository;

    public TaskController(TaskRepository repository) {
        this.repository = repository;
    }

    @PostMapping
    public ResponseEntity<Task> create(@Valid @RequestBody TaskRequest request) {
        Task task = repository.save(new Task(request));
        return ResponseEntity.created(URI.create("/tasks/" + task.getId())).body(task);
    }

    @GetMapping
    public List<Task> list() {
        return repository.findAll(Sort.by("id"));
    }

    @GetMapping("/{id}")
    public Task get(@PathVariable Long id) {
        return findTask(id);
    }

    @PutMapping("/{id}")
    @Transactional
    public Task update(@PathVariable Long id, @Valid @RequestBody TaskRequest request) {
        Task task = findTask(id);
        task.update(request);
        return repository.save(task);
    }

    @DeleteMapping("/{id}")
    @Transactional
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        repository.delete(findTask(id));
        return ResponseEntity.noContent().build();
    }

    private Task findTask(Long id) {
        return repository.findById(id).orElseThrow(() ->
            new ResponseStatusException(HttpStatus.NOT_FOUND, "Task tidak ditemukan"));
    }
}
