package com.example.cn.helloworld.data.playlist;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import com.example.cn.helloworld.R;
import com.example.cn.helloworld.data.model.Playlist;
import com.example.cn.helloworld.data.model.Song;
import com.example.cn.helloworld.data.storage.AdminLocalStore;

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

    private static final PlaylistRepository INSTANCE = new PlaylistRepository();
    private static final String KEY_PLAYLISTS = "admin_playlists";

    private final List<Playlist> playlists = new ArrayList<>();
    private SharedPreferences preferences;
    private boolean initialized;

    private PlaylistRepository() {
    }

    public static PlaylistRepository getInstance(Context context) {
        if (context == null) {
            throw new IllegalArgumentException("context == null");
        }
        INSTANCE.ensureInitialized(context.getApplicationContext());
        return INSTANCE;
    }

    public static PlaylistRepository getInstance() {
        if (!INSTANCE.initialized) {
            throw new IllegalStateException("Call getInstance(Context) first");
        }
        return INSTANCE;
    }

    public List<Playlist> getAllPlaylists() {
        return new ArrayList<>(playlists);
    }

    public List<Playlist> getHomeSummaries() {
        return getAllPlaylists();
    }

    public Playlist getById(String playlistId) {
        if (playlistId == null) return null;
        for (Playlist playlist : playlists) {
            if (playlistId.equals(playlist.getId())) {
                return playlist;
            }
        }
        return null;
    }

    public void addPlaylist(Playlist playlist) {
        if (playlist == null) return;
        playlists.add(playlist);
        persist();
    }

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

    private synchronized void ensureInitialized(Context context) {
        if (initialized) {
            return;
        }
        AdminLocalStore.init(context);
        preferences = AdminLocalStore.get(context);
        loadFromStorage();
        initialized = true;
    }

    private void loadFromStorage() {
        if (preferences == null) {
            playlists.clear();
            playlists.addAll(buildPlaylists());
            return;
        }
        String json = preferences.getString(KEY_PLAYLISTS, null);
        if (TextUtils.isEmpty(json)) {
            playlists.clear();
            playlists.addAll(buildPlaylists());
            persist();
            return;
        }
        try {
            JSONArray array = new JSONArray(json);
            playlists.clear();
            for (int i = 0; i < array.length(); i++) {
                playlists.add(fromJson(array.getJSONObject(i)));
            }
        } catch (JSONException e) {
            playlists.clear();
            playlists.addAll(buildPlaylists());
            persist();
        }
    }

    private void persist() {
        if (preferences == null) {
            return;
        }
        JSONArray array = new JSONArray();
        for (Playlist playlist : playlists) {
            array.put(toJson(playlist));
        }
        preferences.edit().putString(KEY_PLAYLISTS, array.toString()).commit();
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

    private Playlist fromJson(JSONObject object) throws JSONException {
        List<String> tags = jsonArrayToList(object.optJSONArray("tags"));
        List<Song> songs = jsonArrayToSongs(object.optJSONArray("songs"));
        Integer coverResId = object.has("coverResId") && !object.isNull("coverResId")
                ? object.optInt("coverResId") : null;
        return new Playlist(
                object.getString("id"),
                object.optString("title"),
                object.optString("description"),
                object.optString("playUrl", null),
                object.optString("coverUrl", null),
                coverResId,
                tags,
                songs,
                object.optLong("playCount"),
                object.optLong("favoriteCount")
        );
    }

    private JSONObject toJson(Playlist playlist) {
        JSONObject object = new JSONObject();
        try {
            object.put("id", playlist.getId());
            object.put("title", playlist.getTitle());
            object.put("description", playlist.getDescription());
            object.put("playUrl", playlist.getPlayUrl());
            object.put("coverUrl", playlist.getCoverUrl());
            object.put("coverResId", playlist.getCoverResId());
            object.put("tags", new JSONArray(playlist.getTags()));
            object.put("songs", songsToJsonArray(playlist.getSongs()));
            object.put("playCount", playlist.getPlayCount());
            object.put("favoriteCount", playlist.getFavoriteCount());
        } catch (JSONException ignored) {
        }
        return object;
    }

    private JSONArray songsToJsonArray(List<Song> songs) {
        JSONArray array = new JSONArray();
        if (songs == null) {
            return array;
        }
        for (Song song : songs) {
            array.put(songToJson(song));
        }
        return array;
    }

    private JSONObject songToJson(Song song) {
        JSONObject object = new JSONObject();
        try {
            object.put("id", song.getId());
            object.put("title", song.getTitle());
            object.put("artist", song.getArtist());
            object.put("description", song.getDescription());
            object.put("durationMs", song.getDurationMs());
            object.put("audioResId", song.getAudioResId());
            object.put("coverResId", song.getCoverResId());
            object.put("streamUrl", song.getStreamUrl());
            object.put("coverUrl", song.getCoverUrl());
        } catch (JSONException ignored) {
        }
        return object;
    }

    private List<Song> jsonArrayToSongs(JSONArray array) throws JSONException {
        List<Song> songs = new ArrayList<>();
        if (array == null) {
            return songs;
        }
        for (int i = 0; i < array.length(); i++) {
            songs.add(songFromJson(array.getJSONObject(i)));
        }
        return songs;
    }

    private Song songFromJson(JSONObject object) {
        long duration = object.optLong("durationMs");
        int audioResId = object.optInt("audioResId");
        int coverResId = object.optInt("coverResId");
        String streamUrl = object.optString("streamUrl", null);
        String coverUrl = object.optString("coverUrl", null);
        String id = object.optString("id");
        String title = object.optString("title");
        String artist = object.optString("artist");
        String description = object.optString("description");
        if (audioResId > 0) {
            return new Song(id, title, artist, description, duration, audioResId, coverResId);
        }
        return new Song(id, title, artist, description, duration, streamUrl, coverUrl, coverResId);
    }

    private List<String> jsonArrayToList(JSONArray array) {
        List<String> list = new ArrayList<>();
        if (array == null) {
            return list;
        }
        for (int i = 0; i < array.length(); i++) {
            list.add(array.optString(i));
        }
        return list;
    }
}
