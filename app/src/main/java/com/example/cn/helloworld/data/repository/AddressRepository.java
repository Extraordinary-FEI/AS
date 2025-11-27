package com.example.cn.helloworld.data.repository;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import com.example.cn.helloworld.data.model.Address;
import com.example.cn.helloworld.data.session.SessionManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class AddressRepository {

    private static final String PREF_NAME = "user_addresses";
    private static final String KEY_ADDRESSES = "addresses";
    private static final String KEY_ADDRESSES_PREFIX = "addresses_";

    private final SharedPreferences sharedPreferences;
    private final String addressesKey;

    public AddressRepository(Context context) {
        Context appContext = context.getApplicationContext();
        this.sharedPreferences = appContext.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);

        // 安全初始化：防止 SessionManager 崩溃导致整个应用闪退
        String roleSuffix = "user";
        String userIdentity = "";

        try {
            SessionManager sessionManager = new SessionManager(appContext);
            if (sessionManager.isAdmin()) {
                roleSuffix = "admin";
            } else {
                roleSuffix = "user";
            }
            userIdentity = sessionManager.getUserId();
            if (TextUtils.isEmpty(userIdentity)) {
                userIdentity = sessionManager.getUsername();
            }
        } catch (Exception e) {
            // 捕获任何异常，避免因 SessionManager 问题导致闪退
            e.printStackTrace();
            roleSuffix = "user";
            userIdentity = "";
        }

        if (TextUtils.isEmpty(userIdentity)) {
            this.addressesKey = KEY_ADDRESSES_PREFIX + roleSuffix;
        } else {
            this.addressesKey = KEY_ADDRESSES_PREFIX + roleSuffix + "_" + userIdentity;
        }
    }

    public List<Address> loadAddresses() {
        String raw = sharedPreferences.getString(addressesKey, null);
        // 兼容旧版本数据
        if (raw == null) {
            raw = sharedPreferences.getString(KEY_ADDRESSES, null);
        }
        if (raw == null) {
            return createDefault();
        }

        try {
            JSONArray array = new JSONArray(raw);
            List<Address> result = new ArrayList<Address>();
            for (int i = 0; i < array.length(); i++) {
                JSONObject obj = array.getJSONObject(i);
                result.add(new Address(
                        obj.optString("id", ""),
                        obj.optString("name", ""),
                        obj.optString("phone", ""),
                        obj.optString("detail", "")));
            }
            return result;
        } catch (JSONException e) {
            e.printStackTrace();
            return createDefault();
        }
    }

    public void saveAddresses(List<Address> addresses) {
        JSONArray array = new JSONArray();
        for (Address address : addresses) {
            JSONObject obj = new JSONObject();
            try {
                obj.put("id", address.getId());
                obj.put("name", address.getContactName());
                obj.put("phone", address.getPhone());
                obj.put("detail", address.getDetail());
                array.put(obj);
            } catch (JSONException ignored) {
                // 忽略单个地址序列化失败
            }
        }
        sharedPreferences.edit().putString(addressesKey, array.toString()).apply();
    }

    private List<Address> createDefault() {
        List<Address> demo = new ArrayList<Address>();
        demo.add(new Address("demo-1", "默认联系人", "13800001234", "北京市朝阳区应援大道 1 号"));
        return demo;
    }
}