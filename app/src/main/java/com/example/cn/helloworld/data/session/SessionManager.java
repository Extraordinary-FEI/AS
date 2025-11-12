package com.example.cn.helloworld.data.session;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import com.example.cn.helloworld.data.model.LoginResult;
import com.example.cn.helloworld.data.model.Permission;
import com.example.cn.helloworld.data.model.UserRole;

import java.util.EnumSet;
import java.util.Set;

public class SessionManager {

    private static final String PREF_NAME = "auth_session";
    private static final String KEY_USERNAME = "username";
    private static final String KEY_TOKEN = "token";
    private static final String KEY_ROLE = "role";
    private static final String KEY_PERMISSIONS = "permissions";

    private final SharedPreferences sharedPreferences;

    public SessionManager(Context context) {
        this.sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public void saveSession(LoginResult result) {
        if (result == null || !result.isSuccess()) {
            return;
        }
        sharedPreferences.edit()
                .putString(KEY_USERNAME, result.getUsername())
                .putString(KEY_TOKEN, result.getToken())
                .putString(KEY_ROLE, result.getRole().name())
                .putString(KEY_PERMISSIONS, serializePermissions(result.getPermissions()))
                .apply();
    }

    public boolean isLoggedIn() {
        return !TextUtils.isEmpty(sharedPreferences.getString(KEY_TOKEN, null))
                && !TextUtils.isEmpty(sharedPreferences.getString(KEY_ROLE, null));
    }

    public String getUsername() {
        return sharedPreferences.getString(KEY_USERNAME, "");
    }

    public String getToken() {
        return sharedPreferences.getString(KEY_TOKEN, "");
    }

    public UserRole getRole() {
        return UserRole.fromValue(sharedPreferences.getString(KEY_ROLE, null));
    }

    public boolean hasRole(UserRole role) {
        return getRole() == role;
    }

    public boolean isAdmin() {
        return hasRole(UserRole.ADMIN);
    }

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

    public boolean hasPermission(Permission permission) {
        return getPermissions().contains(permission);
    }

    public void clearSession() {
        sharedPreferences.edit().clear().apply();
    }

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
