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
        addSeedBanner(new Banner("banner-tx1", "新歌速递", "抢先听千玺全新单曲，幕后花絮同步上线", R.drawable.banner_tx1));
        addSeedBanner(new Banner("banner-tx2", "巡演现场", "沉浸式回顾全国巡演高燃片段，光影与呼吸同频", R.drawable.banner_tx2));
        addSeedBanner(new Banner("banner-tx3", "粉丝限定礼遇", "会员签到礼、生日贺卡与周边抽奖，专属宠爱升级", R.drawable.banner_tx3));
        addSeedBanner(new Banner("banner-tx4", "练习室直拍", "捕捉每日排练瞬间，见证舞台打磨的点滴", R.drawable.banner_tx4));
        addSeedBanner(new Banner("banner-tx5", "公益同行", "与千玺一起关注公益项目，传递舞台之外的温度", R.drawable.banner_tx5));
        addSeedBanner(new Banner("banner-tx6", "社群活动", "参与应援任务、线下见面与群聊活动，组队相遇", R.drawable.banner_tx6));
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
                object.optInt("imageResId", R.drawable.banner_tx1)
        );
    }
}
