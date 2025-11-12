package com.example.cn.helloworld.data.model;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 商品模型：用于描述应援商品、门票、签名照等。
 * 兼容后台管理（active状态）与前台展示（releaseTime、limitedQuantity）。
 */
public class Product {
    private final String id;
    private final String name;
    private final String description;
    private final double price;
    private final int imageResId;
    private final String categoryId;
    private final int inventory;
    private final List<String> tags;
    private final List<String> starEvents;

    // 管理功能字段
    private final boolean active;

    // 展示功能字段
    private final String releaseTime;
    private final String limitedQuantity;
    private final Map<String, String> categoryAttributes;

    public Product(String id,
                   String name,
                   String description,
                   double price,
                   int imageResId,
                   String categoryId,
                   int inventory,
                   List<String> tags,
                   List<String> starEvents) {
        this(id, name, description, price, imageResId, categoryId, inventory,
                tags, starEvents, true, "", "", Collections.<String, String>emptyMap());
    }

    public Product(String id,
                   String name,
                   String description,
                   double price,
                   int imageResId,
                   String categoryId,
                   int inventory,
                   List<String> tags,
                   List<String> starEvents,
                   boolean active,
                   String releaseTime,
                   String limitedQuantity,
                   Map<String, String> categoryAttributes) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
        this.imageResId = imageResId;
        this.categoryId = categoryId;
        this.inventory = inventory;
        this.tags = tags == null ? (List<String>) Collections.emptyList() : Collections.unmodifiableList(tags);
        this.starEvents = starEvents == null ? (List<String>) Collections.emptyList() : Collections.unmodifiableList(starEvents);
        this.active = active;
        this.releaseTime = releaseTime == null ? "" : releaseTime;
        this.limitedQuantity = limitedQuantity == null ? "" : limitedQuantity;
        if (categoryAttributes == null || categoryAttributes.isEmpty()) {
            this.categoryAttributes = Collections.emptyMap();
        } else {
            this.categoryAttributes = Collections.unmodifiableMap(new HashMap<>(categoryAttributes));
        }
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

    public double getPrice() {
        return price;
    }

    public int getImageResId() {
        return imageResId;
    }

    public String getCategoryId() {
        return categoryId;
    }

    public int getInventory() {
        return inventory;
    }

    public List<String> getTags() {
        return tags;
    }

    public List<String> getStarEvents() {
        return starEvents;
    }

    public boolean isActive() {
        return active;
    }

    public String getReleaseTime() {
        return releaseTime;
    }

    public String getLimitedQuantity() {
        return limitedQuantity;
    }

    public Map<String, String> getCategoryAttributes() {
        return categoryAttributes;
    }

    /**
     * 创建一个修改版副本，用于后台编辑更新商品信息。
     */
    public Product copyWith(String name,
                            String description,
                            double price,
                            int inventory,
                            boolean active,
                            String categoryId,
                            String releaseTime,
                            String limitedQuantity,
                            Map<String, String> categoryAttributes) {
        return new Product(
                id,
                name != null ? name : this.name,
                description != null ? description : this.description,
                price,
                imageResId,
                categoryId != null ? categoryId : this.categoryId,
                inventory,
                tags,
                starEvents,
                active,
                releaseTime != null ? releaseTime : this.releaseTime,
                limitedQuantity != null ? limitedQuantity : this.limitedQuantity,
                categoryAttributes != null ? categoryAttributes : this.categoryAttributes
        );
    }
}
