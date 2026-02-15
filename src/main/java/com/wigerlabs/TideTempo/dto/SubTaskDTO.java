package com.wigerlabs.TideTempo.dto;

import java.io.Serializable;

public class SubTaskDTO implements Serializable {
    private int id;
    private String name;
    private String description;
    private int taskId;
    private int priorityId;
    private int statusId;

    public SubTaskDTO() {
    }

    public SubTaskDTO(int id, String name, String description, int taskId, int priorityId, int statusId) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.taskId = taskId;
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


    public int getTaskId() {
        return taskId;
    }

    public void setTaskId(int taskId) {
        this.taskId = taskId;
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
