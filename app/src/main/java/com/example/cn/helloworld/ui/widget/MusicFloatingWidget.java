package com.example.cn.helloworld.ui.widget;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.cn.helloworld.MusicActivity;
import com.example.cn.helloworld.R;

/**
 * 用于在各个页面展示音乐播放悬浮窗，并通过广播控制 MusicService。
 */
public class MusicFloatingWidget {

    private final Activity activity;
    private final View container;
    private final ImageView coverView;
    private final TextView titleView;
    private final TextView subtitleView;
    private final ImageButton playPauseButton;
    private BroadcastReceiver uiReceiver;
    private boolean isPlaying = false;

    public MusicFloatingWidget(Activity activity) {
        this.activity = activity;
        this.container = activity.findViewById(R.id.music_floating_container);

        if (container == null) {
            coverView = null;
            titleView = null;
            subtitleView = null;
            playPauseButton = null;
            return;
        }

        coverView = (ImageView) container.findViewById(R.id.music_cover);
        titleView = (TextView) container.findViewById(R.id.music_title);
        subtitleView = (TextView) container.findViewById(R.id.music_subtitle);
        playPauseButton = (ImageButton) container.findViewById(R.id.music_action_play);
        ImageButton closeButton = (ImageButton) container.findViewById(R.id.music_action_close);

        container.setVisibility(View.GONE);

        container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFullPlayer();
            }
        });

        playPauseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                togglePlayState();
            }
        });

        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopPlayback();
                container.setVisibility(View.GONE);
            }
        });
    }

    public void start() {
        if (container == null || uiReceiver != null) {
            return;
        }

        IntentFilter filter = new IntentFilter("ACTION_UPDATE_UI");
        uiReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                handleUpdate(intent);
            }
        };
        activity.registerReceiver(uiReceiver, filter);
    }

    public void stop() {
        if (uiReceiver != null) {
            try {
                activity.unregisterReceiver(uiReceiver);
            } catch (IllegalArgumentException ignored) {
            }
            uiReceiver = null;
        }
    }

    private void handleUpdate(@Nullable Intent intent) {
        if (intent == null || container == null) {
            return;
        }

        boolean hasSong = intent.getIntExtra("total", 0) > 0;
        if (!hasSong) {
            container.setVisibility(View.GONE);
            return;
        }

        container.setVisibility(View.VISIBLE);

        String title = intent.getStringExtra("title");
        String artist = intent.getStringExtra("artist");
        String playlistTitle = intent.getStringExtra("playlistTitle");
        int coverResId = intent.getIntExtra("coverResId", R.drawable.cover_playlist_placeholder);
        isPlaying = intent.getBooleanExtra("playing", false);

        if (titleView != null) {
            titleView.setText(title == null ? activity.getString(R.string.app_name) : title);
        }

        if (subtitleView != null) {
            StringBuilder subtitle = new StringBuilder();
            if (!isEmpty(artist)) {
                subtitle.append(artist);
            }
            if (!isEmpty(playlistTitle)) {
                if (subtitle.length() > 0) {
                    subtitle.append(" · ");
                }
                subtitle.append(playlistTitle);
            }
            subtitleView.setText(subtitle.toString());
        }

        if (coverView != null) {
            coverView.setImageResource(coverResId);
        }

        updatePlayButton();
    }

    private void updatePlayButton() {
        if (playPauseButton != null) {
            playPauseButton.setImageResource(isPlaying ? R.drawable.pause : R.drawable.play);
        }
    }

    private void togglePlayState() {
        if (isPlaying) {
            activity.sendBroadcast(new Intent("ACTION_PAUSE"));
        } else {
            activity.sendBroadcast(new Intent("ACTION_PLAY"));
        }
    }

    private void stopPlayback() {
        activity.sendBroadcast(new Intent("ACTION_STOP"));
    }

    private void openFullPlayer() {
        Intent intent = new Intent(activity, MusicActivity.class);
        try {
            activity.startActivity(intent);
        } catch (ActivityNotFoundException ignore) {
        }
    }

    private boolean isEmpty(@Nullable String text) {
        return text == null || text.trim().length() == 0;
    }
}
