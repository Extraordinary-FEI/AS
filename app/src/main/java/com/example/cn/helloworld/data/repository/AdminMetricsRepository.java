package com.example.cn.helloworld.data.repository;

import com.example.cn.helloworld.data.model.AdminMetrics;
import com.example.cn.helloworld.data.model.Order;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class AdminMetricsRepository {

    private final SupportTaskRepository supportTaskRepository;
    private final AdminOrderRepository orderRepository;
    private final Set<String> registrationUserIds = new HashSet<>();
    private final Set<String> activeUserIds = new HashSet<>();

    public AdminMetricsRepository(SupportTaskRepository supportTaskRepository,
                                  AdminOrderRepository orderRepository) {
        this.supportTaskRepository = supportTaskRepository;
        this.orderRepository = orderRepository;
        seed();
    }

    private void seed() {
        // 模拟报名/注册数据
        registrationUserIds.add("user_amy");
        registrationUserIds.add("user_bob");
        registrationUserIds.add("user_chris");
        registrationUserIds.add("user_dora");
        registrationUserIds.add("user_ella");

        // 活跃用户
        activeUserIds.add("user_amy");
        activeUserIds.add("user_bob");
        activeUserIds.add("user_kim");
        activeUserIds.add("user_lee");
        activeUserIds.add("user_nia");
        activeUserIds.add("user_ola");
    }

    public AdminMetrics loadMetrics() {
        int orderCount = orderRepository.count();
        int pendingTasks = supportTaskRepository.countTasksByStatus(SupportTaskRepository.STATUS_PENDING);
        int newRegistrations = registrationUserIds.size();
        int activeUsers = activeUserIds.size();
        return new AdminMetrics(orderCount, pendingTasks, newRegistrations, activeUsers);
    }

    public List<Order> getOrders() {
        return orderRepository.getOrders();
    }

    public void registerUser(String userId) {
        if (userId != null) {
            registrationUserIds.add(userId);
        }
    }

    public void markActiveUser(String userId) {
        if (userId != null) {
            activeUserIds.add(userId);
        }
    }
}
