package com.example.cn.helloworld.data.repository;

import com.example.cn.helloworld.data.model.SupportTask;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class SupportTaskRepository {

    public static final String STATUS_PENDING = "PENDING";
    public static final String STATUS_APPROVED = "APPROVED";
    public static final String STATUS_REJECTED = "REJECTED";

    private static final Map<String, SupportTask> tasks = new LinkedHashMap<>();
    private static boolean initialized = false;

    public SupportTaskRepository() {
        if (!initialized) {
            seed();
            initialized = true;
        }
    }

    private void seed() {
        addTask(new SupportTask("task-weibo", "微博控评", "集合队伍，守护主话题热度。",
                STATUS_PENDING, null, System.currentTimeMillis(), System.currentTimeMillis(), 1));
        addTask(new SupportTask("task-qqmusic", "QQ 音乐打榜", "集中打卡提高日播放量。",
                STATUS_PENDING, null, System.currentTimeMillis(), System.currentTimeMillis(), 2));
        addTask(new SupportTask("task-offline", "线下广告位", "招募同城伙伴一起筹备生日灯箱。",
                STATUS_PENDING, null, System.currentTimeMillis(), System.currentTimeMillis(), 3));
    }

    private void addTask(SupportTask task) {
        tasks.put(task.getTaskId(), task);
    }

    public List<SupportTask> getAll() {
        return new ArrayList<>(tasks.values());
    }

    public List<SupportTask> getPendingTasks() {
        List<SupportTask> result = new ArrayList<>();
        for (SupportTask task : tasks.values()) {
            if (STATUS_PENDING.equals(task.getStatus())) {
                result.add(task);
            }
        }
        return result;
    }

    public void approveTask(String taskId, String admin) {
        updateStatus(taskId, STATUS_APPROVED, admin);
    }

    public void rejectTask(String taskId, String admin) {
        updateStatus(taskId, STATUS_REJECTED, admin);
    }

    public void updateStatus(String taskId, String status, String admin) {
        SupportTask task = tasks.get(taskId);
        if (task == null) {
            return;
        }
        task.setStatus(status);
        task.setAssignedAdmin(admin);
        task.setUpdatedAt(System.currentTimeMillis());
    }

    public int countTasksByStatus(String status) {
        int count = 0;
        for (SupportTask task : tasks.values()) {
            if (status.equals(task.getStatus())) {
                count++;
            }
        }
        return count;
    }
}
