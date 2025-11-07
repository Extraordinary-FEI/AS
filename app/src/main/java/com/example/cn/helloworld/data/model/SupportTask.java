package com.example.cn.helloworld.data.model;

import java.io.Serializable;

/**
 * Represents a customer support task for administrators.
 */
public class SupportTask implements Serializable {

    private String taskId;
    private String title;
    private String description;
    private String status;
    private String assignedAdmin;
    private long createdAt;
    private long updatedAt;
    private int priority;

    public SupportTask(String taskId, String title, String description) {
        this(taskId, title, description, "OPEN", null, System.currentTimeMillis(),
                System.currentTimeMillis(), 0);
    }

    public SupportTask(String taskId, String title, String description, String status,
                       String assignedAdmin, long createdAt, long updatedAt, int priority) {
        this.taskId = taskId;
        this.title = title;
        this.description = description;
        this.status = status;
        this.assignedAdmin = assignedAdmin;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.priority = priority;
    }

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getAssignedAdmin() {
        return assignedAdmin;
    }

    public void setAssignedAdmin(String assignedAdmin) {
        this.assignedAdmin = assignedAdmin;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }

    public long getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(long updatedAt) {
        this.updatedAt = updatedAt;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }
}
