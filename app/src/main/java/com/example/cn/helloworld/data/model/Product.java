package com.example.cn.helloworld.data.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Product implements Serializable {

    private String id;
    private String name;
    private String description;
    private double price;
    private int inventory;
    private String category;

    private int rating;
    private List<String> tags;
    private List<String> starEvents;

    private boolean active;
    private String coverUrl;
    private String releaseTime;
    private Map<String, String> attributes;

    // 是否展示在首页上方的切换位
    private boolean featuredOnHome;

    // 供详情页使用的扩展字段
    private int imageResId;                       // 图片资源
    private String limitedQuantity = "";          // 限购/限量信息
    private Map<String, String> categoryAttributes; // 分类属性（例如颜色、尺寸等）

    public Product(String productId, String name, String description,
                   double price, int icLauncher,
                   String resolvedCategoryId, int inventory,
                   List<String> strings, List<String> stringList, boolean active) {
        this.tags = new ArrayList<String>();
        this.starEvents = new ArrayList<String>();
    }

    // 简化版构造器
    public Product(
            String id,
            String name,
            String description,
            double price,
            int inventory,
            String category,
            int rating,
            List<String> tags,
            List<String> starEvents
    ) {
        this(id, name, description, price, inventory, category, rating,
                tags, starEvents, true, null, null, null, 0, "", null);
    }

    // 完整构造器（仓库里在用）
    public Product(
            String id,
            String name,
            String description,
            double price,
            int inventory,
            String category,
            int rating,
            List<String> tags,
            List<String> starEvents,
            boolean active,
            String coverUrl,
            String releaseTime,
            Map<String, String> attributes
    ) {
        this(id, name, description, price, inventory, category, rating,
                tags, starEvents, active, coverUrl, releaseTime, attributes,
                0, "", null, false);
    }

    // 最终大构造器
    public Product(
            String id,
            String name,
            String description,
            double price,
            int inventory,
            String category,
            int rating,
            List<String> tags,
            List<String> starEvents,
            boolean active,
            String coverUrl,
            String releaseTime,
            Map<String, String> attributes,
            int imageResId,
            String limitedQuantity,
            Map<String, String> categoryAttributes,
            boolean featuredOnHome
    ) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
        this.inventory = inventory;
        this.category = category;
        this.rating = rating;

        this.tags = tags != null ? tags : new ArrayList<String>();
        this.starEvents = starEvents != null ? starEvents : new ArrayList<String>();

        this.active = active;
        this.coverUrl = coverUrl;
        this.releaseTime = releaseTime;
        this.attributes = attributes;

        this.imageResId = imageResId;
        this.limitedQuantity = limitedQuantity != null ? limitedQuantity : "";
        this.categoryAttributes = categoryAttributes;
        this.featuredOnHome = featuredOnHome;
    }

    // ---------- getter / setter ----------
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }

    public int getInventory() { return inventory; }
    public void setInventory(int inventory) { this.inventory = inventory; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public int getRating() { return rating; }
    public void setRating(int rating) { this.rating = rating; }

    public List<String> getTags() { return tags; }
    public void setTags(List<String> tags) {
        this.tags = tags != null ? tags : new ArrayList<String>();
    }

    public List<String> getStarEvents() { return starEvents; }
    public void setStarEvents(List<String> starEvents) {
        this.starEvents = starEvents != null ? starEvents : new ArrayList<String>();
    }

    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }

    public String getCoverUrl() { return coverUrl; }
    public void setCoverUrl(String coverUrl) { this.coverUrl = coverUrl; }

    public String getReleaseTime() { return releaseTime; }
    public void setReleaseTime(String releaseTime) { this.releaseTime = releaseTime; }

    public Map<String, String> getAttributes() { return attributes; }
    public void setAttributes(Map<String, String> attributes) { this.attributes = attributes; }

    public String getCategoryId() { return category; }

    public int getImageResId() { return imageResId; }
    public void setImageResId(int imageResId) { this.imageResId = imageResId; }

    public String getLimitedQuantity() { return limitedQuantity; }
    public void setLimitedQuantity(String limitedQuantity) { this.limitedQuantity = limitedQuantity; }

    public Map<String, String> getCategoryAttributes() { return categoryAttributes; }
    public void setCategoryAttributes(Map<String, String> categoryAttributes) {
        this.categoryAttributes = categoryAttributes;
    }

    public boolean isFeaturedOnHome() { return featuredOnHome; }
    public void setFeaturedOnHome(boolean featuredOnHome) { this.featuredOnHome = featuredOnHome; }
}
