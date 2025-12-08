package com.example.cn.helloworld.data.repository;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import com.example.cn.helloworld.data.model.SupportTask;
import com.example.cn.helloworld.data.storage.AdminLocalStore;
import com.example.cn.helloworld.data.repository.support.LocalSupportTaskDataSource;
import com.example.cn.helloworld.ui.main.HomeModels;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class SupportTaskRepository {

    public static final String STATUS_PENDING = "PENDING";
    public static final String STATUS_APPROVED = "APPROVED";
    public static final String STATUS_REJECTED = "REJECTED";

    private static final String KEY_SUPPORT_TASKS = "admin_support_tasks";
    private static final Map<String, SupportTask> tasks = new LinkedHashMap<>();
    private static boolean initialized = false;
    private static SharedPreferences preferences;

    public SupportTaskRepository(Context context) {
        if (context != null) {
            AdminLocalStore.init(context);
            preferences = AdminLocalStore.get(context);
        } else if (preferences == null && AdminLocalStore.isInitialized()) {
            preferences = AdminLocalStore.get();
        }
        // 如果之前因为缺少 Context 只做了内存 seed，一旦拿到 SharedPreferences 就立刻加载持久化数据
        if (preferences != null) {
            loadFromStorage();
            initialized = true;
        } else if (!initialized) {
            loadFromStorage();
            initialized = true;
        }
    }

    public SupportTaskRepository() {
        this(null);
    }

    private void seed() {
        tasks.clear();
        seedFromUserFacingTasks();
        // 如果用户端没有内置任务（极端情况），确保至少有一个占位任务
        if (tasks.isEmpty()) {
            addTaskInternal(new SupportTask("task-weibo", "微博控评", "集合队伍，守护主话题热度。",
                    STATUS_PENDING, null, System.currentTimeMillis(), System.currentTimeMillis(), 1));
        }
        persist();
    }

    /**
     * 复用用户端的占位任务，确保管理员端与普通用户看到的默认数据一致。
     */
    private void seedFromUserFacingTasks() {
        List<HomeModels.SupportTask> defaultTasks = new LocalSupportTaskDataSource().getSupportTasks();
        for (HomeModels.SupportTask task : defaultTasks) {
            if (task == null) {
                continue;
            }
            addTaskInternal(new SupportTask(
                    task.getId(),
                    task.getName(),
                    task.getDescription(),
                    mapStatusFromHome(task.getStatus()),
                    null,
                    System.currentTimeMillis(),
                    System.currentTimeMillis(),
                    task.getEnrolledCount()
            ));
        }
    }

    private String mapStatusFromHome(HomeModels.SupportTask.TaskStatus status) {
        if (status == HomeModels.SupportTask.TaskStatus.ONGOING) {
            return STATUS_APPROVED;
        }
        if (status == HomeModels.SupportTask.TaskStatus.COMPLETED) {
            return STATUS_REJECTED;
        }
        return STATUS_PENDING;
    }

    private void loadFromStorage() {
        if (preferences == null) {
            seed();
            return;
        }
        String json = preferences.getString(KEY_SUPPORT_TASKS, null);
        if (TextUtils.isEmpty(json)) {
            seed();
            return;
        }
        try {
            JSONArray array = new JSONArray(json);
            tasks.clear();
            for (int i = 0; i < array.length(); i++) {
                SupportTask task = fromJson(array.getJSONObject(i));
                tasks.put(task.getTaskId(), task);
            }
        } catch (JSONException e) {
            seed();
        }
    }

    private void persist() {
        if (preferences == null) {
            return;
        }
        JSONArray array = new JSONArray();
        for (SupportTask task : tasks.values()) {
            array.put(toJson(task));
        }
        preferences.edit().putString(KEY_SUPPORT_TASKS, array.toString()).commit();
    }

    private void addTaskInternal(SupportTask task) {
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
        // 审批通过后不允许再取消报名或更改为其他状态
        if (STATUS_APPROVED.equals(task.getStatus()) && !STATUS_APPROVED.equals(status)) {
            return;
        }
        task.setStatus(status);
        task.setAssignedAdmin(admin);
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

    public void createTask(SupportTask task) {
        if (task == null) {
            return;
        }
        tasks.put(task.getTaskId(), task);
        persist();
    }

    public void updateTask(SupportTask task) {
        if (task == null) {
            return;
        }
        SupportTask existing = tasks.get(task.getTaskId());
        if (existing != null && STATUS_APPROVED.equals(existing.getStatus())
                && !STATUS_APPROVED.equals(task.getStatus())) {
            // 保持审批通过的状态不被回退，避免撤销报名
            task.setStatus(existing.getStatus());
            task.setAssignedAdmin(existing.getAssignedAdmin());
        }
        tasks.put(task.getTaskId(), task);
        persist();
    }

    public void deleteTask(String taskId) {
        if (taskId == null) {
            return;
        }
        SupportTask existing = tasks.get(taskId);
        if (existing != null && STATUS_APPROVED.equals(existing.getStatus())) {
            // 审批通过的报名不允许被删除
            return;
        }
        if (tasks.remove(taskId) != null) {
            persist();
        }
    }

    public SupportTask getTaskById(String taskId) {
        return tasks.get(taskId);
    }

    public String generateTaskId() {
        return "task-" + System.currentTimeMillis();
    }

    private JSONObject toJson(SupportTask task) {
        JSONObject object = new JSONObject();
        try {
            object.put("taskId", task.getTaskId());
            object.put("title", task.getTitle());
            object.put("description", task.getDescription());
            object.put("status", task.getStatus());
            object.put("assignedAdmin", task.getAssignedAdmin());
            object.put("createdAt", task.getCreatedAt());
            object.put("updatedAt", task.getUpdatedAt());
            object.put("priority", task.getPriority());
        } catch (JSONException ignored) {
        }
        return object;
    }

    private SupportTask fromJson(JSONObject object) {
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
