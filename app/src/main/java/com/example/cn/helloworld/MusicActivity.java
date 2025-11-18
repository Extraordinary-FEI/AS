package com.example.cn.helloworld;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

public class MusicActivity extends Activity {

    public static final String EXTRA_PLAYLIST_ID = "extra_playlist_id";
    public static final String EXTRA_SONG_ID = "extra_song_id";

    private ImageButton btnPlayPause, btnStop, btnNext, btnPrev;
    private ImageView imgCover;
    private TextView tvSongName;

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

        // 启动音乐服务
        Intent serviceIntent;
        if (playlistId != null || songId != null) {
            serviceIntent = MusicService.createPlaySongIntent(this, playlistId, songId);
        } else {
            serviceIntent = new Intent(this, MusicService.class);
        }
        startServiceCompat(serviceIntent);

        registerUIReceiver();

        // 播放/暂停
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

        // 停止
        btnStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendBroadcast(new Intent("ACTION_STOP"));
            }
        });

        // 下一曲
        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendBroadcast(new Intent("ACTION_NEXT"));
            }
        });

        // 上一曲
        btnPrev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendBroadcast(new Intent("ACTION_PREV"));
            }
        });
    }

    /**
     * 兼容 API 25 项目结构的 startForegroundService
     */
    private void startServiceCompat(Intent intent) {
        if (Build.VERSION.SDK_INT >= 26) {
            try {
                Activity.class.getMethod("startForegroundService", Intent.class).invoke(this, intent);
                return;
            } catch (Exception ignored) {}
        }
        startService(intent);
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
                    int coverResId = intent.getIntExtra("coverResId",
                            R.drawable.cover_playlist_placeholder);

                    isPlaying = intent.getBooleanExtra("playing", false);

                    String prefix = isPlaying ? "正在播放：" : "已暂停：";
                    String safeTitle = (title == null ? getString(R.string.app_name) : title);
                    String subtitle = (artist == null ? "" : " - " + artist);

                    if (index < 0 || total <= 0) {
                        index = 0;
                        total = 1;
                    }

                    tvSongName.setText(
                            prefix + safeTitle + subtitle + " (" + (index + 1) + "/" + total + ")"
                    );
                    imgCover.setImageResource(coverResId);

                    btnPlayPause.setImageResource(
                            isPlaying ? R.drawable.pause : R.drawable.play
                    );
                }
            }
        };

        IntentFilter filter = new IntentFilter("ACTION_UPDATE_UI");
        registerReceiver(uiUpdateReceiver, filter);
    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(uiUpdateReceiver);
        super.onDestroy();
    }
}
