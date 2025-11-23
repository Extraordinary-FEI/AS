package com.example.cn.helloworld;

import android.app.Application;

import com.example.cn.helloworld.data.storage.AdminLocalStore;

/**
 * Global application entry to ensure admin persistence is ready before any screen runs.
 */
public class AdminApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        // Initialize admin shared storage at process start so all repositories can persist updates.
        AdminLocalStore.init(this);
    }
}