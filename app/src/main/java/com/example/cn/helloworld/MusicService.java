package com.example.cn.helloworld;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;
import android.widget.Toast;

import com.example.cn.helloworld.data.model.Playlist;
import com.example.cn.helloworld.data.model.Song;
import com.example.cn.helloworld.data.playlist.PlaylistRepository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MusicService extends Service {

    public static final String ACTION_PLAY_SONG = "com.example.cn.helloworld.action.PLAY_SONG";
    public static final String ACTION_REQUEST_UPDATE =
            "com.example.cn.helloworld.action.REQUEST_UPDATE";
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
    private String currentPlaylistTitle = "";

    @Override
    public void onCreate() {
        super.onCreate();
        playlistRepository = PlaylistRepository.getInstance(this);
        ensureDefaultPlaylist();
        preparePlayer();

        musicReceiver = new MusicReceiver(this);
        musicReceiver.register(this);

        showNotification("音乐播放器", "准备播放");
        updateUI();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null) {
            if (ACTION_PLAY_SONG.equals(intent.getAction())) {
                handlePlaySongRequest(
                        intent.getStringExtra(EXTRA_PLAYLIST_ID),
                        intent.getStringExtra(EXTRA_SONG_ID)
                );
            } else if (ACTION_REQUEST_UPDATE.equals(intent.getAction())) {
                updateUI();
            }
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

    /** 发送 UI 更新广播（带封面路径 + 封面资源ID） */
    private void updateUI() {
        Song currentSong = getCurrentSong();

        Intent uiIntent = new Intent(ACTION_UPDATE_UI);
        uiIntent.putExtra("title",
                currentSong != null ? currentSong.getTitle() : getString(R.string.app_name));
        uiIntent.putExtra("artist",
                currentSong != null ? currentSong.getArtist() : "");
        uiIntent.putExtra("index", currentIndex);
        uiIntent.putExtra("total", currentSongs.size());
        uiIntent.putExtra("playing", player != null && player.isPlaying());
        uiIntent.putExtra("songId", currentSong != null ? currentSong.getId() : "");

        // 封面资源ID（用于没有自定义封面时）
        int coverResId = R.drawable.cover_playlist_placeholder;
        if (currentSong != null && currentSong.getCoverResId() != 0) {
            coverResId = currentSong.getCoverResId();
        }
        uiIntent.putExtra("coverResId", coverResId);

        // 自定义封面路径（由后台选择的图片）
        uiIntent.putExtra("coverPath",
                currentSong != null ? currentSong.getCoverImagePath() : null);

        sendBroadcast(uiIntent);
    }

    @Override
    public void onDestroy() {
        musicReceiver.unregister(this);
        releasePlayer();
        sendBroadcast(new Intent(ACTION_HIDE_FLOATING_MUSIC));
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void ensureDefaultPlaylist() {
        List<Playlist> all = playlistRepository.getAllPlaylists();
        if (!all.isEmpty()) {
            setCurrentPlaylist(all.get(0));
        }
    }

    private void setCurrentPlaylist(Playlist playlist) {
        currentPlaylist = playlist;
        currentSongs = playlist.getSongs() == null
                ? Collections.<Song>emptyList()
                : playlist.getSongs();
        currentPlaylistTitle = playlist.getTitle();
        currentIndex = 0;
    }

    private void setAllSongsPlaylist() {
        List<Playlist> all = playlistRepository.getAllPlaylists();
        List<Song> songs = new ArrayList<Song>();
        for (Playlist p : all) {
            if (p.getSongs() != null) {
                songs.addAll(p.getSongs());
            }
        }
        currentPlaylist = null;
        currentSongs = songs;
        currentPlaylistTitle = getString(R.string.playlist_all_songs);
        currentIndex = 0;
    }

    private void preparePlayer() {
        Song currentSong = getCurrentSong();
        if (currentSong == null) return;

        try {
            String localPath = currentSong.getLocalFilePath();

            // ① 本地文件（通过文件选择器选的 content:// 或 file://）
            if (!TextUtils.isEmpty(localPath)) {
                player = new MediaPlayer();
                player.setDataSource(this, Uri.parse(localPath));
                player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        nextMusic();
                    }
                });
                player.prepare();
                return;
            }

            // ② raw 资源
            if (currentSong.getAudioResId() != 0) {
                player = MediaPlayer.create(this, currentSong.getAudioResId());
                if (player != null) {
                    player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                        @Override
                        public void onCompletion(MediaPlayer mp) {
                            nextMusic();
                        }
                    });
                }
                return;
            }

            // ③ 网络 URL
            if (!TextUtils.isEmpty(currentSong.getStreamUrl())) {
                player = new MediaPlayer();
                player.setDataSource(currentSong.getStreamUrl());
                player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        nextMusic();
                    }
                });
                player.prepare();
                return;
            }

            Toast.makeText(this, "无法播放：音频文件不存在", Toast.LENGTH_SHORT).show();

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "音乐加载失败", Toast.LENGTH_SHORT).show();
        }
    }

    private void releasePlayer() {
        if (player != null) {
            try {
                player.stop();
            } catch (Exception ignored) {}
            player.release();
            player = null;
        }
    }

    private Song getCurrentSong() {
        if (currentSongs.isEmpty()) return null;
        if (currentIndex < 0 || currentIndex >= currentSongs.size()) {
            currentIndex = 0;
        }
        return currentSongs.get(currentIndex);
    }

    private void handlePlaySongRequest(String playlistId, String songId) {
        if (!TextUtils.isEmpty(playlistId)) {
            Playlist p = playlistRepository.getById(playlistId);
            if (p != null) {
                setCurrentPlaylist(p);
            }
        } else {
            setAllSongsPlaylist();
        }

        if (!TextUtils.isEmpty(songId)) {
            int index = findSongIndex(songId);
            if (index != -1) {
                currentIndex = index;
            }
        }

        switchMusic();
    }

    private int findSongIndex(String songId) {
        for (int i = 0; i < currentSongs.size(); i++) {
            Song s = currentSongs.get(i);
            if (songId.equals(s.getId())) {
                return i;
            }
        }
        return -1;
    }

    private String getCurrentSongTitle() {
        Song s = getCurrentSong();
        return s != null ? s.getTitle() : getString(R.string.app_name);
    }
}
