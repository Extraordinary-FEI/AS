package com.example.cn.helloworld.data.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Represents an order placed from the cart.
 */
public class Order implements Serializable {

    private String orderId;
    private List<CartItem> items;
    private double totalAmount;
    private String status;
    private String shippingAddress;
    private long createdAt;

    public Order(String orderId) {
        this(orderId, new ArrayList<CartItem>(), 0.0, "CREATED", null, System.currentTimeMillis());
    }

    public Order(String orderId, List<CartItem> items, double totalAmount, String status,
                 String shippingAddress, long createdAt) {
        this.orderId = orderId;
        this.items = new ArrayList<CartItem>();
        if (items != null) {
            this.items.addAll(items);
        }
        this.totalAmount = totalAmount;
        this.status = status;
        this.shippingAddress = shippingAddress;
        this.createdAt = createdAt;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public List<CartItem> getItems() {
        return Collections.unmodifiableList(items);
    }

    public void setItems(List<CartItem> items) {
        this.items.clear();
        if (items != null) {
            this.items.addAll(items);
        }
        recalculateTotal();
    }

    public void addItem(CartItem item) {
        if (item != null) {
            this.items.add(item);
            this.totalAmount = this.totalAmount + item.getSubtotal();
        }
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getShippingAddress() {
        return shippingAddress;
    }

    public void setShippingAddress(String shippingAddress) {
        this.shippingAddress = shippingAddress;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }

    public void recalculateTotal() {
        double sum = 0.0;
        for (int i = 0; i < items.size(); i++) {
            sum = sum + items.get(i).getSubtotal();
        }
        this.totalAmount = sum;
    }
}
