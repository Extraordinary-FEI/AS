package com.example.cn.helloworld.ui.user;

/**
 * 线下打卡点数据模型。
 */
public class CheckinLocation {
    private final String id;
    private final String name;
    private final String description;
    private final String tips;
    private final String geoQuery;

    public CheckinLocation(String id, String name, String description, String tips, String geoQuery) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.tips = tips;
        this.geoQuery = geoQuery;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getTips() {
        return tips;
    }

    public String getGeoQuery() {
        return geoQuery;
    }
}
