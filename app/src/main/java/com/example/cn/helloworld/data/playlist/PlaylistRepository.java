package com.example.cn.helloworld.data.playlist;

import com.example.cn.helloworld.R;
import com.example.cn.helloworld.data.model.Playlist;
import com.example.cn.helloworld.data.model.Song;
import com.example.cn.helloworld.ui.main.HomeModels;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 静态歌单仓库，后续可替换为网络或本地数据库实现。
 */
public final class PlaylistRepository {

    private static final PlaylistRepository INSTANCE = new PlaylistRepository();

    private final List<Playlist> playlists;
    private final List<HomeModels.Playlist> homeSummaries;

    private PlaylistRepository() {
        playlists = buildPlaylists();
        homeSummaries = buildHomeSummaries(playlists);
    }

    public static PlaylistRepository getInstance() {
        return INSTANCE;
    }

    public List<HomeModels.Playlist> getHomeSummaries() {
        return new ArrayList<HomeModels.Playlist>(homeSummaries);
    }

    public List<Playlist> getAllPlaylists() {
        return new ArrayList<Playlist>(playlists);
    }

    public Playlist findById(String playlistId) {
        if (playlistId == null) {
            return null;
        }
        for (int i = 0; i < playlists.size(); i++) {
            Playlist playlist = playlists.get(i);
            if (playlistId.equals(playlist.getId())) {
                return playlist;
            }
        }
        return null;
    }

    private List<Playlist> buildPlaylists() {
        List<Playlist> data = new ArrayList<Playlist>();
        data.add(createStageSpotlight());
        data.add(createHealingVoice());
        data.add(createGrowthDiary());
        data.add(createCityWalk());
        return data;
    }

    private List<HomeModels.Playlist> buildHomeSummaries(List<Playlist> playlistSource) {
        int[] colorPalette = new int[] {
                R.color.homePlaylistColor1,
                R.color.homePlaylistColor2,
                R.color.homePlaylistColor3,
                R.color.homePlaylistColor4
        };
        List<HomeModels.Playlist> result = new ArrayList<HomeModels.Playlist>();
        for (int i = 0; i < playlistSource.size(); i++) {
            Playlist playlist = playlistSource.get(i);
            int colorRes = colorPalette[i % colorPalette.length];
            result.add(new HomeModels.Playlist(
                    playlist.getId(),
                    playlist.getTitle(),
                    playlist.getDescription(),
                    colorRes,
                    playlist.getTags(),
                    playlist.getPlayCount(),
                    playlist.getFavoriteCount(),
                    playlist.getSongs().size()
            ));
        }
        return result;
    }

    private Playlist createStageSpotlight() {
        List<String> tags = Arrays.asList("舞台", "热血", "LIVE");
        List<Song> songs = Arrays.asList(
                new Song(
                        "song-fenwuhai",
                        "粉雾海",
                        "易烊千玺",
                        215000L,
                        "https://example.com/audio/fenwuhai.mp3",
                        "电影《奇迹·笨小孩》推广曲，现场版开场即燃。",
                        null,
                        Integer.valueOf(R.drawable.cover_playlist_stage)
                ),
                new Song(
                        "song-unpredictable",
                        "Unpredictable",
                        "易烊千玺",
                        210000L,
                        "https://example.com/audio/unpredictable.mp3",
                        "英文单曲现场混音，展示舞台掌控力。",
                        null,
                        Integer.valueOf(R.drawable.cover_playlist_stage)
                ),
                new Song(
                        "song-youth-guide",
                        "青春修炼手册",
                        "TFBOYS",
                        204000L,
                        "https://example.com/audio/qingxiuxl.mp3",
                        "周年演唱会版本，伴随应援海洋整齐挥舞。",
                        null,
                        Integer.valueOf(R.drawable.cover_playlist_stage)
                ),
                new Song(
                        "song-dreamer",
                        "大梦想家",
                        "TFBOYS",
                        208000L,
                        "https://example.com/audio/damengxiangjia.mp3",
                        "青春洋溢的编舞，回顾少年时代的舞台记忆。",
                        null,
                        Integer.valueOf(R.drawable.cover_playlist_stage)
                )
        );
        return new Playlist(
                "playlist-stage-spotlight",
                "舞台聚光",
                "收录千玺代表性舞台，灯光升起的那一刻再次心跳。",
                "https://example.com/playlist/stage-spotlight",
                null,
                Integer.valueOf(R.drawable.cover_playlist_stage),
                tags,
                songs,
                856000L,
                128000L
        );
    }

