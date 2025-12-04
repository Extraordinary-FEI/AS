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

        // 初始化本地存储
        AdminLocalStore.init(this);

        // 最重要：初始化歌单仓库
        com.example.cn.helloworld.data.playlist.PlaylistRepository.getInstance(this);
    }
}
