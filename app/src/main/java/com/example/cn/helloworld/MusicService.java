package com.example.cn.helloworld;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;
import android.widget.Toast;

import com.example.cn.helloworld.data.model.Playlist;
import com.example.cn.helloworld.data.model.Song;
import com.example.cn.helloworld.data.playlist.PlaylistRepository;

import java.util.Collections;
import java.util.List;


public class MusicService extends Service {

    public static final String ACTION_PLAY_SONG = "com.example.cn.helloworld.action.PLAY_SONG";
    public static final String ACTION_UPDATE_UI = "ACTION_UPDATE_UI";
    public static final String ACTION_HIDE_FLOATING_MUSIC =
            "com.example.cn.helloworld.action.HIDE_FLOATING_MUSIC";
    public static final String EXTRA_PLAYLIST_ID = "extra_playlist_id";
    public static final String EXTRA_SONG_ID = "extra_song_id";

    private MediaPlayer player;
    private MusicReceiver musicReceiver;
    private PlaylistRepository playlistRepository;
    private Playlist currentPlaylist;
    private List<Song> currentSongs = Collections.emptyList();
    private int currentIndex = 0;

    @Override
    public void onCreate() {
        super.onCreate();
        playlistRepository = PlaylistRepository.getInstance(this);
        ensureDefaultPlaylist();
        preparePlayer();

        // 注册外部接收器
        musicReceiver = new MusicReceiver(this);
        musicReceiver.register(this);

        showNotification("音乐播放器", "准备播放");
        updateUI();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null && ACTION_PLAY_SONG.equals(intent.getAction())) {
            handlePlaySongRequest(intent.getStringExtra(EXTRA_PLAYLIST_ID), intent.getStringExtra(EXTRA_SONG_ID));
        }
        return START_STICKY;
    }

    public static Intent createPlaySongIntent(Context context, String playlistId, String songId) {
        Intent intent = new Intent(context, MusicService.class);
        intent.setAction(ACTION_PLAY_SONG);
        intent.putExtra(EXTRA_PLAYLIST_ID, playlistId);
        intent.putExtra(EXTRA_SONG_ID, songId);
        return intent;
    }

    public void playMusic() {
        if (player == null) {
            preparePlayer();
        }
        if (player != null && !player.isPlaying()) {
            player.start();
            showNotification("正在播放", getCurrentSongTitle());
            updateUI();
        }
    }

    public void pauseMusic() {
        if (player != null && player.isPlaying()) {
            player.pause();
            showNotification("已暂停", getCurrentSongTitle());
            updateUI();
        }
    }

    public void stopMusic() {
        if (player != null) {
            releasePlayer();
            showNotification("已停止", getCurrentSongTitle());
            updateUI();
        }
    }

    public void nextMusic() {
        if (currentSongs.isEmpty()) return;
        currentIndex = (currentIndex + 1) % currentSongs.size();
        switchMusic();
    }

    public void prevMusic() {
        if (currentSongs.isEmpty()) return;
        currentIndex = (currentIndex - 1 + currentSongs.size()) % currentSongs.size();
        switchMusic();
    }

    private void switchMusic() {
        releasePlayer();
        preparePlayer();
        if (player != null) {
            player.start();
            showNotification("正在播放", getCurrentSongTitle());
            updateUI();
        }
    }

    private void showNotification(String title, String text) {
        Intent intent = new Intent(this, MusicActivity.class);
        PendingIntent pi = PendingIntent.getActivity(
                this,
                0,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT
        );

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
        Song currentSong = getCurrentSong();
        uiIntent.putExtra("title", currentSong != null ? currentSong.getTitle() : getString(R.string.app_name));
        uiIntent.putExtra("artist", currentSong != null ? currentSong.getArtist() : "");
        uiIntent.putExtra("coverResId", currentSong != null ? currentSong.getCoverResId() : R.drawable.cover_playlist_placeholder);
        uiIntent.putExtra("index", currentSong != null ? currentIndex : -1);
        uiIntent.putExtra("total", currentSongs != null ? currentSongs.size() : 0);
        uiIntent.putExtra("playlistTitle", currentPlaylist != null ? currentPlaylist.getTitle() : "");
        uiIntent.putExtra("playing", player != null && player.isPlaying());
        sendBroadcast(uiIntent);
    }

    @Override
    public void onDestroy() {
        musicReceiver.unregister(this);
        releasePlayer();
        sendBroadcast(new Intent(ACTION_HIDE_FLOATING_MUSIC));
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void ensureDefaultPlaylist() {
        if (currentPlaylist == null) {
            List<Playlist> all = playlistRepository.getAllPlaylists();
            if (!all.isEmpty()) {
                setCurrentPlaylist(all.get(0));
            }
        }
    }

    private void setCurrentPlaylist(Playlist playlist) {
        currentPlaylist = playlist;
        currentSongs = playlist.getSongs() == null ? Collections.<Song>emptyList() : playlist.getSongs();
        currentIndex = 0;
    }

    private void preparePlayer() {
        Song currentSong = getCurrentSong();
        if (currentSong == null) {
            return;
        }

        if (currentSong.getAudioResId() == 0) {
            Toast.makeText(this, R.string.error_song_not_found, Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            player = MediaPlayer.create(this, currentSong.getAudioResId());
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

    private void releasePlayer() {
        if (player != null) {
            try {
                player.stop();
            } catch (IllegalStateException ignored) {
            }
            player.release();
            player = null;
        }
    }

    private Song getCurrentSong() {
        if (currentSongs == null || currentSongs.isEmpty()) {
            return null;
        }
        if (currentIndex < 0 || currentIndex >= currentSongs.size()) {
            currentIndex = 0;
        }
        return currentSongs.get(currentIndex);
    }

    private void handlePlaySongRequest(String playlistId, String songId) {
        if (TextUtils.isEmpty(playlistId) && TextUtils.isEmpty(songId)) {
            return;
        }

        if (!TextUtils.isEmpty(playlistId)) {
            Playlist playlist = playlistRepository.getById(playlistId);
            if (playlist == null) {
                Toast.makeText(this, R.string.error_playlist_not_found, Toast.LENGTH_SHORT).show();
                return;
            }
            setCurrentPlaylist(playlist);
        } else {
            ensureDefaultPlaylist();
        }

        if (!TextUtils.isEmpty(songId)) {
            int index = findSongIndex(songId);
            if (index == -1) {
                Toast.makeText(this, R.string.error_song_not_found, Toast.LENGTH_SHORT).show();
                return;
            }
            currentIndex = index;
        }

        switchMusic();
    }

    private int findSongIndex(String songId) {
        if (currentSongs == null) return -1;
        for (int i = 0; i < currentSongs.size(); i++) {
            Song song = currentSongs.get(i);
            if (songId.equals(song.getId())) {
                return i;
            }
        }
        return -1;
    }

    private String getCurrentSongTitle() {
        Song song = getCurrentSong();
        return song != null ? song.getTitle() : getString(R.string.app_name);
    }
}
