package com.example.cn.helloworld;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.widget.Toast;

public class MusicService extends Service {

    private MediaPlayer player;
    private MusicReceiver musicReceiver;
    private int currentIndex = 0;

    // 歌曲资源数组（已去掉If You）
    private int[] songs = {
            R.raw.yyqx_lisao,
            R.raw.yyqx_nishuo,
            R.raw.yyqx_baobei
    };

    private String[] titles = {"离骚", "你说", "宝贝"};

    @Override
    public void onCreate() {
        super.onCreate();
        initPlayer();

        // 注册外部接收器
        musicReceiver = new MusicReceiver(this);
        musicReceiver.register(this);

        showNotification("音乐播放器", "准备播放");
        updateUI();
    }

    private void initPlayer() {
        if (player != null) {
            player.release();
        }

        try {
            player = MediaPlayer.create(this, songs[currentIndex]);
            if (player == null) {
                throw new Exception("无法加载音频文件，请检查格式");
            }

            player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    nextMusic();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "音乐加载失败", Toast.LENGTH_SHORT).show();
        }
    }

    public void playMusic() {
        if (player != null && !player.isPlaying()) {
            player.start();
            showNotification("正在播放", titles[currentIndex]);
            updateUI();
        }
    }

    public void pauseMusic() {
        if (player != null && player.isPlaying()) {
            player.pause();
            showNotification("已暂停", titles[currentIndex]);
            updateUI();
        }
    }

    public void stopMusic() {
        if (player != null) {
            player.stop();
            initPlayer();
            showNotification("已停止", titles[currentIndex]);
            updateUI();
        }
    }

    public void nextMusic() {
        currentIndex = (currentIndex + 1) % songs.length;
        switchMusic();
    }

    public void prevMusic() {
        currentIndex = (currentIndex - 1 + songs.length) % songs.length;
        switchMusic();
    }

    private void switchMusic() {
        try {
            if (player != null) {
                player.stop();
                player.release();
            }
            player = MediaPlayer.create(this, songs[currentIndex]);
            if (player == null) {
                Toast.makeText(this, "该歌曲无法播放", Toast.LENGTH_SHORT).show();
                return;
            }
            player.start();
            showNotification("正在播放", titles[currentIndex]);
            updateUI();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "切换音乐失败", Toast.LENGTH_SHORT).show();
        }
    }

    private void showNotification(String title, String text) {
        Intent intent = new Intent(this, MusicActivity.class);
        PendingIntent pi = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        Notification notification = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.logo)
                .setContentTitle(title)
                .setContentText(text)
                .setContentIntent(pi)
                .build();

        startForeground(1, notification);
    }

    /** 发送UI更新广播 **/
    private void updateUI() {
        Intent uiIntent = new Intent("ACTION_UPDATE_UI");
        uiIntent.putExtra("title", titles[currentIndex]);
        uiIntent.putExtra("index", currentIndex);
        uiIntent.putExtra("total", titles.length);
        uiIntent.putExtra("playing", player != null && player.isPlaying());
        sendBroadcast(uiIntent);
    }

    @Override
    public void onDestroy() {
        musicReceiver.unregister(this);
        if (player != null) {
            player.stop();
            player.release();
        }
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
