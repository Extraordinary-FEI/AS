package com.example.cn.helloworld.data.repository;

import android.content.Context;

import com.example.cn.helloworld.R;
import com.example.cn.helloworld.data.model.Playlist;
import com.example.cn.helloworld.data.model.Song;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

public class PlaylistRepository {

    private static final Map<String, Playlist> playlists = new LinkedHashMap<>();
    private static boolean initialized = false;

    public PlaylistRepository(Context context) {
        if (!initialized) {
            seed(context.getApplicationContext());
            initialized = true;
        }
    }

    private void seed(Context context) {
        List<Song> healingSongs = new ArrayList<>();
        healingSongs.add(new Song(
                "song-nishuo",
                "你说",
                "林俊杰",
                214000L,
                "https://example.com/audio/ni_shuo.mp3",
                "温柔治愈系，诉说心底的故事",
                null,
                Integer.valueOf(R.drawable.cover_nishuo)));
        healingSongs.add(new Song(
                "song-baobei",
                "宝贝",
                "张悬",
                198000L,
                "https://example.com/audio/bao_bei.mp3",
                "轻快民谣，伴你醒来迎接阳光",
                null,
                Integer.valueOf(R.drawable.cover_baobei)));

        List<String> healingTags = new ArrayList<>();
        healingTags.add("华语");
        healingTags.add("治愈");
        healingTags.add("安静");

        Playlist healing = new Playlist(
                "playlist-classic",
                "轻听华语 · 治愈精选",
                "精选 2000 年后治愈系华语歌曲，适合午后阅读或夜晚放松聆听。",
                "https://example.com/playlist/classic-heal",
                null,
                Integer.valueOf(R.drawable.cover_lisao),
                healingTags,
                healingSongs);

        playlists.put(healing.getId(), healing);

        List<Song> stageSongs = new ArrayList<>();
        stageSongs.add(new Song(
                "song-stage-1",
                "Lights On",
                "TFBOYS",
                195000L,
                "https://example.com/audio/lights_on.mp3",
                "舞台热力全开，一起挥舞应援棒",
                null,
                Integer.valueOf(R.drawable.song_cover)));

        Playlist stage = new Playlist(
                "playlist-stage",
                "舞台热力",
                "精选巡演现场，让肾上腺素飙升",
                "https://example.com/playlist/stage",
                null,
                Integer.valueOf(R.drawable.song_cover),
                new ArrayList<String>(),
                stageSongs);

        playlists.put(stage.getId(), stage);
    }

    public List<Playlist> getAll() {
        return new ArrayList<>(playlists.values());
    }

    public Playlist getById(String playlistId) {
        return playlists.get(playlistId);
    }

    public void savePlaylist(Playlist playlist) {
        if (playlist == null) {
            return;
        }
        playlists.put(playlist.getId(), playlist);
    }

    public void updatePlaylistDetails(String playlistId,
                                      String title,
                                      String description,
                                      String playUrl,
                                      String coverUrl,
                                      Integer coverResId,
                                      List<String> tags) {
        Playlist current = playlists.get(playlistId);
        if (current == null) {
            return;
        }
        Playlist updated = current.copyWith(title, description, playUrl, coverUrl, coverResId, tags, current.getSongs());
        playlists.put(playlistId, updated);
    }

    public void replaceSongs(String playlistId, List<Song> songs) {
        Playlist current = playlists.get(playlistId);
        if (current == null) {
            return;
        }
        Playlist updated = current.copyWith(current.getTitle(),
                current.getDescription(),
                current.getPlayUrl(),
                current.getCoverUrl(),
                current.getCoverResId(),
                current.getTags(),
                songs);
        playlists.put(playlistId, updated);
    }

    public void addSong(String playlistId, Song song) {
        if (song == null) {
            return;
        }
        Playlist current = playlists.get(playlistId);
        if (current == null) {
            return;
        }
        List<Song> songs = current.getSongs();
        songs.add(song);
        replaceSongs(playlistId, songs);
    }

    public void updateSong(String playlistId, Song song) {
        if (song == null) {
            return;
        }
        Playlist current = playlists.get(playlistId);
        if (current == null) {
            return;
        }
        List<Song> songs = current.getSongs();
        for (int i = 0; i < songs.size(); i++) {
            if (songs.get(i).getId().equals(song.getId())) {
                songs.set(i, song);
                break;
            }
        }
        replaceSongs(playlistId, songs);
    }

    public void removeSong(String playlistId, String songId) {
        Playlist current = playlists.get(playlistId);
        if (current == null) {
            return;
        }
        List<Song> songs = current.getSongs();
        for (int i = songs.size() - 1; i >= 0; i--) {
            if (songs.get(i).getId().equals(songId)) {
                songs.remove(i);
            }
        }
        replaceSongs(playlistId, songs);
    }

    public String generatePlaylistId() {
        String candidate = "playlist-" + UUID.randomUUID().toString().substring(0, 8).toLowerCase(Locale.US);
        while (playlists.containsKey(candidate)) {
            candidate = "playlist-" + UUID.randomUUID().toString().substring(0, 8).toLowerCase(Locale.US);
        }
        return candidate;
    }

    public String generateSongId(String playlistId) {
        String base = playlistId == null ? "song" : playlistId + "-song";
        String candidate = base + "-" + UUID.randomUUID().toString().substring(0, 6).toLowerCase(Locale.US);
        Playlist playlist = playlists.get(playlistId);
        if (playlist == null) {
            return candidate;
        }
        List<Song> songs = playlist.getSongs();
        boolean exists;
        do {
            exists = false;
            for (Song song : songs) {
                if (song.getId().equals(candidate)) {
                    exists = true;
                    candidate = base + "-" + UUID.randomUUID().toString().substring(0, 6).toLowerCase(Locale.US);
                    break;
                }
            }
        } while (exists);
        return candidate;
    }
}
