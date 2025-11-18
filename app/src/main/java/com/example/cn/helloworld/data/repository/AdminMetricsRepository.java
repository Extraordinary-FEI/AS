package com.example.cn.helloworld.data.repository;

import com.example.cn.helloworld.data.model.AdminMetrics;
import com.example.cn.helloworld.data.model.Order;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class AdminMetricsRepository {

    private final SupportTaskRepository supportTaskRepository;
    private final List<Order> orders = new ArrayList<>();
    private final Set<String> registrationUserIds = new HashSet<>();
    private final Set<String> activeUserIds = new HashSet<>();

    public AdminMetricsRepository(SupportTaskRepository supportTaskRepository) {
        this.supportTaskRepository = supportTaskRepository;
        seed();
    }

    private void seed() {
        // 模拟最近一天内的订单
        long now = System.currentTimeMillis();
        for (int i = 0; i < 18; i++) {
            Order order = new Order("order-" + (1000 + i));
            double total = 128 + (i % 5) * 50;
            order.setTotalAmount(total);
            order.setCreatedAt(now - (i * 60L * 60L * 1000L));
            switch (i % 4) {
                case 0:
                    order.setStatus("PAID");
                    break;
                case 1:
                    order.setStatus("FULFILLED");
                    break;
                case 2:
                    order.setStatus("SHIPPED");
                    break;
                default:
                    order.setStatus("CREATED");
                    break;
            }
            orders.add(order);
        }

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
        int orderCount = orders.size();
        int pendingTasks = supportTaskRepository.countTasksByStatus(SupportTaskRepository.STATUS_PENDING);
        int newRegistrations = registrationUserIds.size();
        int activeUsers = activeUserIds.size();
        return new AdminMetrics(orderCount, pendingTasks, newRegistrations, activeUsers);
    }

    public void addOrder(Order order) {
        if (order != null) {
            orders.add(order);
        }
    }

    public List<Order> getOrders() {
        return new ArrayList<>(orders);
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
