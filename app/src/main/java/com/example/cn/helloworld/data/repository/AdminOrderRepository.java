package com.example.cn.helloworld.data.repository;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import com.example.cn.helloworld.data.model.CartItem;
import com.example.cn.helloworld.data.model.Order;
import com.example.cn.helloworld.data.storage.AdminLocalStore;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * 持久化管理员订单，用于在后台完成增删改查。
 */
public class AdminOrderRepository {

    private static final String KEY_ADMIN_ORDERS = "admin_orders";

    private final SharedPreferences preferences;

    public AdminOrderRepository(Context context) {
        this.preferences = AdminLocalStore.get(context);
    }

    public synchronized List<Order> getOrders() {
        List<Order> orders = readOrders();
        if (orders.isEmpty()) {
            seedSampleOrders();
            orders = readOrders();
        }
        return orders;
    }

    public synchronized void saveOrUpdate(Order order) {
        if (order == null || TextUtils.isEmpty(order.getOrderId())) {
            return;
        }
        List<Order> orders = new ArrayList<Order>(readOrders());
        boolean updated = false;
        for (int i = 0; i < orders.size(); i++) {
            if (orders.get(i) != null && order.getOrderId().equals(orders.get(i).getOrderId())) {
                orders.set(i, order);
                updated = true;
                break;
            }
        }
        if (!updated) {
            orders.add(order);
        }
        persistOrders(orders);
    }

    public synchronized void delete(String orderId) {
        if (TextUtils.isEmpty(orderId)) {
            return;
        }
        List<Order> orders = new ArrayList<Order>();
        List<Order> existing = readOrders();
        for (int i = 0; i < existing.size(); i++) {
            Order order = existing.get(i);
            if (order == null) continue;
            if (orderId.equals(order.getOrderId())) {
                continue;
            }
            orders.add(order);
        }
        persistOrders(orders);
    }

    public synchronized int count() {
        return readOrders().size();
    }

    private void seedSampleOrders() {
        long now = System.currentTimeMillis();
        List<Order> samples = new ArrayList<Order>();
        for (int i = 0; i < 6; i++) {
            Order order = new Order(String.format(Locale.getDefault(), "order-%d", 1000 + i));
            order.setTotalAmount(128 + (i * 25));
            order.setStatus(i % 2 == 0 ? "PAID" : "CREATED");
            order.setCreatedAt(now - i * 60L * 60L * 1000L);
            samples.add(order);
        }
        persistOrders(samples);
    }

    private void persistOrders(List<Order> orders) {
        JSONArray array = new JSONArray();
        for (int i = 0; i < orders.size(); i++) {
            Order order = orders.get(i);
            if (order == null) continue;
            array.put(toJson(order));
        }
        preferences.edit().putString(KEY_ADMIN_ORDERS, array.toString()).apply();
    }

    private List<Order> readOrders() {
        String stored = preferences.getString(KEY_ADMIN_ORDERS, "");
        if (TextUtils.isEmpty(stored)) {
            return new ArrayList<Order>();
        }
        List<Order> list = new ArrayList<Order>();
        try {
            JSONArray array = new JSONArray(stored);
            for (int i = 0; i < array.length(); i++) {
                JSONObject obj = array.optJSONObject(i);
                Order order = fromJson(obj);
                if (order != null) {
                    list.add(order);
                }
            }
        } catch (JSONException e) {
            return new ArrayList<Order>();
        }
        return list;
    }

    private JSONObject toJson(Order order) {
        JSONObject obj = new JSONObject();
        try {
            obj.put("orderId", order.getOrderId());
            obj.put("totalAmount", order.getTotalAmount());
            obj.put("status", order.getStatus());
            obj.put("shippingAddress", order.getShippingAddress());
            obj.put("createdAt", order.getCreatedAt());
            JSONArray items = new JSONArray();
            if (order.getItems() != null) {
                List<CartItem> cartItems = order.getItems();
                for (int i = 0; i < cartItems.size(); i++) {
                    CartItem item = cartItems.get(i);
                    if (item == null) continue;
                    JSONObject itemObj = new JSONObject();
                    itemObj.put("productId", item.getProductId());
                    itemObj.put("productName", item.getProductName());
                    itemObj.put("quantity", item.getQuantity());
                    itemObj.put("unitPrice", item.getUnitPrice());
                    items.put(itemObj);
                }
            }
            obj.put("items", items);
        } catch (JSONException e) {
            // ignore
        }
        return obj;
    }

    private Order fromJson(JSONObject obj) {
        if (obj == null) return null;
        String id = obj.optString("orderId");
        if (TextUtils.isEmpty(id)) {
            return null;
        }
        JSONArray itemsArray = obj.optJSONArray("items");
        List<CartItem> items = new ArrayList<CartItem>();
        if (itemsArray != null) {
            for (int i = 0; i < itemsArray.length(); i++) {
                JSONObject itemObj = itemsArray.optJSONObject(i);
                if (itemObj == null) continue;
                String productId = itemObj.optString("productId");
                String productName = itemObj.optString("productName");
                int quantity = itemObj.optInt("quantity", 1);
                double unitPrice = itemObj.optDouble("unitPrice", 0.0);
                items.add(new CartItem(productId, productName, unitPrice, quantity, null, true));
            }
        }
        double totalAmount = obj.optDouble("totalAmount", 0.0);
        String status = obj.optString("status", "CREATED");
        String shippingAddress = obj.optString("shippingAddress", "");
        long createdAt = obj.optLong("createdAt", System.currentTimeMillis());
        Order order = new Order(id, items, totalAmount, status, shippingAddress, createdAt);
        if (items.isEmpty() && totalAmount <= 0) {
            order.recalculateTotal();
        }
        return order;
    }
}
