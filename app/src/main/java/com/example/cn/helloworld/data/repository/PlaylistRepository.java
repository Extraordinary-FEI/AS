package com.example.cn.helloworld.data.repository;

import android.content.Context;

import com.example.cn.helloworld.data.model.Playlist;
import com.example.cn.helloworld.data.model.Song;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 统一管理歌单（Playlist）的仓库
 * 提供增删改查、替换歌曲列表等功能
 * 完全兼容 PlaylistEditorActivity / PlaylistManagementActivity
 */
public class PlaylistRepository {

    /** 内存缓存所有歌单 */
    private static final Map<String, Playlist> playlistStore = new HashMap<String, Playlist>();

    public PlaylistRepository(Context context) {
        // 如果需要默认歌单，可在这里初始化
        // 现在先留空，由 PlaylistManagementActivity 创建
    }

    /** 获取全部歌单 */
    public List<Playlist> getAll() {
        return new ArrayList<Playlist>(playlistStore.values());
    }

    /** 根据 ID 获取歌单 */
    public Playlist getById(String playlistId) {
        return playlistStore.get(playlistId);
    }

    /** 保存新歌单（用于创建、第一次写入） */
    public void savePlaylist(Playlist playlist) {
        if (playlist != null) {
            playlistStore.put(playlist.getId(), playlist);
        }
    }

    /** 管理员更新歌单详情（用 copyWith，而不是 setXXX） */
    public void updatePlaylistDetails(
            String playlistId,
            String title,
            String description,
            String playUrl,
            String coverUrl,
            Integer coverResId,
            List<String> tags
    ) {
        Playlist playlist = playlistStore.get(playlistId);
        if (playlist != null) {
            Playlist updated = playlist.copyWith(
                    title,
                    description,
                    playUrl,
                    coverUrl,
                    coverResId,
                    tags,
                    null,                       // 歌曲列表不变
                    playlist.getPlayCount(),
                    playlist.getFavoriteCount()
            );
            playlistStore.put(playlistId, updated);
        }
    }

    /** 替换整个歌单的歌曲列表 */
    public void replaceSongs(String playlistId, List<Song> newList) {
        Playlist playlist = playlistStore.get(playlistId);
        if (playlist != null) {
            Playlist updated = playlist.copyWith(
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    newList,
                    playlist.getPlayCount(),
                    playlist.getFavoriteCount()
            );
            playlistStore.put(playlistId, updated);
        }
    }

    /** 生成歌曲 ID（给 PlaylistEditorActivity 用） */
    public String generateSongId(String playlistId) {
        Playlist p = playlistStore.get(playlistId);
        int count = (p == null || p.getSongs() == null) ? 0 : p.getSongs().size();
        return playlistId + "_song_" + (count + 1);
    }
}
