package com.example.cn.helloworld.ui.user;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.HashSet;
import java.util.Set;

/**
 * 管理线下打卡进度，使用 SharedPreferences 记录完成状态。
 */
public class CheckinManager {

    private static final String PREF_NAME = "checkin_progress";
    private static final String KEY_COMPLETED = "completed_locations";

    private final SharedPreferences preferences;

    public CheckinManager(Context context) {
        preferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public boolean isCompleted(String locationId) {
        return getCompletedIds().contains(locationId);
    }

    public void toggleCompleted(String locationId) {
        Set<String> completed = new HashSet<String>(getCompletedIds());
        if (completed.contains(locationId)) {
            completed.remove(locationId);
        } else {
            completed.add(locationId);
        }
        preferences.edit().putStringSet(KEY_COMPLETED, completed).apply();
    }

    public int getCompletedCount() {
        return getCompletedIds().size();
    }

    private Set<String> getCompletedIds() {
        Set<String> stored = preferences.getStringSet(KEY_COMPLETED, new HashSet<String>());
        return new HashSet<String>(stored);
    }
}

