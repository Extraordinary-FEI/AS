package com.example.cn.helloworld.data.repository;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import com.example.cn.helloworld.R;
import com.example.cn.helloworld.data.model.Category;
import com.example.cn.helloworld.data.model.Product;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class ProductRepository {

    private static final String PREFS_NAME = "product_repo";
    private static final String KEY_PRODUCTS = "products_json";

    private static ProductRepository INSTANCE;

    private final Context appContext;
    private final SharedPreferences preferences;
    private final List<Product> products = new ArrayList<Product>();

    private ProductRepository(Context context) {
        this.appContext = context.getApplicationContext();
        this.preferences = appContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        loadProductsFromStorage();
        if (products.isEmpty()) {
            loadInitialProducts();
            persist();
        }
    }

    public static synchronized ProductRepository getInstance(Context context) {
        if (INSTANCE == null) {
            INSTANCE = new ProductRepository(context);
        }
        return INSTANCE;
    }

    // 初始化三类商品
    private void loadInitialProducts() {

        products.add(new Product(
                "ticket_001",
                "易烊千玺演唱会 · VIP门票",
                "演唱会前排VIP通道，限量发售。",
                1299.0,
                50,
                "qianxi_ticket",
                5,
                Arrays.asList("演唱会", "VIP", "限量"),
                Arrays.asList("YICOOL LIVE"),
                true,
                "android.resource://com.example.cn.helloworld/drawable/cover_nishuo",
                "2025-01-01",
                createAttributes("区域", "A1", "座位类型", "VIP")
        ));

        products.add(new Product(
                "merch_001",
                "易烊千玺官方应援灯牌",
                "高亮LED灯牌，官方正版。",
                199.0,
                300,
                "qianxi_merch",
                5,
                Arrays.asList("应援", "官方", "实体周边"),
                Arrays.asList("Support"),
                true,
                "android.resource://com.example.cn.helloworld/drawable/song_cover",
                "2025-01-05",
                createAttributes("颜色", "白色", "灯光", "三档可调")
        ));

        products.add(new Product(
                "signed_001",
                "易烊千玺 · 手写 To 签",
                "粉丝限定款，数量有限。",
                899.0,
                20,
                "qianxi_signed",
                5,
                Arrays.asList("签名", "限量"),
                Arrays.asList("Exclusive"),
                true,
                "android.resource://com.example.cn.helloworld/drawable/cover_baobei",
                "2025-01-10",
                createAttributes("签字笔颜色", "黑色")
        ));
    }

    private HashMap<String,String> createAttributes(String k1, String v1) {
        HashMap<String,String> map = new HashMap<String, String>();
        map.put(k1, v1);
        return map;
    }

    private HashMap<String,String> createAttributes(String k1, String v1, String k2, String v2) {
        HashMap<String,String> map = new HashMap<String, String>();
        map.put(k1, v1);
        map.put(k2, v2);
        return map;
    }

    public List<Product> getAll() {
        return new ArrayList<Product>(products);
    }

    public List<Product> getByCategory(String categoryId) {
        List<Product> result = new ArrayList<Product>();
        for (Product p : products) {
            if (p.getCategory().equals(categoryId)) {
                result.add(p);
            }
        }
        return result;
    }

    public Product getProductById(String productId) {
        for (Product p : products) {
            if (p.getId().equals(productId)) {
                return p;
            }
        }
        return null;
    }

    public List<Product> getProducts(String categoryId) {
        return getByCategory(categoryId);
    }

    // 管理员获取商品列表
    public List<Product> getProductsForAdmin() {
        return getAll();
    }

    public void setProductActive(String productId, boolean active) {
        for (Product p : products) {
            if (p.getId().equals(productId)) {
                p.setActive(active);
                break;
            }
        }
        persist();
    }

    public void saveProduct(Product product) {
        if (product == null) {
            return;
        }
        products.add(product);
        persist();
    }

    public void updateProductDetails(
            String productId,
            String name,
            String desc,
            double price,
            int inventory,
            boolean active,
            String categoryId
    ) {
        for (Product p : products) {
            if (p.getId().equals(productId)) {
                p.setName(name);
                p.setDescription(desc);
                p.setPrice(price);
                p.setInventory(inventory);
                p.setActive(active);
                p.setCategory(categoryId);
                break;
            }
        }
        persist();
    }

    // 供 ProductManagementActivity 使用
    public String generateProductId(String categoryId) {
        String safeCategory = TextUtils.isEmpty(categoryId) ? "product" : categoryId;
        int maxIndex = 0;
        for (Product p : products) {
            if (safeCategory.equals(p.getCategory())) {
                String[] parts = p.getId().split("_");
                if (parts.length > 1) {
                    try {
                        int index = Integer.parseInt(parts[parts.length - 1]);
                        if (index > maxIndex) {
                            maxIndex = index;
                        }
                    } catch (NumberFormatException ignore) {
                        // ignore unparsable ids
                    }
                }
            }
        }
        return String.format(Locale.getDefault(), "%s_%d", safeCategory, maxIndex + 1);
    }

    // 供 CategoryFragment / ProductListFragment 使用
    public List<Category> getCategories() {
        List<Category> list = new ArrayList<Category>();
        list.add(new Category("qianxi_ticket",
                "演唱会门票", R.drawable.ic_category_ticket));
        list.add(new Category("qianxi_merch",
                "应援周边", R.drawable.ic_category_merch));
        list.add(new Category("qianxi_signed",
                "签名 / To签", R.drawable.ic_category_signed));
        return list;
    }

    private void loadProductsFromStorage() {
        products.clear();
        String json = preferences.getString(KEY_PRODUCTS, null);
        if (TextUtils.isEmpty(json)) {
            return;
        }
        try {
            JSONArray array = new JSONArray(json);
            for (int i = 0; i < array.length(); i++) {
                JSONObject object = array.getJSONObject(i);
                Product product = fromJson(object);
                if (product != null) {
                    products.add(product);
                }
            }
        } catch (JSONException ignored) {
            products.clear();
        }
    }

    private void persist() {
        JSONArray array = new JSONArray();
        for (Product product : products) {
            try {
                array.put(toJson(product));
            } catch (JSONException ignored) {
            }
        }
        preferences.edit().putString(KEY_PRODUCTS, array.toString()).apply();
    }

    private Product fromJson(JSONObject object) {
        if (object == null) {
            return null;
        }
        String id = object.optString("id");
        String name = object.optString("name");
        String description = object.optString("description");
        double price = object.optDouble("price", 0.0);
        int inventory = object.optInt("inventory", 0);
        String category = object.optString("category");
        int rating = object.optInt("rating", 0);
        List<String> tags = jsonArrayToList(object.optJSONArray("tags"));
        List<String> starEvents = jsonArrayToList(object.optJSONArray("starEvents"));
        boolean active = object.optBoolean("active", true);
        String coverUrl = optStringOrNull(object, "coverUrl");
        String releaseTime = optStringOrNull(object, "releaseTime");
        Map<String, String> attributes = jsonToMap(object.optJSONObject("attributes"));
        int imageResId = object.optInt("imageResId", 0);
        String limited = object.optString("limitedQuantity", "");
        Map<String, String> categoryAttrs = jsonToMap(object.optJSONObject("categoryAttributes"));

        return new Product(id, name, description, price, inventory, category, rating,
                tags, starEvents, active, coverUrl, releaseTime, attributes,
                imageResId, limited, categoryAttrs);
    }

    private JSONObject toJson(Product product) throws JSONException {
        JSONObject object = new JSONObject();
        object.put("id", product.getId());
        object.put("name", product.getName());
        object.put("description", product.getDescription());
        object.put("price", product.getPrice());
        object.put("inventory", product.getInventory());
        object.put("category", product.getCategory());
        object.put("rating", product.getRating());
        object.put("tags", new JSONArray(product.getTags()));
        object.put("starEvents", new JSONArray(product.getStarEvents()));
        object.put("active", product.isActive());
        if (!TextUtils.isEmpty(product.getCoverUrl())) {
            object.put("coverUrl", product.getCoverUrl());
        }
        if (!TextUtils.isEmpty(product.getReleaseTime())) {
            object.put("releaseTime", product.getReleaseTime());
        }
        object.put("attributes", mapToJson(product.getAttributes()));
        object.put("imageResId", product.getImageResId());
        object.put("limitedQuantity", product.getLimitedQuantity());
        object.put("categoryAttributes", mapToJson(product.getCategoryAttributes()));
        return object;
    }

    private List<String> jsonArrayToList(JSONArray array) {
        List<String> list = new ArrayList<String>();
        if (array == null) {
            return list;
        }
        for (int i = 0; i < array.length(); i++) {
            list.add(array.optString(i));
        }
        return list;
    }

    private JSONObject mapToJson(Map<String, String> map) throws JSONException {
        JSONObject object = new JSONObject();
        if (map == null) {
            return object;
        }
        for (Map.Entry<String, String> entry : map.entrySet()) {
            if (!TextUtils.isEmpty(entry.getKey()) && entry.getValue() != null) {
                object.put(entry.getKey(), entry.getValue());
            }
        }
        return object;
    }

    private Map<String, String> jsonToMap(JSONObject object) {
        Map<String, String> map = new HashMap<String, String>();
        if (object == null) {
            return map;
        }
        JSONArray names = object.names();
        if (names == null) {
            return map;
        }
        for (int i = 0; i < names.length(); i++) {
            String key = names.optString(i);
            map.put(key, object.optString(key));
        }
        return map;
    }

    private String optStringOrNull(JSONObject object, String key) {
        if (object == null) {
            return null;
        }
        String value = object.optString(key, null);
        if (TextUtils.isEmpty(value)) {
            return null;
        }
        return value;
    }
}
