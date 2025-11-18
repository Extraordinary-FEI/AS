package com.example.cn.helloworld.data.repository;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import com.example.cn.helloworld.data.model.AdminMetrics;
import com.example.cn.helloworld.data.model.CartItem;
import com.example.cn.helloworld.data.model.Order;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class AdminMetricsRepository {

    private static final String PREFS_NAME = "admin_metrics_repo";
    private static final String KEY_ORDERS = "orders_json";
    private static final String KEY_REGISTRATIONS = "registrations";
    private static final String KEY_ACTIVE_USERS = "active_users";

    private static AdminMetricsRepository INSTANCE;

    private final SharedPreferences preferences;
    private final List<Order> orders = new ArrayList<Order>();
    private final Set<String> registrationUserIds = new HashSet<String>();
    private final Set<String> activeUserIds = new HashSet<String>();

    private AdminMetricsRepository(Context context) {
        preferences = context.getApplicationContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        loadFromStorage();
        if (orders.isEmpty()) {
            seed();
            persist();
        }
    }

    public static synchronized AdminMetricsRepository getInstance(Context context) {
        if (INSTANCE == null) {
            INSTANCE = new AdminMetricsRepository(context);
        }
        return INSTANCE;
    }

    private void seed() {
        for (int i = 0; i < 5; i++) {
            Order order = new Order("order-" + (1000 + i));
            order.setStatus(i % 2 == 0 ? "PAID" : "PENDING_PAYMENT");
            order.setShippingAddress("北京市朝阳区粉丝路 " + (10 + i) + " 号");
            List<CartItem> items = new ArrayList<CartItem>();
            items.add(new CartItem("seed-" + i, "千纸鹤限定徽章", 59.9, 1, null, true));
            items.add(new CartItem("seed-" + i + "-b", "生贺手幅", 29.9, 2, null, true));
            order.setItems(items);
            order.recalculateTotal();
            orders.add(order);
        }

        registrationUserIds.add("user_amy");
        registrationUserIds.add("user_bob");
        registrationUserIds.add("user_chris");
        registrationUserIds.add("user_dora");
        registrationUserIds.add("user_ella");

        activeUserIds.add("user_amy");
        activeUserIds.add("user_bob");
        activeUserIds.add("user_kim");
        activeUserIds.add("user_lee");
        activeUserIds.add("user_nia");
        activeUserIds.add("user_ola");
    }

    public AdminMetrics loadMetrics(SupportTaskRepository supportTaskRepository) {
        int orderCount = orders.size();
        int pendingTasks = supportTaskRepository.countTasksByStatus(SupportTaskRepository.STATUS_PENDING);
        int newRegistrations = registrationUserIds.size();
        int activeUsers = activeUserIds.size();
        return new AdminMetrics(orderCount, pendingTasks, newRegistrations, activeUsers);
    }

    public List<Order> getOrders() {
        return new ArrayList<Order>(orders);
    }

    public void recordOrder(Order order) {
        if (order == null) {
            return;
        }
        orders.add(order);
        persist();
    }

    public void registerUser(String userId) {
        if (TextUtils.isEmpty(userId)) {
            return;
        }
        registrationUserIds.add(userId);
        persist();
    }

    public void markActiveUser(String userId) {
        if (TextUtils.isEmpty(userId)) {
            return;
        }
        activeUserIds.add(userId);
        persist();
    }

    private void loadFromStorage() {
        orders.clear();
        String json = preferences.getString(KEY_ORDERS, null);
        if (!TextUtils.isEmpty(json)) {
            try {
                JSONArray array = new JSONArray(json);
                for (int i = 0; i < array.length(); i++) {
                    Order order = fromJson(array.getJSONObject(i));
                    if (order != null) {
                        orders.add(order);
                    }
                }
            } catch (JSONException ignored) {
                orders.clear();
            }
        }

        registrationUserIds.clear();
        registrationUserIds.addAll(preferences.getStringSet(KEY_REGISTRATIONS, new HashSet<String>()));

        activeUserIds.clear();
        activeUserIds.addAll(preferences.getStringSet(KEY_ACTIVE_USERS, new HashSet<String>()));
    }

    private void persist() {
        JSONArray array = new JSONArray();
        for (Order order : orders) {
            try {
                array.put(toJson(order));
            } catch (JSONException ignored) {
            }
        }
        preferences.edit()
                .putString(KEY_ORDERS, array.toString())
                .putStringSet(KEY_REGISTRATIONS, new HashSet<String>(registrationUserIds))
                .putStringSet(KEY_ACTIVE_USERS, new HashSet<String>(activeUserIds))
                .apply();
    }

    private JSONObject toJson(Order order) throws JSONException {
        JSONObject object = new JSONObject();
        object.put("orderId", order.getOrderId());
        object.put("status", order.getStatus());
        object.put("totalAmount", order.getTotalAmount());
        object.put("address", order.getShippingAddress());
        object.put("createdAt", order.getCreatedAt());
        JSONArray itemsArray = new JSONArray();
        for (CartItem item : order.getItems()) {
            JSONObject itemObject = new JSONObject();
            itemObject.put("productId", item.getProductId());
            itemObject.put("name", item.getProductName());
            itemObject.put("price", item.getUnitPrice());
            itemObject.put("quantity", item.getQuantity());
            itemsArray.put(itemObject);
        }
        object.put("items", itemsArray);
        return object;
    }

    private Order fromJson(JSONObject object) {
        if (object == null) {
            return null;
        }
        Order order = new Order(object.optString("orderId"));
        order.setStatus(object.optString("status"));
        order.setTotalAmount(object.optDouble("totalAmount", 0.0));
        order.setShippingAddress(object.optString("address"));
        order.setCreatedAt(object.optLong("createdAt", System.currentTimeMillis()));
        JSONArray array = object.optJSONArray("items");
        if (array != null) {
            List<CartItem> items = new ArrayList<CartItem>();
            for (int i = 0; i < array.length(); i++) {
                JSONObject itemObject = array.optJSONObject(i);
                if (itemObject == null) continue;
                CartItem item = new CartItem(
                        itemObject.optString("productId"),
                        itemObject.optString("name"),
                        itemObject.optDouble("price", 0.0),
                        itemObject.optInt("quantity", 0),
                        null,
                        true);
                items.add(item);
            }
            order.setItems(items);
        }
        order.recalculateTotal();
        return order;
    }
}
