package com.example.cn.helloworld.data.repository;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

/**
 * 按用户存储应援任务的报名状态，便于在详情页和列表中展示“已报名 / 已通过 / 已驳回”。
 */
public class SupportTaskEnrollmentRepository {

    public enum EnrollmentStatus {
        NOT_APPLIED,
        PENDING,
        APPROVED,
        REJECTED,
        CANCELLED
    }

    private static final String PREF_NAME = "support_task_enrollments";
    private static final String KEY_PREFIX = "enrollment_";

    private final SharedPreferences preferences;

    public SupportTaskEnrollmentRepository(Context context) {
        this.preferences = context.getApplicationContext()
                .getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public EnrollmentStatus getEnrollmentStatus(String userId, String taskId) {
        if (TextUtils.isEmpty(taskId)) {
            return EnrollmentStatus.NOT_APPLIED;
        }
        String key = buildKey(userId, taskId);
        String stored = preferences.getString(key, EnrollmentStatus.NOT_APPLIED.name());
        try {
            return EnrollmentStatus.valueOf(stored);
        } catch (Exception ignore) {
            return EnrollmentStatus.NOT_APPLIED;
        }
    }

    public void markApplied(String userId, String taskId) {
        updateStatus(userId, taskId, EnrollmentStatus.PENDING);
    }

    public void markApproved(String userId, String taskId) {
        updateStatus(userId, taskId, EnrollmentStatus.APPROVED);
    }

    public void markRejected(String userId, String taskId) {
        updateStatus(userId, taskId, EnrollmentStatus.REJECTED);
    }

    public void markCancelled(String userId, String taskId) {
        updateStatus(userId, taskId, EnrollmentStatus.CANCELLED);
    }

    public void reset(String userId, String taskId) {
        updateStatus(userId, taskId, EnrollmentStatus.NOT_APPLIED);
    }

    public void updateStatus(String userId, String taskId, EnrollmentStatus status) {
        if (TextUtils.isEmpty(taskId) || status == null) {
            return;
        }
        String key = buildKey(userId, taskId);
        preferences.edit().putString(key, status.name()).apply();
    }

    private String buildKey(String userId, String taskId) {
        String safeUser = TextUtils.isEmpty(userId) ? "guest" : userId;
        return KEY_PREFIX + safeUser + "_" + taskId;
    }
}

