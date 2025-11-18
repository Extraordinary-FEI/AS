package com.example.cn.helloworld.data.repository;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import com.example.cn.helloworld.data.model.SupportTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class SupportTaskRepository {

    public static final String STATUS_PENDING = "PENDING";
    public static final String STATUS_APPROVED = "APPROVED";
    public static final String STATUS_REJECTED = "REJECTED";

    private static final String PREFS_NAME = "support_task_repo";
    private static final String KEY_TASKS = "support_tasks";

    private static SupportTaskRepository INSTANCE;

    private final SharedPreferences preferences;
    private final Map<String, SupportTask> tasks = new LinkedHashMap<String, SupportTask>();

    private SupportTaskRepository(Context context) {
        preferences = context.getApplicationContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        loadFromStorage();
        if (tasks.isEmpty()) {
            seed();
            persist();
        }
    }

    public static synchronized SupportTaskRepository getInstance(Context context) {
        if (INSTANCE == null) {
            INSTANCE = new SupportTaskRepository(context);
        }
        return INSTANCE;
    }

    private void seed() {
        tasks.clear();
        addTaskInternal(new SupportTask("task-weibo", "微博控评", "集合队伍，守护主话题热度。",
                STATUS_PENDING, null, System.currentTimeMillis(), System.currentTimeMillis(), 1));
        addTaskInternal(new SupportTask("task-qqmusic", "QQ 音乐打榜", "集中打卡提高日播放量。",
                STATUS_PENDING, null, System.currentTimeMillis(), System.currentTimeMillis(), 2));
        addTaskInternal(new SupportTask("task-offline", "线下广告位", "招募同城伙伴一起筹备生日灯箱。",
                STATUS_PENDING, null, System.currentTimeMillis(), System.currentTimeMillis(), 3));
    }

    private void addTaskInternal(SupportTask task) {
        tasks.put(task.getTaskId(), task);
    }

    public List<SupportTask> getAll() {
        return new ArrayList<SupportTask>(tasks.values());
    }

    public List<SupportTask> getPendingTasks() {
        List<SupportTask> result = new ArrayList<SupportTask>();
        for (SupportTask task : tasks.values()) {
            if (STATUS_PENDING.equals(task.getStatus())) {
                result.add(task);
            }
        }
        return result;
    }

    public SupportTask getTaskById(String taskId) {
        return tasks.get(taskId);
    }

    public SupportTask createTask(String title, String description, int priority, String status, String admin) {
        String resolvedStatus = TextUtils.isEmpty(status) ? STATUS_PENDING : status;
        String id = "task-" + UUID.randomUUID().toString().replace("-", "").substring(0, 8);
        SupportTask task = new SupportTask(id,
                title,
                description,
                resolvedStatus,
                shouldAssignAdmin(resolvedStatus) ? admin : null,
                System.currentTimeMillis(),
                System.currentTimeMillis(),
                priority);
        tasks.put(task.getTaskId(), task);
        persist();
        return task;
    }

    public void updateTask(String taskId, String title, String description, String status, int priority, String admin) {
        SupportTask task = tasks.get(taskId);
        if (task == null) {
            return;
        }
        task.setTitle(title);
        task.setDescription(description);
        task.setPriority(priority);
        String resolvedStatus = TextUtils.isEmpty(status) ? STATUS_PENDING : status;
        task.setStatus(resolvedStatus);
        if (shouldAssignAdmin(resolvedStatus)) {
            task.setAssignedAdmin(admin);
        } else if (STATUS_PENDING.equals(resolvedStatus)) {
            task.setAssignedAdmin(null);
        }
        task.setUpdatedAt(System.currentTimeMillis());
        persist();
    }

    public void deleteTask(String taskId) {
        if (tasks.remove(taskId) != null) {
            persist();
        }
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
        if (shouldAssignAdmin(status)) {
            task.setAssignedAdmin(admin);
        }
        task.setUpdatedAt(System.currentTimeMillis());
        persist();
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

    private boolean shouldAssignAdmin(String status) {
        return !TextUtils.isEmpty(status) && !STATUS_PENDING.equals(status);
    }

    private void loadFromStorage() {
        tasks.clear();
        String json = preferences.getString(KEY_TASKS, null);
        if (TextUtils.isEmpty(json)) {
            return;
        }
        try {
            JSONArray array = new JSONArray(json);
            for (int i = 0; i < array.length(); i++) {
                SupportTask task = fromJson(array.getJSONObject(i));
                if (task != null) {
                    tasks.put(task.getTaskId(), task);
                }
            }
        } catch (JSONException ignored) {
            tasks.clear();
        }
    }

    private void persist() {
        JSONArray array = new JSONArray();
        for (SupportTask task : tasks.values()) {
            try {
                array.put(toJson(task));
            } catch (JSONException ignored) {
            }
        }
        preferences.edit().putString(KEY_TASKS, array.toString()).apply();
    }

    private JSONObject toJson(SupportTask task) throws JSONException {
        JSONObject object = new JSONObject();
        object.put("taskId", task.getTaskId());
        object.put("title", task.getTitle());
        object.put("description", task.getDescription());
        object.put("status", task.getStatus());
        if (!TextUtils.isEmpty(task.getAssignedAdmin())) {
            object.put("assignedAdmin", task.getAssignedAdmin());
        }
        object.put("createdAt", task.getCreatedAt());
        object.put("updatedAt", task.getUpdatedAt());
        object.put("priority", task.getPriority());
        return object;
    }

    private SupportTask fromJson(JSONObject object) {
        if (object == null) {
            return null;
        }
        return new SupportTask(
                object.optString("taskId"),
                object.optString("title"),
                object.optString("description"),
                object.optString("status", STATUS_PENDING),
                object.optString("assignedAdmin", null),
                object.optLong("createdAt", System.currentTimeMillis()),
                object.optLong("updatedAt", System.currentTimeMillis()),
                object.optInt("priority", 0)
        );
    }
}
