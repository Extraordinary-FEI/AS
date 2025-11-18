package com.example.cn.helloworld.data.playlist;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import com.example.cn.helloworld.R;
import com.example.cn.helloworld.data.model.Playlist;
import com.example.cn.helloworld.data.model.Song;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 本地歌单仓库：使用本地 mp3 + 本地封面图片
 */
public final class PlaylistRepository {

    private static final String PREFS_NAME = "playlist_repo";
    private static final String KEY_PLAYLISTS = "playlists_json";

    private static PlaylistRepository INSTANCE;

    private final SharedPreferences preferences;
    private final List<Playlist> playlists = new ArrayList<Playlist>();

    private PlaylistRepository(Context context) {
        Context appContext = context.getApplicationContext();
        preferences = appContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        loadFromStorage();
        if (playlists.isEmpty()) {
            playlists.addAll(buildPlaylists());
            persist();
        }
    }

    public static synchronized PlaylistRepository getInstance(Context context) {
        if (INSTANCE == null) {
            INSTANCE = new PlaylistRepository(context);
        }
        return INSTANCE;
    }

    /** 获取全部歌单列表（给主页、后台管理用） */
    public List<Playlist> getAllPlaylists() {
        return new ArrayList<Playlist>(playlists);
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
        persist();
    }

    /** 编辑歌单时，更新已有歌单（如果后面 PlaylistEditorActivity 需要的话可以用这个） */
    public void updatePlaylist(Playlist updated) {
        if (updated == null) return;
        for (int i = 0; i < playlists.size(); i++) {
            if (playlists.get(i).getId().equals(updated.getId())) {
                playlists.set(i, updated);
                persist();
                return;
            }
        }
    }

    // ===================== 构建初始本地数据 =====================

    private List<Playlist> buildPlaylists() {
        List<Playlist> list = new ArrayList<Playlist>();
        list.add(createStageSpotlight());
        list.add(createHealingVoice());
        return list;
    }

    private void loadFromStorage() {
        playlists.clear();
        String json = preferences.getString(KEY_PLAYLISTS, null);
        if (TextUtils.isEmpty(json)) {
            return;
        }
        try {
            JSONArray array = new JSONArray(json);
            for (int i = 0; i < array.length(); i++) {
                Playlist playlist = fromJson(array.getJSONObject(i));
                if (playlist != null) {
                    playlists.add(playlist);
                }
            }
        } catch (JSONException ignored) {
            playlists.clear();
        }
    }

    private void persist() {
        JSONArray array = new JSONArray();
        for (Playlist playlist : playlists) {
            try {
                array.put(toJson(playlist));
            } catch (JSONException ignored) {
            }
        }
        preferences.edit().putString(KEY_PLAYLISTS, array.toString()).apply();
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

    private Playlist fromJson(JSONObject object) {
        if (object == null) {
            return null;
        }
        String id = object.optString("id");
        String title = object.optString("title");
        String description = object.optString("description");
        String playUrl = object.optString("playUrl");
        String coverUrl = object.optString("coverUrl");
        Integer coverResId = object.has("coverResId") ? object.optInt("coverResId") : null;
        List<String> tags = jsonArrayToList(object.optJSONArray("tags"));
        List<Song> songs = songsFromJson(object.optJSONArray("songs"));
        long playCount = object.optLong("playCount", 0);
        long favoriteCount = object.optLong("favoriteCount", 0);
        return new Playlist(id, title, description, playUrl, coverUrl,
                coverResId, tags, songs, playCount, favoriteCount);
    }

    private JSONObject toJson(Playlist playlist) throws JSONException {
        JSONObject object = new JSONObject();
        object.put("id", playlist.getId());
        object.put("title", playlist.getTitle());
        object.put("description", playlist.getDescription());
        if (!TextUtils.isEmpty(playlist.getPlayUrl())) {
            object.put("playUrl", playlist.getPlayUrl());
        }
        if (!TextUtils.isEmpty(playlist.getCoverUrl())) {
            object.put("coverUrl", playlist.getCoverUrl());
        }
        if (playlist.getCoverResId() != null) {
            object.put("coverResId", playlist.getCoverResId());
        }
        object.put("tags", new JSONArray(playlist.getTags()));
        object.put("songs", songsToJson(playlist.getSongs()));
        object.put("playCount", playlist.getPlayCount());
        object.put("favoriteCount", playlist.getFavoriteCount());
        return object;
    }

    private JSONArray songsToJson(List<Song> songs) throws JSONException {
        JSONArray array = new JSONArray();
        for (Song song : songs) {
            JSONObject object = new JSONObject();
            object.put("id", song.getId());
            object.put("title", song.getTitle());
            object.put("artist", song.getArtist());
            object.put("description", song.getDescription());
            object.put("durationMs", song.getDurationMs());
            object.put("audioResId", song.getAudioResId());
            object.put("coverResId", song.getCoverResId());
            if (!TextUtils.isEmpty(song.getStreamUrl())) {
                object.put("streamUrl", song.getStreamUrl());
            }
            if (!TextUtils.isEmpty(song.getCoverUrl())) {
                object.put("coverUrl", song.getCoverUrl());
            }
            array.put(object);
        }
        return array;
    }

    private List<Song> songsFromJson(JSONArray array) {
        List<Song> list = new ArrayList<Song>();
        if (array == null) {
            return list;
        }
        for (int i = 0; i < array.length(); i++) {
            JSONObject object = array.optJSONObject(i);
            if (object == null) continue;
            String id = object.optString("id");
            String title = object.optString("title");
            String artist = object.optString("artist");
            String description = object.optString("description");
            long duration = object.optLong("durationMs", 0);
            int audioResId = object.optInt("audioResId", 0);
            int coverResId = object.optInt("coverResId", 0);
            String streamUrl = object.optString("streamUrl", null);
            String coverUrl = object.optString("coverUrl", null);

            Song song;
            if (!TextUtils.isEmpty(streamUrl) || !TextUtils.isEmpty(coverUrl)) {
                song = new Song(id, title, artist, description, duration,
                        streamUrl, coverUrl, coverResId > 0 ? coverResId : null);
            } else {
                song = new Song(id, title, artist, description, duration,
                        audioResId, coverResId);
            }
            list.add(song);
        }
        return list;
    }

    private List<String> jsonArrayToList(JSONArray array) {
        List<String> list = new ArrayList<String>();
        if (array == null) return list;
        for (int i = 0; i < array.length(); i++) {
            list.add(array.optString(i));
        }
        return list;
    }
}
