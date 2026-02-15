package com.wigerlabs.TideTempo.dto;

import java.io.Serializable;

public class SubTaskDTO implements Serializable {
    private int id;
    private String name;
    private int taskId;
    private int statusId;

    public SubTaskDTO() {
    }

    public SubTaskDTO(int id, String name, int taskId, int statusId) {
        this.id = id;
        this.name = name;
        this.taskId = taskId;
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


    public int getTaskId() {
        return taskId;
    }

    public void setTaskId(int taskId) {
        this.taskId = taskId;
    }


    public int getStatusId() {
        return statusId;
    }

    public void setStatusId(int statusId) {
        this.statusId = statusId;
    }
}
