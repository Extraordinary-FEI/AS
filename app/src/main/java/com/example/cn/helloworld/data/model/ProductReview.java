package com.example.cn.helloworld.data.model;

import java.io.Serializable;

/**
 * Represents a review submitted for a product.
 */
public class ProductReview implements Serializable {

    private String productId;
    private String userName;
    private String content;
    private float rating;
    private long createdAt;

    public ProductReview(String productId, String userName, String content, float rating) {
        this(productId, userName, content, rating, System.currentTimeMillis());
    }

    public ProductReview(String productId, String userName, String content, float rating, long createdAt) {
        this.productId = productId;
        this.userName = userName;
        this.content = content;
        this.rating = rating;
        this.createdAt = createdAt;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }
}
