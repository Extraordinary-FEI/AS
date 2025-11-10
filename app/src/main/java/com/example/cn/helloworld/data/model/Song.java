package com.example.cn.helloworld.data.model;

import androidx.annotation.DrawableRes;

/**
 * 简单的歌曲模型，便于歌单与播放器模块复用。
 */
public class Song {

    private final String id;
    private final String title;
    private final String artist;
    private final long durationMs;
    private final String streamUrl;
    private final String description;
    private final String coverUrl;
    private final Integer coverResId;

    public Song(String id,
                String title,
                String artist,
                long durationMs,
                String streamUrl,
                String description,
                String coverUrl,
                @DrawableRes Integer coverResId) {
        this.id = id;
        this.title = title;
        this.artist = artist;
        this.durationMs = durationMs;
        this.streamUrl = streamUrl;
        this.description = description;
        this.coverUrl = coverUrl;
        this.coverResId = coverResId;
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getArtist() {
        return artist;
    }

    public long getDurationMs() {
        return durationMs;
    }

    public String getStreamUrl() {
        return streamUrl;
    }

    public String getDescription() {
        return description;
    }

    public String getCoverUrl() {
        return coverUrl;
    }

    @DrawableRes
    public Integer getCoverResId() {
        return coverResId;
    }
}
