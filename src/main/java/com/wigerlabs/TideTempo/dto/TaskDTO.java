package com.wigerlabs.TideTempo.dto;

import java.io.Serializable;

public class TaskDTO implements Serializable {
    private int id;
    private String name;
    private String description;
    private int userId;
    private int priorityId;
    private int statusId;

    public TaskDTO() {
    }

    public TaskDTO(int id, String name, String description, int userId, int priorityId, int statusId) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.userId = userId;
        this.priorityId = priorityId;
        this.statusId = statusId;
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


    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getPriorityId() {
        return priorityId;
    }

    public void setPriorityId(int priorityId) {
        this.priorityId = priorityId;
    }

    public int getStatusId() {
        return statusId;
    }

    public void setStatusId(int statusId) {
        this.statusId = statusId;
    }
}
