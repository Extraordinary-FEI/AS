package com.example.cn.helloworld.data.repository;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import com.example.cn.helloworld.R;
import com.example.cn.helloworld.data.model.Banner;
import com.example.cn.helloworld.data.storage.AdminLocalStore;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * 轮播图的数据仓库，提供增删改查并持久化到 SharedPreferences。
 */
public class BannerRepository {

    private static final String KEY_BANNERS = "admin_home_banners";

    private static final Map<String, Banner> banners = new LinkedHashMap<String, Banner>();
    private static boolean initialized = false;
    private static SharedPreferences preferences;

    public BannerRepository(Context context) {
        if (context != null) {
            AdminLocalStore.init(context);
            preferences = AdminLocalStore.get(context);
        } else if (preferences == null && AdminLocalStore.isInitialized()) {
            preferences = AdminLocalStore.get();
        }
        if (preferences != null) {
            loadFromStorage();
            initialized = true;
        } else if (!initialized) {
            loadFromStorage();
            initialized = true;
        }
    }

    public List<Banner> getAllBanners() {
        return new ArrayList<Banner>(banners.values());
    }

    public void createBanner(Banner banner) {
        if (banner == null) {
            return;
        }
        banners.put(banner.getId(), banner);
        persist();
    }

    public void updateBanner(Banner banner) {
        if (banner == null) {
            return;
        }
        banners.put(banner.getId(), banner);
        persist();
    }

    public void deleteBanner(String bannerId) {
        if (bannerId == null) {
            return;
        }
        if (banners.remove(bannerId) != null) {
            persist();
        }
    }

    public String generateBannerId() {
        return "banner-" + UUID.randomUUID().toString();
    }

    private void loadFromStorage() {
        if (preferences == null) {
            seed();
            return;
        }
        String json = preferences.getString(KEY_BANNERS, null);
        if (TextUtils.isEmpty(json)) {
            seed();
            return;
        }
        try {
            JSONArray array = new JSONArray(json);
            banners.clear();
            for (int i = 0; i < array.length(); i++) {
                Banner banner = fromJson(array.getJSONObject(i));
                banners.put(banner.getId(), banner);
            }
        } catch (JSONException e) {
            seed();
        }
    }

    private void persist() {
        if (preferences == null) {
            return;
        }
        JSONArray array = new JSONArray();
        for (Banner banner : banners.values()) {
            array.put(toJson(banner));
        }
        preferences.edit().putString(KEY_BANNERS, array.toString()).commit();
    }

    private void seed() {
        banners.clear();
        addSeedBanner(new Banner("banner-birthday", "千玺生日月冲刺", "每日打卡累计生贺能量", R.drawable.cover_nishuo));
        addSeedBanner(new Banner("banner-public", "公益舞台回顾", "重温他与山城孩子的约定", R.drawable.cover_baobei));
        addSeedBanner(new Banner("banner-tour", "线下巡礼报名", "和小橙灯一起打卡地标应援点", R.drawable.song_cover));
        persist();
    }

    private void addSeedBanner(Banner banner) {
        banners.put(banner.getId(), banner);
    }

    private JSONObject toJson(Banner banner) {
        JSONObject object = new JSONObject();
        try {
            object.put("id", banner.getId());
            object.put("title", banner.getTitle());
            object.put("description", banner.getDescription());
            object.put("imageResId", banner.getImageResId());
        } catch (JSONException ignored) {
        }
        return object;
    }

    private Banner fromJson(JSONObject object) {
        return new Banner(
                object.optString("id"),
                object.optString("title"),
                object.optString("description"),
                object.optInt("imageResId", R.drawable.cover_nishuo)
        );
    }
}
