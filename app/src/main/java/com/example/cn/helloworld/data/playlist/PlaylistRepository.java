package com.example.cn.helloworld.data.playlist;

import com.example.cn.helloworld.R;
import com.example.cn.helloworld.data.model.Playlist;
import com.example.cn.helloworld.data.model.Song;
import com.example.cn.helloworld.ui.admin.PlaylistEditorActivity;
import com.example.cn.helloworld.ui.admin.PlaylistManagementActivity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public final class PlaylistRepository {

    private static final PlaylistRepository INSTANCE = new PlaylistRepository(this);

    private final List<Playlist> playlists;

    public PlaylistRepository(PlaylistManagementActivity playlistEditorActivity) {
        playlists = buildPlaylists();
    }

    public static PlaylistRepository getInstance() {
        return INSTANCE;
    }

    public List<Playlist> getAllPlaylists() {
        return new ArrayList<>(playlists);
    }

    public Playlist findById(String playlistId) {
        for (Playlist p : playlists) {
            if (p.getId().equals(playlistId)) {
                return p;
            }
        }
        return null;
    }

    private List<Playlist> buildPlaylists() {
        List<Playlist> list = new ArrayList<>();
        list.add(createStageSpotlight());
        list.add(createHealingVoice());
        return list;
    }

    private Playlist createStageSpotlight() {

        List<String> tags = Arrays.asList("舞台", "燃", "LIVE");

        List<Song> songs = Arrays.asList(
                new Song("song-fenwuhai", "粉雾海", "易烊千玺", "《奇迹笨小孩》推广曲舞台版",
                        215000L, R.raw.yyqx_baobei, R.drawable.cover_baobei),
                new Song("song-lisao", "离骚", "易烊千玺", "古风舞台 remix",
                        210000L, R.raw.yyqx_lisao, R.drawable.cover_lisao),
                new Song("song-nishuo", "你说", "易烊千玺", "治愈系柔声现场",
                        204000L, R.raw.yyqx_nishuo, R.drawable.cover_nishuo)
        );

        return new Playlist(
                "playlist-stage", "舞台聚光", "代表性舞台合集",
                null, null,
                R.drawable.cover_playlist_stage,
                tags, songs, 856000L, 128000L
        );
    }

    private Playlist createHealingVoice() {

        List<String> tags = Arrays.asList("治愈", "抒情");

        List<Song> songs = Arrays.asList(
                new Song("song-myfriend", "我的朋友", "易烊千玺", "温柔的陪伴",
                        242000L, R.raw.yyqx_baobei, R.drawable.cover_baobei),
                new Song("song-ourtime", "我们的时光", "TFBOYS", "青春记忆",
                        233000L, R.raw.yyqx_lisao, R.drawable.cover_lisao)
        );

        return new Playlist(
                "playlist-healing", "轻声慢语", "温柔歌声治愈心情",
                null, null,
                R.drawable.cover_playlist_healing,
                tags, songs, 612000L, 98000L
        );
    }

    public List<Playlist> getHomeSummaries() {
        return new ArrayList<>(playlists);
    }
}
