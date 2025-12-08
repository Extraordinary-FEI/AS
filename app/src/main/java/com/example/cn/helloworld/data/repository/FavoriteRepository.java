package com.example.cn.helloworld.data.repository;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Simple local favorite storage for songs / products / support tasks.
 */
public class FavoriteRepository {

    private static final String PREF_NAME = "favorite_repository";
    private static final String KEY_SONGS = "favorite_songs";
    private static final String KEY_PRODUCTS = "favorite_products";
    private static final String KEY_TASKS = "favorite_tasks";

    private final SharedPreferences preferences;

    public FavoriteRepository(Context context) {
        preferences = context.getApplicationContext()
                .getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    // region song favorites
    public boolean isSongFavorite(String songId) {
        return getSet(KEY_SONGS).contains(songId);
    }

    public void setSongFavorite(String songId, boolean favorite) {
        updateSet(KEY_SONGS, songId, favorite);
    }

    public List<String> getFavoriteSongs() {
        return new ArrayList<String>(getSet(KEY_SONGS));
    }

    public void clearSongs() {
        preferences.edit().remove(KEY_SONGS).apply();
    }
    // endregion

    // region product favorites
    public boolean isProductFavorite(String productId) {
        return getSet(KEY_PRODUCTS).contains(productId);
    }

    public void setProductFavorite(String productId, boolean favorite) {
        updateSet(KEY_PRODUCTS, productId, favorite);
    }

    public List<String> getFavoriteProducts() {
        return new ArrayList<String>(getSet(KEY_PRODUCTS));
    }

    public void clearProducts() {
        preferences.edit().remove(KEY_PRODUCTS).apply();
    }
    // endregion

    // region support task favorites
    public boolean isTaskFavorite(String taskId) {
        return getSet(KEY_TASKS).contains(taskId);
    }

    public void setTaskFavorite(String taskId, boolean favorite) {
        updateSet(KEY_TASKS, taskId, favorite);
    }

    public List<String> getFavoriteTasks() {
        return new ArrayList<String>(getSet(KEY_TASKS));
    }

    public void clearTasks() {
        preferences.edit().remove(KEY_TASKS).apply();
    }
    // endregion

    public void clearAll() {
        clearSongs();
        clearProducts();
        clearTasks();
    }

    private Set<String> getSet(String key) {
        Set<String> stored = preferences.getStringSet(key, null);
        if (stored == null) {
            return new HashSet<String>();
        }
        return new HashSet<String>(stored);
    }

    private void updateSet(String key, String value, boolean add) {
        if (TextUtils.isEmpty(value)) {
            return;
        }
        Set<String> set = getSet(key);
        if (add) {
            set.add(value);
        } else {
            set.remove(value);
        }
        preferences.edit().putStringSet(key, set).apply();
    }
}

