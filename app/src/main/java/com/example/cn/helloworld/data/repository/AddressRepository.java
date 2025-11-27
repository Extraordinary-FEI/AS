package com.example.cn.helloworld.data.repository;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.cn.helloworld.data.model.Address;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class AddressRepository {

    private static final String PREF_NAME = "user_addresses";
    private static final String KEY_ADDRESSES = "addresses";

    private final SharedPreferences sharedPreferences;

    public AddressRepository(Context context) {
        this.sharedPreferences = context.getApplicationContext()
                .getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public List<Address> loadAddresses() {
        String raw = sharedPreferences.getString(KEY_ADDRESSES, null);
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
            }
        }
        sharedPreferences.edit().putString(KEY_ADDRESSES, array.toString()).apply();
    }

    private List<Address> createDefault() {
        List<Address> demo = new ArrayList<Address>();
        demo.add(new Address("demo-1", "默认联系人", "13800001234", "北京市朝阳区应援大道 1 号"));
        return demo;
    }
}

