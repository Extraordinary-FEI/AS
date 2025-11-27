package com.example.cn.helloworld.data.session;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.cn.helloworld.data.model.UserRole;

import java.util.HashSet;
import java.util.Set;

public class SessionManager {

    private static final String PREF_NAME = "user_session";

    private static final String KEY_USER_ID = "user_id";
    private static final String KEY_USERNAME = "username";
    private static final String KEY_ROLE = "user_role";
    private static final String KEY_IS_ADMIN = "is_admin";

    private static final String KEY_TOKEN = "auth_token";
    private static final String KEY_PERMISSIONS = "permissions";

    private static final String KEY_REMEMBER = "remember_login";
    private static final String KEY_LAST_USERNAME = "last_username";
    private static final String KEY_LAST_ROLE = "last_role";

    private SharedPreferences prefs;
    private SharedPreferences.Editor editor;

    public SessionManager(Context context) {
        prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = prefs.edit();
    }

    // -------------------- 登录 --------------------

    /** 普通用户登录（必须保存 userId + username） */
    public void login(String userId, String username) {
        editor.putString(KEY_USER_ID, userId);
        editor.putString(KEY_USERNAME, username);
        editor.putString(KEY_ROLE, "USER");
        editor.putBoolean(KEY_IS_ADMIN, false);
        editor.apply();
    }

    /** 管理员登录（必须保存 userId + username） */
    public void loginAdmin(String userId, String username) {
        editor.putString(KEY_USER_ID, userId);
        editor.putString(KEY_USERNAME, username);
        editor.putString(KEY_ROLE, "ADMIN");
        editor.putBoolean(KEY_IS_ADMIN, true);
        editor.apply();
    }

    /** 保存 token + 角色 + 记住我 */
    public void saveSession(String token, UserRole role, boolean remember) {
        editor.putString(KEY_TOKEN, token);
        editor.putString(KEY_ROLE, String.valueOf(role));
        editor.putBoolean(KEY_IS_ADMIN, role == UserRole.ADMIN);
        editor.putBoolean(KEY_REMEMBER, remember);
        editor.apply();
    }

    // -------------------- 获取数据 --------------------

    public boolean isLoggedIn() {
        return prefs.contains(KEY_USER_ID);
    }

    public boolean isAdmin() {
        // 兼容旧版本：如果未写入 is_admin 标记，则根据角色兜底判断
        String role = prefs.getString(KEY_ROLE, UserRole.USER.name());
        return prefs.getBoolean(KEY_IS_ADMIN, false)
                || UserRole.ADMIN.name().equalsIgnoreCase(role);
    }

    public String getUserId() {
        return prefs.getString(KEY_USER_ID, "");
    }

    public String getUsername() {
        return prefs.getString(KEY_USERNAME, "未知用户");
    }

    public UserRole getRole() {
        String r = prefs.getString(KEY_ROLE, "USER");
        return UserRole.valueOf(r);
    }

    public String getToken() {
        return prefs.getString(KEY_TOKEN, "");
    }

    public boolean shouldRemember() {
        return prefs.getBoolean(KEY_REMEMBER, false);
    }

    public String getLastUsername() {
        return prefs.getString(KEY_LAST_USERNAME, "");
    }

    public String getLastSelectedRole() {
        return prefs.getString(KEY_LAST_ROLE, "USER");
    }

    public Set<String> getPermissions() {
        return prefs.getStringSet(KEY_PERMISSIONS, new HashSet<String>());
    }

    // -------------------- 修改数据 --------------------

    public void updateLoginPreference(String username, String role, boolean remember) {
        editor.putString(KEY_LAST_USERNAME, username);
        editor.putString(KEY_LAST_ROLE, role);
        editor.putBoolean(KEY_REMEMBER, remember);
        editor.apply();
    }

    public void updatePermissions(Set<String> permissions) {
        editor.putStringSet(KEY_PERMISSIONS, permissions);
        editor.apply();
    }

    // -------------------- 登出 --------------------

    public void clearSession() {
        editor.clear();
        editor.apply();
    }

    public void logout() {
        clearSession();
    }
}
