package com.example.cn.helloworld.data.model;

import android.support.annotation.DrawableRes;

import java.io.Serializable;

public class Song implements Serializable {

    private String id;
    private String title;
    private String artist;
    private String description;
    private long durationMs;

    private String streamUrl;

    private String coverUrl;
    @DrawableRes
    private Integer coverResId;

    public Song(String id,
                String title,
                String artist,
                String description,
                long durationMs,
                String streamUrl,
                String coverUrl,
                Integer coverResId) {

        this.id = id;
        this.title = title;
        this.artist = artist;
        this.description = description;
        this.durationMs = durationMs;
        this.streamUrl = streamUrl;
        this.coverUrl = coverUrl;
        this.coverResId = coverResId;
    }

    // -------- getter / setter ---------

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getArtist() {
        return artist;
    }

    public String getDescription() {
        return description;
    }

    public long getDurationMs() {
        return durationMs;
    }

    public String getStreamUrl() {
        return streamUrl;
    }

    public String getCoverUrl() {
        return coverUrl;
    }

    public Integer getCoverResId() {
        return coverResId;
    }

    public void setCoverResId(Integer coverResId) {
        this.coverResId = coverResId;
    }

    public void setCoverUrl(String coverUrl) {
        this.coverUrl = coverUrl;
    }
}
