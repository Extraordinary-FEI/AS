package com.example.cn.helloworld.data.session;

import android.content.Context;
import android.content.SharedPreferences;

public class SessionManager {

    private static final String PREF_NAME = "user_session";
    private static final String KEY_USER_ID = "user_id";
    private static final String KEY_USERNAME = "username";
    private static final String KEY_IS_ADMIN = "is_admin";

    private SharedPreferences prefs;
    private SharedPreferences.Editor editor;

    public SessionManager(Context context) {
        prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = prefs.edit();
    }

    /** 保存登录信息（普通用户） */
    public void login(String userId, String username) {
        editor.putString(KEY_USER_ID, userId);
        editor.putString(KEY_USERNAME, username);
        editor.putBoolean(KEY_IS_ADMIN, false);
        editor.apply();
    }

    /** 保存登录信息（管理员） */
    public void loginAdmin(String userId, String username) {
        editor.putString(KEY_USER_ID, userId);
        editor.putString(KEY_USERNAME, username);
        editor.putBoolean(KEY_IS_ADMIN, true);
        editor.apply();
    }

    /** 是否已登录 */
    public boolean isLoggedIn() {
        return prefs.contains(KEY_USER_ID);
    }

    /** 是否管理员登录 */
    public boolean isAdmin() {
        return prefs.getBoolean(KEY_IS_ADMIN, false);
    }

    /** 获取用户 ID */
    public String getUserId() {
        return prefs.getString(KEY_USER_ID, "");
    }

    /** 获取用户名 */
    public String getUsername() {
        return prefs.getString(KEY_USERNAME, "未知用户");
    }

    /** 退出登录（关键方法） */
    public void logout() {
        editor.clear();
        editor.apply();
    }
}
