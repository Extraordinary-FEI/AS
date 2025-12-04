package com.example.cn.helloworld;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;                      // ★ 新增
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
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
    private String currentSongId = "";

    private boolean isPlaying = false;
    private BroadcastReceiver uiUpdateReceiver;

    public static Intent createIntent(Context context, String playlistId, String songId) {
        Intent intent = new Intent(context, MusicActivity.class);
        intent.putExtra(EXTRA_PLAYLIST_ID, playlistId);
        intent.putExtra(EXTRA_SONG_ID, songId);
        return intent;
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
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
        currentSongId = getIntent().getStringExtra(EXTRA_SONG_ID);

        // 启动音乐服务
        Intent serviceIntent;
        if (playlistId != null || currentSongId != null) {
            serviceIntent = MusicService.createPlaySongIntent(this, playlistId, currentSongId);
        } else {
            serviceIntent = new Intent(this, MusicService.class);
        }
        startServiceCompat(serviceIntent);

        registerUIReceiver();

        // 播放与暂停
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

        // 下一首
        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendBroadcast(new Intent("ACTION_NEXT"));
            }
        });

        // 上一首
        btnPrev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendBroadcast(new Intent("ACTION_PREV"));
            }
        });

        // 返回
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MusicActivity.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
                finish();
            }
        });

        // 收藏
        btnFavorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleSongFavorite();
            }
        });

        updateFavoriteIcon(false);
    }

    /** 兼容低版本的前台服务启动方式 */
    private void startServiceCompat(Intent intent) {
        if (Build.VERSION.SDK_INT >= 26) {
            try {
                Activity.class.getMethod("startForegroundService", Intent.class)
                        .invoke(this, intent);
                return;
            } catch (Exception ignored) {}
        }
        startService(intent);
    }

    /** 注册 UI 更新广播 */
    private void registerUIReceiver() {
        uiUpdateReceiver = new BroadcastReceiver() {
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void onReceive(Context context, Intent intent) {
                if (MusicService.ACTION_UPDATE_UI.equals(intent.getAction())) {

                    String title = intent.getStringExtra("title");
                    String artist = intent.getStringExtra("artist");
                    int index = intent.getIntExtra("index", 0);
                    int total = intent.getIntExtra("total", 1);
                    boolean playing = intent.getBooleanExtra("playing", false);

                    // 新增：封面字段
                    String coverPath = intent.getStringExtra("coverPath");
                    int coverResId = intent.getIntExtra(
                            "coverResId",
                            R.drawable.cover_playlist_placeholder
                    );

                    currentSongId = intent.getStringExtra("songId");
                    isPlaying = playing;

                    // 显示歌曲名
                    String prefix = playing ? "正在播放：" : "已暂停：";
                    String safeTitle = (title == null ? getString(R.string.app_name) : title);
                    String subtitle = (artist == null ? "" : " - " + artist);

                    tvSongName.setText(prefix + safeTitle + subtitle +
                            " (" + (index + 1) + "/" + total + ")");

                    // 显示封面：优先使用自定义封面路径，其次使用资源ID
                    if (coverPath != null && coverPath.length() > 0) {
                        imgCover.setImageURI(Uri.parse(coverPath));
                    } else {
                        imgCover.setImageResource(coverResId);
                    }

                    // 切换按钮图标
                    btnPlayPause.setImageResource(
                            isPlaying ? R.drawable.pause : R.drawable.play
                    );

                    // 更新收藏图标
                    updateFavoriteIcon(false);
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

    /** 切换收藏 */
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    private void toggleSongFavorite() {
        if (currentSongId == null || currentSongId.length() == 0) {
            Toast.makeText(this, R.string.error_song_not_found, Toast.LENGTH_SHORT).show();
            return;
        }

        boolean favored = favoriteRepository.isSongFavorite(currentSongId);
        favoriteRepository.setSongFavorite(currentSongId, !favored);

        updateFavoriteIcon(true);
    }

    /** 更新收藏图标 */
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    private void updateFavoriteIcon(boolean animate) {
        boolean favored = currentSongId != null &&
                favoriteRepository.isSongFavorite(currentSongId);

        btnFavorite.setImageResource(
                favored ? R.drawable.ic_heart_filled_red : R.drawable.ic_heart_outline_white
        );

        if (animate) {
            btnFavorite.setScaleX(0.85f);
            btnFavorite.setScaleY(0.85f);
            btnFavorite.animate()
                    .scaleX(1.15f).scaleY(1.15f)
                    .setDuration(150)
                    .withEndAction(new Runnable() {
                        @Override
                        public void run() {
                            btnFavorite.animate()
                                    .scaleX(1f).scaleY(1f)
                                    .setDuration(120)
                                    .start();
                        }
                    })
                    .start();
        }
    }
}
