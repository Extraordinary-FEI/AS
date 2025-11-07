package com.example.cn.helloworld.data.model;

import java.io.Serializable;

/**
 * Represents an item added to the cart.
 */
public class CartItem implements Serializable {

    private String productId;
    private String productName;
    private double unitPrice;
    private int quantity;
    private String imageUrl;
    private boolean selected;

    public CartItem(String productId, String productName, double unitPrice) {
        this(productId, productName, unitPrice, 1, null, true);
    }

    public CartItem(String productId, String productName, double unitPrice, int quantity,
                    String imageUrl, boolean selected) {
        this.productId = productId;
        this.productName = productName;
        this.unitPrice = unitPrice;
        this.quantity = quantity;
        this.imageUrl = imageUrl;
        this.selected = selected;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public double getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(double unitPrice) {
        this.unitPrice = unitPrice;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public void increaseQuantity() {
        this.quantity = this.quantity + 1;
    }

    public void decreaseQuantity() {
        if (this.quantity > 1) {
            this.quantity = this.quantity - 1;
        }
    }

    public double getSubtotal() {
        return unitPrice * quantity;
    }
}
