package com.example.cn.helloworld.data.playlist;

import com.example.cn.helloworld.R;
import com.example.cn.helloworld.data.model.Playlist;
import com.example.cn.helloworld.data.model.Song;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 本地歌单仓库：使用本地 mp3 + 本地封面图片
 */
public final class PlaylistRepository {

    // 单例
    private static final PlaylistRepository INSTANCE = new PlaylistRepository();

    // 内存中的歌单列表
    private final List<Playlist> playlists;

    /** 私有构造：外部不能 new，只能通过 getInstance() 拿 */
    private PlaylistRepository() {
        playlists = buildPlaylists();
    }

    /** 对外获取单例 */
    public static PlaylistRepository getInstance() {
        return INSTANCE;
    }

    // ===================== 对外提供的操作 =====================

    /** 获取全部歌单列表（给主页、后台管理用） */
    public List<Playlist> getAllPlaylists() {
        return new ArrayList<>(playlists);
    }

    /** 后台管理 / 主页都可以用这个名字 */
    public List<Playlist> getHomeSummaries() {
        return getAllPlaylists();
    }

    /** 根据 id 查歌单（给 PlaylistFragment / 详情页用） */
    public Playlist getById(String playlistId) {
        if (playlistId == null) return null;
        for (Playlist p : playlists) {
            if (playlistId.equals(p.getId())) {
                return p;
            }
        }
        return null;
    }

    /** 新增一个歌单（给 PlaylistManagementActivity.createNewPlaylist 用） */
    public void addPlaylist(Playlist playlist) {
        if (playlist == null) return;
        playlists.add(playlist);
    }

    /** 编辑歌单时，更新已有歌单（如果后面 PlaylistEditorActivity 需要的话可以用这个） */
    public void updatePlaylist(Playlist updated) {
        if (updated == null) return;
        for (int i = 0; i < playlists.size(); i++) {
            if (playlists.get(i).getId().equals(updated.getId())) {
                playlists.set(i, updated);
                return;
            }
        }
    }

    // ===================== 构建初始本地数据 =====================

    private List<Playlist> buildPlaylists() {
        List<Playlist> list = new ArrayList<>();
        list.add(createStageSpotlight());
        list.add(createHealingVoice());
        return list;
    }

    /** 歌单 1：舞台聚光 */
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
                "playlist-stage",
                "舞台聚光",
                "代表性舞台合集",
                null,
                null,
                R.drawable.cover_playlist_stage,
                tags,
                songs,
                856000L,
                128000L
        );
    }

    /** 歌单 2：轻声慢语 */
    private Playlist createHealingVoice() {

        List<String> tags = Arrays.asList("治愈", "抒情");

        List<Song> songs = Arrays.asList(
                new Song("song-myfriend", "我的朋友", "易烊千玺", "温柔的陪伴",
                        242000L, R.raw.yyqx_baobei, R.drawable.cover_baobei),
                new Song("song-ourtime", "我们的时光", "TFBOYS", "青春记忆",
                        233000L, R.raw.yyqx_lisao, R.drawable.cover_lisao)
        );

        return new Playlist(
                "playlist-healing",
                "轻声慢语",
                "温柔歌声治愈心情",
                null,
                null,
                R.drawable.cover_playlist_healing,
                tags,
                songs,
                612000L,
                98000L
        );
    }
}
