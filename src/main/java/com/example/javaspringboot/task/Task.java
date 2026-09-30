package com.example.javaspringboot.task;

import jakarta.persistence.*;

@Entity
@Table(name = "tasks")
public class Task {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 200)
    private String title;

    @Column(nullable = false, length = 2000)
    private String description;

    @Column(nullable = false)
    private boolean completed;

    protected Task() {}

    Task(TaskRequest request) {
        update(request);
    }

    void update(TaskRequest request) {
        title = request.title().trim();
        description = request.description() == null ? "" : request.description();
        completed = Boolean.TRUE.equals(request.completed());
    }

    public Long getId() { return id; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public boolean isCompleted() { return completed; }
}
