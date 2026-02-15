package com.wigerlabs.TideTempo.entity;

import jakarta.persistence.*;

@Entity
public class Task extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(nullable = false, length = 255, unique = true)
    private String name;

    @Column(nullable = true, length = 500)
    private String description;

    @Column(name = "time_spent", nullable = false)
    private int timeSpent = 0;

    @JoinColumn(name = "user_id", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    @JoinColumn(name = "priority_id", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private Priority priority;

    @JoinColumn(name = "status_id", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private Status status;

    public Task() {
    }

    public Task(int id, String name, String description, int timeSpent, User user, Priority priority, Status status) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.timeSpent = timeSpent;
        this.user = user;
        this.priority = priority;
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

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Priority getPriority() {
        return priority;
    }

    public void setPriority(Priority priority) {
        this.priority = priority;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }
}
