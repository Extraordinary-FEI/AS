package com.example.cn.helloworld.data.model;

import android.support.annotation.DrawableRes;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 歌单模型，包含基础信息与歌曲列表。
 */
public class Playlist {

    private final String id;
    private final String title;
    private final String description;
    private final String playUrl;
    private final String coverUrl;
    private final Integer coverResId;
    private final List<String> tags;
    private final List<Song> songs;

    public Playlist(String id,
                    String title,
                    String description,
                    String playUrl,
                    String coverUrl,
                    @DrawableRes Integer coverResId,
                    List<String> tags,
                    List<Song> songs) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.playUrl = playUrl;
        this.coverUrl = coverUrl;
        this.coverResId = coverResId;
        this.tags = tags == null ? Collections.<String>emptyList() : new ArrayList<String>(tags);
        this.songs = songs == null ? Collections.<Song>emptyList() : new ArrayList<Song>(songs);
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

    @DrawableRes
    public Integer getCoverResId() {
        return coverResId;
    }

    public List<String> getTags() {
        return new ArrayList<String>(tags);
    }

    public List<Song> getSongs() {
        return new ArrayList<Song>(songs);
    }

    public Playlist copyWith(String title,
                             String description,
                             String playUrl,
                             String coverUrl,
                             Integer coverResId,
                             List<String> tags,
                             List<Song> songs) {
        return new Playlist(
                id,
                title,
                description,
                playUrl,
                coverUrl,
                coverResId,
                tags,
                songs
        );
    }
}
