package com.example.cn.helloworld.data.session;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import com.example.cn.helloworld.data.model.LoginResult;

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

    public void saveSession(LoginResult result, boolean rememberMe) {
        if (result == null || !result.isSuccess()) {
            return;
        }
        sharedPreferences.edit()
                .putString(KEY_USERNAME, result.getUsername())
                .putString(KEY_TOKEN, result.getToken())
                .putString(KEY_ROLE, result.getRole())
                .putString(KEY_PERMISSIONS, result.getPermissions())
                .putBoolean(KEY_REMEMBER_ME, rememberMe)
                .putString(KEY_LAST_ROLE, result.getRole())
                .putString(KEY_LAST_USERNAME, result.getUsername())
                .apply();
    }

    public boolean isLoggedIn() {
        return !TextUtils.isEmpty(sharedPreferences.getString(KEY_TOKEN, null))
                && !TextUtils.isEmpty(sharedPreferences.getString(KEY_ROLE, null));
    }

    public boolean shouldRemember() {
        return sharedPreferences.getBoolean(KEY_REMEMBER_ME, false);
    }

    public String getUsername() {
        return sharedPreferences.getString(KEY_USERNAME, "");
    }

    public String getToken() {
        return sharedPreferences.getString(KEY_TOKEN, "");
    }

    public String getRole() {
        return sharedPreferences.getString(KEY_ROLE, "");
    }

    public String getPermissions() {
        return sharedPreferences.getString(KEY_PERMISSIONS, "");
    }

    public String getLastUsername() {
        return sharedPreferences.getString(KEY_LAST_USERNAME, "");
    }

    public String getLastSelectedRole() {
        return sharedPreferences.getString(KEY_LAST_ROLE, "");
    }

    public void updateLoginPreference(String username, String role, boolean rememberMe) {
        sharedPreferences.edit()
                .putString(KEY_LAST_USERNAME, username)
                .putString(KEY_LAST_ROLE, role)
                .putBoolean(KEY_REMEMBER_ME, rememberMe)
                .apply();
    }

    public void clearSession() {
        sharedPreferences.edit()
                .remove(KEY_USERNAME)
                .remove(KEY_TOKEN)
                .remove(KEY_ROLE)
                .remove(KEY_PERMISSIONS)
                .remove(KEY_REMEMBER_ME)
                .apply();
    }
}