    private Playlist createHealingVoice() {
        List<String> tags = Arrays.asList("抒情", "治愈", "慢速");
        List<Song> songs = Arrays.asList(
                new Song(
                        "song-myfriend",
                        "我的朋友",
                        "易烊千玺",
                        242000L,
                        "https://example.com/audio/my_friend.mp3",
                        "电影《少年的你》推广曲，温柔诉说陪伴。",
                        null,
                        Integer.valueOf(R.drawable.cover_playlist_healing)
                ),
                new Song(
                        "song-our-times",
                        "我们的时光",
                        "TFBOYS",
                        236000L,
                        "https://example.com/audio/our_times.mp3",
                        "原声录音版，细腻合声守护成长约定。",
                        null,
                        Integer.valueOf(R.drawable.cover_playlist_healing)
                ),
                new Song(
                        "song-pet",
                        "宠爱",
                        "TFBOYS",
                        233000L,
                        "https://example.com/audio/chongai.mp3",
                        "轻快节奏背后藏着少年嗓音的柔软和牵挂。",
                        null,
                        Integer.valueOf(R.drawable.cover_playlist_healing)
                ),
                new Song(
                        "song-dream-diary",
                        "大梦想家 (原声)",
                        "TFBOYS",
                        208000L,
                        "https://example.com/audio/dream_diary.mp3",
                        "收束成轻声合唱的尾奏，把梦想悄悄写进日记。",
                        null,
                        Integer.valueOf(R.drawable.cover_playlist_healing)
                )
        );
        return new Playlist(
                "playlist-healing-voice",
                "轻声慢语",
                "把耳机交给千玺的温柔歌声，做一次深呼吸。",
                "https://example.com/playlist/healing-voice",
                null,
                Integer.valueOf(R.drawable.cover_playlist_healing),
                tags,
                songs,
                612000L,
                98000L
        );
    }

    private Playlist createGrowthDiary() {
        List<String> tags = Arrays.asList("成长", "纪念", "青春");
        List<Song> songs = Arrays.asList(
                new Song(
                        "song-dreamer-anniversary",
                        "大梦想家 (纪念版)",
                        "TFBOYS",
                        208000L,
                        "https://example.com/audio/dreamer_anniversary.mp3",
                        "周年纪念现场，记录少年到青年的蜕变。",
                        null,
                        Integer.valueOf(R.drawable.cover_playlist_growth)
                ),
                new Song(
                        "song-our-times-live",
                        "我们的时光 (Live)",
                        "TFBOYS",
                        236000L,
                        "https://example.com/audio/our_times_live.mp3",
                        "与灯海合唱的那一晚，是陪伴最真切的回应。",
                        null,
                        Integer.valueOf(R.drawable.cover_playlist_growth)
                ),
                new Song(
                        "song-myfriend-ost",
                        "我的朋友 (原声)",
                        "易烊千玺",
                        242000L,
                        "https://example.com/audio/my_friend_ost.mp3",
                        "镜头切换至陈念与小北，相伴的勇气仍在。",
                        null,
                        Integer.valueOf(R.drawable.cover_playlist_growth)
                ),
                new Song(
                        "song-youth-guide-rework",
                        "青春修炼手册 (青春版)",
                        "TFBOYS",
                        204000L,
                        "https://example.com/audio/youth_guide.mp3",
                        "重新回顾练习生笔记，向更好的自己进发。",
                        null,
                        Integer.valueOf(R.drawable.cover_playlist_growth)
                )
        );
        return new Playlist(
                "playlist-growth-diary",
                "成长日记",
                "翻开千玺的成长记忆，拾起一路同行的约定。",
                "https://example.com/playlist/growth-diary",
                null,
                Integer.valueOf(R.drawable.cover_playlist_growth),
                tags,
                songs,
                704000L,
                112000L
        );
    }

    private Playlist createCityWalk() {
        List<String> tags = Arrays.asList("街舞", "律动", "夜色");
        List<Song> songs = Arrays.asList(
                new Song(
                        "song-unpredictable-mix",
                        "Unpredictable (Street Mix)",
                        "易烊千玺",
                        210000L,
                        "https://example.com/audio/unpredictable_mix.mp3",
                        "综艺街舞舞台版本，节奏踩点干净利落。",
                        null,
                        Integer.valueOf(R.drawable.cover_playlist_city)
                ),
                new Song(
                        "song-fenwuhai-live",
                        "粉雾海 (Live)",
                        "易烊千玺",
                        215000L,
                        "https://example.com/audio/fenwuhai_live.mp3",
                        "用舞蹈重绘电影镜头，雾气中依旧坚定目光。",
                        null,
                        Integer.valueOf(R.drawable.cover_playlist_city)
                ),
                new Song(
                        "song-our-times-city",
                        "我们的时光 (City ver.)",
                        "TFBOYS",
                        236000L,
                        "https://example.com/audio/our_times_city.mp3",
                        "霓虹街景里轻唱，守护约定的每一步。",
                        null,
                        Integer.valueOf(R.drawable.cover_playlist_city)
                ),
                new Song(
                        "song-pet-remix",
                        "宠爱 (Remix)",
                        "TFBOYS",
                        233000L,
                        "https://example.com/audio/pet_remix.mp3",
                        "DJ 版节奏更贴合夜骑心率，耳机里的活力补给。",
                        null,
                        Integer.valueOf(R.drawable.cover_playlist_city)
                )
        );
        return new Playlist(
                "playlist-city-walk",
                "夜行 City Walk",
                "边走边听千玺街舞 BGM，在城市夜色里持续发光。",
                "https://example.com/playlist/city-walk",
                null,
                Integer.valueOf(R.drawable.cover_playlist_city),
                tags,
                songs,
                542000L,
                76000L
        );
    }
}
