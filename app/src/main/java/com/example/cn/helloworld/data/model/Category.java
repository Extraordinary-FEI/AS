package com.example.cn.helloworld.data.model;

public class Category {
    private final String id;
    private final String name;
    private final int iconResId;

    public Category(String id, String name, int iconResId) {
        this.id = id;
        this.name = name;
        this.iconResId = iconResId;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getIconResId() {
        return iconResId;
    }
}
