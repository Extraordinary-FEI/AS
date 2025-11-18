package com.example.cn.helloworld;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import android.support.v4.content.ContextCompat;

public class MusicActivity extends Activity {

    public static final String EXTRA_PLAYLIST_ID = "extra_playlist_id";
    public static final String EXTRA_SONG_ID = "extra_song_id";

    private ImageButton btnPlayPause, btnStop, btnNext, btnPrev;
    private ImageView imgCover;
    private TextView tvSongName;

    // 当前播放状态
    private boolean isPlaying = false;

    private BroadcastReceiver uiUpdateReceiver;

    public static Intent createIntent(Context context, String playlistId, String songId) {
        Intent intent = new Intent(context, MusicActivity.class);
        intent.putExtra(EXTRA_PLAYLIST_ID, playlistId);
        intent.putExtra(EXTRA_SONG_ID, songId);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music);

        btnPlayPause = (ImageButton) findViewById(R.id.btn_play);
        btnStop = (ImageButton) findViewById(R.id.btn_stop);
        btnNext = (ImageButton) findViewById(R.id.btn_next);
        btnPrev = (ImageButton) findViewById(R.id.btn_prev);
        imgCover = (ImageView) findViewById(R.id.img_cover);
        tvSongName = (TextView) findViewById(R.id.tv_song_name);

        String playlistId = getIntent().getStringExtra(EXTRA_PLAYLIST_ID);
        String songId = getIntent().getStringExtra(EXTRA_SONG_ID);
        if (playlistId != null || songId != null) {
            Intent serviceIntent = MusicService.createPlaySongIntent(this, playlistId, songId);
            ContextCompat.startForegroundService(this, serviceIntent);
        } else {
            startService(new Intent(this, MusicService.class));
        }
        registerUIReceiver();

        // 点击播放/暂停键
        btnPlayPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isPlaying) {
                    sendBroadcast(new Intent("ACTION_PAUSE"));
                } else {
                    sendBroadcast(new Intent("ACTION_PLAY"));
                }
            }
        });

        btnStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendBroadcast(new Intent("ACTION_STOP"));
            }
        });

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendBroadcast(new Intent("ACTION_NEXT"));
            }
        });

        btnPrev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendBroadcast(new Intent("ACTION_PREV"));
            }
        });
    }

    private void registerUIReceiver() {
        uiUpdateReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if ("ACTION_UPDATE_UI".equals(intent.getAction())) {
                    String title = intent.getStringExtra("title");
                    String artist = intent.getStringExtra("artist");
                    int index = intent.getIntExtra("index", 0);
                    int total = intent.getIntExtra("total", 1);
                    int coverResId = intent.getIntExtra("coverResId", R.drawable.cover_playlist_placeholder);
                    isPlaying = intent.getBooleanExtra("playing", false);

                    String prefix = isPlaying ? "正在播放：" : "已暂停：";
                    String safeTitle = title == null ? getString(R.string.app_name) : title;
                    String subtitle = artist == null ? "" : " - " + artist;
                    if (index < 0 || total <= 0) {
                        index = 0;
                        total = 1;
                    }
                    tvSongName.setText(prefix + safeTitle + subtitle + " (" + (index + 1) + "/" + total + ")");
                    imgCover.setImageResource(coverResId);

                    // ✅ 根据播放状态更新图标
                    if (isPlaying) {
                        btnPlayPause.setImageResource(R.drawable.pause); // 替换为你的暂停图标
                    } else {
                        btnPlayPause.setImageResource(R.drawable.play);  // 替换为你的播放图标
                    }
                }
            }
        };

        IntentFilter filter = new IntentFilter();
        filter.addAction("ACTION_UPDATE_UI");
        registerReceiver(uiUpdateReceiver, filter);
    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(uiUpdateReceiver);
        super.onDestroy();
    }
}
