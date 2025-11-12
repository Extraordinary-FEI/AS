package com.example.cn.helloworld.data.session;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import com.example.cn.helloworld.data.model.LoginResult;
import com.example.cn.helloworld.data.model.Permission;
import com.example.cn.helloworld.data.model.UserRole;

import java.util.EnumSet;
import java.util.Set;

/**
 * 管理用户会话与权限状态，包括记住登录信息、角色、权限等。
 */
public class SessionManager {

    private static final String PREF_NAME = "auth_session";
    private static final String KEY_USERNAME = "username";
    private static final String KEY_TOKEN = "token";
    private static final String KEY_ROLE = "role";
    private static final String KEY_PERMISSIONS = "permissions";
    private static final String KEY_REMEMBER_ME = "remember_me";
    private static final String KEY_LAST_ROLE = "last_role";
    private static final String KEY_LAST_USERNAME = "last_username";

    private final SharedPreferences sharedPreferences;

    public SessionManager(Context context) {
        this.sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    /**
     * 保存登录会话信息。
     */
    public void saveSession(LoginResult result, boolean rememberMe) {
        if (result == null || !result.isSuccess()) {
            return;
        }

        sharedPreferences.edit()
                .putString(KEY_USERNAME, result.getUsername())
                .putString(KEY_TOKEN, result.getToken())
                .putString(KEY_ROLE, result.getRole().name())
                .putString(KEY_PERMISSIONS, serializePermissions(result.getPermissions()))
                .putBoolean(KEY_REMEMBER_ME, rememberMe)
                .putString(KEY_LAST_ROLE, result.getRole().name())
                .putString(KEY_LAST_USERNAME, result.getUsername())
                .apply();
    }

    /** 是否登录中 */
    public boolean isLoggedIn() {
        return !TextUtils.isEmpty(sharedPreferences.getString(KEY_TOKEN, null))
                && !TextUtils.isEmpty(sharedPreferences.getString(KEY_ROLE, null));
    }

    /** 是否记住我 */
    public boolean shouldRemember() {
        return sharedPreferences.getBoolean(KEY_REMEMBER_ME, false);
    }

    /** 获取当前用户名 */
    public String getUsername() {
        return sharedPreferences.getString(KEY_USERNAME, "");
    }

    /** 获取当前 Token */
    public String getToken() {
        return sharedPreferences.getString(KEY_TOKEN, "");
    }

    /** 获取当前角色 */
    public UserRole getRole() {
        return UserRole.fromValue(sharedPreferences.getString(KEY_ROLE, null));
    }

    /** 判断是否拥有指定角色 */
    public boolean hasRole(UserRole role) {
        return getRole() == role;
    }

    /** 判断是否为管理员 */
    public boolean isAdmin() {
        return hasRole(UserRole.ADMIN);
    }

    /** 获取权限集合 */
    public Set<Permission> getPermissions() {
        String stored = sharedPreferences.getString(KEY_PERMISSIONS, "");
        EnumSet<Permission> permissions = EnumSet.noneOf(Permission.class);
        if (!TextUtils.isEmpty(stored)) {
            String[] parts = stored.split(",");
            for (String part : parts) {
                Permission permission = Permission.fromValue(part);
                if (permission != null) {
                    permissions.add(permission);
                }
            }
        }
        return permissions;
    }

    /** 是否具有指定权限 */
    public boolean hasPermission(Permission permission) {
        return getPermissions().contains(permission);
    }

    /** 最近登录用户名 */
    public String getLastUsername() {
        return sharedPreferences.getString(KEY_LAST_USERNAME, "");
    }

    /** 最近选择的角色 */
    public String getLastSelectedRole() {
        return sharedPreferences.getString(KEY_LAST_ROLE, "");
    }

    /** 更新“记住我”偏好 */
    public void updateLoginPreference(String username, String role, boolean rememberMe) {
        sharedPreferences.edit()
                .putString(KEY_LAST_USERNAME, username)
                .putString(KEY_LAST_ROLE, role)
                .putBoolean(KEY_REMEMBER_ME, rememberMe)
                .apply();
    }

    /** 清除登录会话 */
    public void clearSession() {
        sharedPreferences.edit()
                .remove(KEY_USERNAME)
                .remove(KEY_TOKEN)
                .remove(KEY_ROLE)
                .remove(KEY_PERMISSIONS)
                .remove(KEY_REMEMBER_ME)
                .apply();
    }

    /** 将权限集合序列化为字符串 */
    private String serializePermissions(Set<Permission> permissions) {
        if (permissions == null || permissions.isEmpty()) {
            return "";
        }
        StringBuilder builder = new StringBuilder();
        for (Permission permission : permissions) {
            if (builder.length() > 0) {
                builder.append(',');
            }
            builder.append(permission.name());
        }
        return builder.toString();
    }
}
