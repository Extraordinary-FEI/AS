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
                .putString(KEY_ROLE, result.getRole())
                .putString(KEY_PERMISSIONS, result.getPermissions())
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

    public String getRole() {
        return sharedPreferences.getString(KEY_ROLE, "");
    }

    public String getPermissions() {
        return sharedPreferences.getString(KEY_PERMISSIONS, "");
    }

    public void clearSession() {
        sharedPreferences.edit().clear().apply();
    }
}
