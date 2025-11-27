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
import android.widget.Toast;

import com.example.cn.helloworld.data.repository.FavoriteRepository;

import com.example.cn.helloworld.ui.main.MainActivity;

public class MusicActivity extends Activity {

    public static final String EXTRA_PLAYLIST_ID = "extra_playlist_id";
    public static final String EXTRA_SONG_ID = "extra_song_id";

    private ImageButton btnPlayPause, btnStop, btnNext, btnPrev, btnBack, btnFavorite;
    private ImageView imgCover;
    private TextView tvSongName;
    private FavoriteRepository favoriteRepository;
    private String currentSongId;

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
        btnBack = (ImageButton) findViewById(R.id.btn_back);
        btnFavorite = (ImageButton) findViewById(R.id.btn_favorite_song);
        imgCover = (ImageView) findViewById(R.id.img_cover);
        tvSongName = (TextView) findViewById(R.id.tv_song_name);
        favoriteRepository = new FavoriteRepository(this);

        String playlistId = getIntent().getStringExtra(EXTRA_PLAYLIST_ID);
        String songId = getIntent().getStringExtra(EXTRA_SONG_ID);
        currentSongId = songId;

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

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MusicActivity.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
                finish();
            }
        });

        btnFavorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleSongFavorite();
            }
        });
        updateFavoriteIcon(false);
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
                if (MusicService.ACTION_UPDATE_UI.equals(intent.getAction())) {

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

                    currentSongId = intent.getStringExtra("songId");
                    updateFavoriteIcon(false);

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

        IntentFilter filter = new IntentFilter(MusicService.ACTION_UPDATE_UI);
        registerReceiver(uiUpdateReceiver, filter);
    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(uiUpdateReceiver);
        super.onDestroy();
    }

    private void toggleSongFavorite() {
        if (currentSongId == null || currentSongId.isEmpty()) {
            Toast.makeText(this, R.string.error_song_not_found, Toast.LENGTH_SHORT).show();
            return;
        }
        boolean favored = favoriteRepository.isSongFavorite(currentSongId);
        favoriteRepository.setSongFavorite(currentSongId, !favored);
        updateFavoriteIcon(true);
    }

    private void updateFavoriteIcon(boolean animate) {
        boolean favored = currentSongId != null && favoriteRepository.isSongFavorite(currentSongId);
        btnFavorite.setImageResource(favored ? R.drawable.ic_heart_filled_red : R.drawable.ic_heart_outline_white);
        btnFavorite.setContentDescription(getString(favored ? R.string.favorite_added : R.string.favorite_removed));
        if (animate) {
            btnFavorite.animate().cancel();
            btnFavorite.setScaleX(0.85f);
            btnFavorite.setScaleY(0.85f);
            btnFavorite.animate()
                    .scaleX(1.15f)
                    .scaleY(1.15f)
                    .setDuration(150)
                    .withEndAction(new Runnable() {
                        @Override
                        public void run() {
                            btnFavorite.animate().scaleX(1f).scaleY(1f).setDuration(120).start();
                        }
                    })
                    .start();
        }
    }
}
