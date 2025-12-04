package com.example.cn.helloworld.data.model;

import java.io.Serializable;

public class Song implements Serializable {

    private String id;
    private String title;
    private String artist;
    private String description;

    private long durationMs;

    private int audioResId;     // 本地 mp3
    private int coverResId;     // 本地图片

    private String streamUrl;   // 线上播放 URL
    private String coverUrl;    // 线上封面 URL
    private String localFilePath; // 本地上传的音频路径
    private String coverImagePath;  // 新增：封面图片文件路径（可由后台设置）


    /** 本地资源构造器 */
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

        this.streamUrl = null;
        this.coverUrl = null;
        this.localFilePath = null;
    }

    /** 在线编辑用构造器 */
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
        this.coverResId = coverResId != null ? coverResId : 0;

        this.audioResId = 0; // 线上歌曲没有本地 mp3
        this.localFilePath = null;
    }

    /** 本地文件上传构造器 */
    public Song(String id,
                String title,
                String artist,
                String description,
                long durationMs,
                String localFilePath,
                Integer coverResId) {

        this.id = id;
        this.title = title;
        this.artist = artist;
        this.description = description;
        this.durationMs = durationMs;

        this.localFilePath = localFilePath;
        this.coverResId = coverResId != null ? coverResId : 0;

        this.audioResId = 0;
        this.streamUrl = null;
        this.coverUrl = null;
    }

    // ===== getter =====

    public String getId() { return id; }
    public String getTitle() { return title; }
    public String getArtist() { return artist; }
    public String getDescription() { return description; }
    public long getDurationMs() { return durationMs; }

    public int getAudioResId() { return audioResId; }
    public int getCoverResId() { return coverResId; }

    public String getStreamUrl() { return streamUrl; }
    public String getCoverUrl() { return coverUrl; }
    public String getLocalFilePath() { return localFilePath; }
    public String getCoverImagePath() {
        return coverImagePath;
    }

    public void setCoverImagePath(String coverImagePath) {
        this.coverImagePath = coverImagePath;
    }

}

