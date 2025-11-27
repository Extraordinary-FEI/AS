package com.example.cn.helloworld.data.model;

/**
 * 首页轮播图的后台模型。
 */
public class Banner {

    private final String id;
    private final String title;
    private final String description;
    private final int imageResId;

    public Banner(String id, String title, String description, int imageResId) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.imageResId = imageResId;
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

    public int getImageResId() {
        return imageResId;
    }
}
