package com.example.cn.helloworld.data.storage;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * 简单的 SharedPreferences 包装类，集中管理管理员模块需要持久化的数据。
 */
public final class AdminLocalStore {

    private static final String PREF_NAME = "admin_persistence_store";

    private static SharedPreferences sharedPreferences;

    private AdminLocalStore() {
    }

    public static synchronized void init(Context context) {
        if (sharedPreferences != null) {
            return;
        }
        if (context == null) {
            return;
        }
        sharedPreferences = context.getApplicationContext()
                .getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public static synchronized SharedPreferences get(Context context) {
        if (sharedPreferences == null) {
            init(context);
        }
        if (sharedPreferences == null) {
            throw new IllegalStateException("AdminLocalStore is not initialized");
        }
        return sharedPreferences;
    }

    public static synchronized SharedPreferences get() {
        if (sharedPreferences == null) {
            throw new IllegalStateException("AdminLocalStore is not initialized");
        }
        return sharedPreferences;
    }

    public static synchronized boolean isInitialized() {
        return sharedPreferences != null;
    }
}
