package com.wigerlabs.TideTempo.entity;

import jakarta.persistence.*;

@Entity
public class SubTask extends BaseEntity {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private int id;

    @Column(nullable = false, length = 255, unique = true)
    private String name;

    @Column(nullable = true, length = 500)
    private String description;

    @Column(name = "time_spent", nullable = false)
    private int timeSpent = 0;

    @JoinColumn(name = "priority_id", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private Priority priority;

    @JoinColumn(name = "task_id", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private Task task;

    @JoinColumn(name = "status_id", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private Status status;

    public SubTask() {
    }

    public SubTask(int id, String name, String description, int timeSpent, Priority priority, Task task, Status status) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.timeSpent = timeSpent;
        this.priority = priority;
        this.task = task;
        this.status = status;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getTimeSpent() {
        return timeSpent;
    }

    public void setTimeSpent(int timeSpent) {
        this.timeSpent = timeSpent;
    }

    public Priority getPriority() {
        return priority;
    }

    public void setPriority(Priority priority) {
        this.priority = priority;
    }

    public Task getTask() {
        return task;
    }

    public void setTask(Task task) {
        this.task = task;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }
}
