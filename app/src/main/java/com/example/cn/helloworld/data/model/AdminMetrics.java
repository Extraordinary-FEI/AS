package com.example.cn.helloworld.data.model;

public class AdminMetrics {

    private final int orderCount;
    private final int pendingTasks;
    private final int newRegistrations;
    private final int activeUsers;

    public AdminMetrics(int orderCount, int pendingTasks, int newRegistrations, int activeUsers) {
        this.orderCount = orderCount;
        this.pendingTasks = pendingTasks;
        this.newRegistrations = newRegistrations;
        this.activeUsers = activeUsers;
    }

    public int getOrderCount() {
        return orderCount;
    }

    public int getPendingTasks() {
        return pendingTasks;
    }

    public int getNewRegistrations() {
        return newRegistrations;
    }

    public int getActiveUsers() {
        return activeUsers;
    }
}
