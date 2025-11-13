package com.example.cn.helloworld.data.repository;

import android.content.Context;

import com.example.cn.helloworld.data.model.Category;
import com.example.cn.helloworld.data.model.Product;
import com.example.cn.helloworld.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class ProductRepository {

    private final List<Product> products = new ArrayList<Product>();

    public ProductRepository(Context context) {
        loadInitialProducts();
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
    }

    public void saveProduct(Product product) {
        products.add(product);
    }

    public void updateProductDetails(
            String productId,
            String name,
            String desc,
            double price,
            int inventory,
            boolean active,
            String coverUrl
    ) {
        for (Product p : products) {
            if (p.getId().equals(productId)) {
                p.setName(name);
                p.setDescription(desc);
                p.setPrice(price);
                p.setInventory(inventory);
                p.setActive(active);
                p.setCoverUrl(coverUrl);
                break;
            }
        }
    }

    // 供 ProductManagementActivity 使用
    public String generateProductId(String categoryId) {
        int count = 0;
        for (Product p : products) {
            if (p.getCategory().equals(categoryId)) {
                count++;
            }
        }
        return categoryId + "_" + (count + 1);
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
}
