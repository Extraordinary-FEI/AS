package com.example.cn.helloworld;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

public class MusicReceiver extends BroadcastReceiver {
    private MusicService service;

    public MusicReceiver(MusicService service) {
        this.service = service;
    }

    public void register(Context context) {
        IntentFilter filter = new IntentFilter();
        filter.addAction("ACTION_PLAY");
        filter.addAction("ACTION_PAUSE");
        filter.addAction("ACTION_STOP");
        filter.addAction("ACTION_NEXT");
        filter.addAction("ACTION_PREV");
        context.registerReceiver(this, filter);
    }

    public void unregister(Context context) {
        context.unregisterReceiver(this);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if ("ACTION_PLAY".equals(action)) {
            service.playMusic();
        } else if ("ACTION_PAUSE".equals(action)) {
            service.pauseMusic();
        } else if ("ACTION_STOP".equals(action)) {
            service.stopMusic();
        } else if ("ACTION_NEXT".equals(action)) {
            service.nextMusic();
        } else if ("ACTION_PREV".equals(action)) {
            service.prevMusic();
        }
    }
}
