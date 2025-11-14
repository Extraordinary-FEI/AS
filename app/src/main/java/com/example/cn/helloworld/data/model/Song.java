package com.example.cn.helloworld.data.model;

import java.io.Serializable;

public class Song implements Serializable {

    private String id;
    private String title;
    private String artist;
    private String description;

    private long durationMs;

    private int audioResId;   // 本地 mp3
    private int coverResId;   // 本地图片

    public Song(String id,
                String title,
                String artist,
                String description,
                long durationMs,
                int audioResId,
                int coverResId) {

        this.id = id;
        this.title = title;
        this.artist = artist;
        this.description = description;
        this.durationMs = durationMs;

        this.audioResId = audioResId;
        this.coverResId = coverResId;
    }

    // ===== getter =====

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

    public int getAudioResId() {
        return audioResId;
    }

    public int getCoverResId() {
        return coverResId;
    }


}
