package com.example.cn.helloworld.data.model;

import java.util.Collections;
import java.util.List;

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
    private final boolean active;

    public Product(String id,
                   String name,
                   String description,
                   double price,
                   int imageResId,
                   String categoryId,
                   int inventory,
                   List<String> tags,
                   List<String> starEvents) {
        this(id, name, description, price, imageResId, categoryId, inventory, tags, starEvents, true);
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
                   boolean active) {
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

    public Product copyWith(String name,
                            String description,
                            double price,
                            int inventory,
                            boolean active,
                            String categoryId) {
        return new Product(id,
                name,
                description,
                price,
                imageResId,
                categoryId == null ? this.categoryId : categoryId,
                inventory,
                tags,
                starEvents,
                active);
    }
}
