package com.example.cn.helloworld.data.model;

import android.support.annotation.DrawableRes;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Playlist implements Serializable {

    private final String id;
    private final String title;
    private final String description;
    private final String playUrl;

    private final String coverUrl;
    @DrawableRes
    private final Integer coverResId;

    private final List<String> tags;
    private final List<Song> songs;

    private final long playCount;
    private final long favoriteCount;

    public Playlist(String id,
                    String title,
                    String description,
                    String playUrl,
                    String coverUrl,
                    Integer coverResId,
                    List<String> tags,
                    List<Song> songs,
                    long playCount,
                    long favoriteCount) {

        this.id = id;
        this.title = title;
        this.description = description;
        this.playUrl = playUrl;
        this.coverUrl = coverUrl;
        this.coverResId = coverResId;

        this.tags = tags == null ? Collections.<String>emptyList() : new ArrayList<String>(tags);
        this.songs = songs == null ? Collections.<Song>emptyList() : new ArrayList<Song>(songs);

        this.playCount = playCount;
        this.favoriteCount = favoriteCount;
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getPlayUrl() {
        return playUrl;
    }

    public String getCoverUrl() {
        return coverUrl;
    }

    public Integer getCoverResId() {
        return coverResId;
    }

    public List<String> getTags() {
        return new ArrayList<>(tags);
    }

    public List<Song> getSongs() {
        return new ArrayList<>(songs);
    }

    public long getPlayCount() {
        return playCount;
    }

    public long getFavoriteCount() {
        return favoriteCount;
    }
}
