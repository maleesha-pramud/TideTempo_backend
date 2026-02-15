package com.wigerlabs.TideTempo.entity;

import jakarta.persistence.*;

@Entity
public class SubTask extends BaseEntity {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private int id;

    @Column(nullable = false, length = 255, unique = true)
    private String name;


    @JoinColumn(name = "task_id", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private Task task;

    @JoinColumn(name = "status_id", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private Status status;

    public SubTask() {
    }

    public SubTask(int id, String name, Task task, Status status) {
        this.id = id;
        this.name = name;
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
