package com.example.cn.helloworld.data.storage;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import com.example.cn.helloworld.data.model.CartItem;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * 简单的购物车持久化，使用 SharedPreferences 保存。方便从商品详情加入购物车。
 */
public class CartStorage {

    private static final String PREF_NAME = "cart_storage";
    private static final String KEY_ITEMS = "cart_items";

    private static CartStorage instance;
    private final SharedPreferences preferences;

    public static synchronized CartStorage getInstance(Context context) {
        if (instance == null) {
            instance = new CartStorage(context.getApplicationContext());
        }
        return instance;
    }

    private CartStorage(Context context) {
        preferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public List<CartItem> getItems() {
        String raw = preferences.getString(KEY_ITEMS, "");
        List<CartItem> items = new ArrayList<CartItem>();
        if (TextUtils.isEmpty(raw)) {
            return items;
        }
        try {
            JSONArray array = new JSONArray(raw);
            for (int i = 0; i < array.length(); i++) {
                JSONObject obj = array.getJSONObject(i);
                CartItem item = new CartItem(
                        obj.optString("productId"),
                        obj.optString("productName"),
                        obj.optDouble("unitPrice"),
                        obj.optInt("quantity", 1),
                        obj.optString("imageUrl"),
                        obj.optBoolean("selected", true)
                );
                items.add(item);
            }
        } catch (JSONException ignored) {
        }
        return items;
    }

    public void addOrIncrease(CartItem newItem) {
        if (newItem == null) {
            return;
        }
        List<CartItem> items = getItems();
        boolean found = false;
        for (int i = 0; i < items.size(); i++) {
            CartItem existing = items.get(i);
            if (existing != null && existing.getProductId().equals(newItem.getProductId())) {
                existing.increaseQuantity();
                existing.setSelected(true);
                found = true;
                break;
            }
        }
        if (!found) {
            items.add(newItem);
        }
        save(items);
    }

    public void save(List<CartItem> items) {
        JSONArray array = new JSONArray();
        if (items != null) {
            for (int i = 0; i < items.size(); i++) {
                CartItem item = items.get(i);
                if (item == null) continue;
                JSONObject obj = new JSONObject();
                try {
                    obj.put("productId", item.getProductId());
                    obj.put("productName", item.getProductName());
                    obj.put("unitPrice", item.getUnitPrice());
                    obj.put("quantity", item.getQuantity());
                    obj.put("imageUrl", item.getImageUrl());
                    obj.put("selected", item.isSelected());
                    array.put(obj);
                } catch (JSONException ignored) {
                }
            }
        }
        preferences.edit().putString(KEY_ITEMS, array.toString()).apply();
    }
}
