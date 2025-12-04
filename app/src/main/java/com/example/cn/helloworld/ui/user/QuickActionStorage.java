package com.example.cn.helloworld.ui.user;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.cn.helloworld.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Persists quick action customization in SharedPreferences.
 */
class QuickActionStorage {

    private static final String PREF_NAME = "user_quick_actions";
    private static final String KEY_ACTIONS = "actions";

    private final SharedPreferences sharedPreferences;
    private final Context context;

    QuickActionStorage(Context context) {
        this.context = context.getApplicationContext();
        this.sharedPreferences = this.context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    List<QuickAction> loadActions() {
        String raw = sharedPreferences.getString(KEY_ACTIONS, null);
        if (raw == null) {
            return createDefaultActions();
        }

        try {
            JSONArray array = new JSONArray(raw);
            List<QuickAction> actions = new ArrayList<QuickAction>();
            for (int i = 0; i < array.length(); i++) {
                JSONObject object = array.getJSONObject(i);
                String id = object.optString("id", "");
                QuickAction.Type type = QuickAction.Type.fromId(object.optString("type", QuickAction.Type.NONE.getId()));
                if ("settings".equals(id) && type == QuickAction.Type.NONE) {
                    type = QuickAction.Type.SETTINGS;
                }
                if ("address".equals(id) && type == QuickAction.Type.SUPPORT) {
                    type = QuickAction.Type.ADDRESS;
                }
                String title = object.optString("title", context.getString(type.getDisplayNameRes()));
                String description = object.optString("description", "");
                actions.add(new QuickAction(id, type, title, description, type.getIconRes()));
            }
            return actions;
        } catch (JSONException e) {
            return createDefaultActions();
        }
    }

    void saveActions(List<QuickAction> actions) {
        JSONArray array = new JSONArray();
        for (QuickAction action : actions) {
            try {
                JSONObject object = new JSONObject();
                object.put("id", action.getId());
                object.put("type", action.getType().getId());
                object.put("title", action.getTitle());
                object.put("description", action.getDescription());
                array.put(object);
            } catch (JSONException ignored) {
                // ignore broken item
            }
        }
        sharedPreferences.edit().putString(KEY_ACTIONS, array.toString()).apply();
    }

    private List<QuickAction> createDefaultActions() {
        List<QuickAction> actions = new ArrayList<QuickAction>();
        actions.add(new QuickAction("orders", QuickAction.Type.ORDER,
                context.getString(R.string.user_action_orders),
                context.getString(R.string.user_action_orders_desc),
                QuickAction.Type.ORDER.getIconRes()));
        actions.add(new QuickAction("collection", QuickAction.Type.FAVORITE,
                context.getString(R.string.user_action_collection),
                context.getString(R.string.user_action_collection_desc),
                QuickAction.Type.FAVORITE.getIconRes()));
        actions.add(new QuickAction("address", QuickAction.Type.ADDRESS,
                context.getString(R.string.user_action_address),
                context.getString(R.string.user_action_address_desc),
                QuickAction.Type.ADDRESS.getIconRes()));
        actions.add(new QuickAction("settings", QuickAction.Type.SETTINGS,
                context.getString(R.string.user_action_settings),
                context.getString(R.string.user_action_settings_desc),
                QuickAction.Type.SETTINGS.getIconRes()));
        return actions;
    }
}
