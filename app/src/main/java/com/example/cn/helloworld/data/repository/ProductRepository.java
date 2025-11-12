package com.example.cn.helloworld.data.repository;

import android.content.Context;
import android.text.TextUtils;

import com.example.cn.helloworld.R;
import com.example.cn.helloworld.data.model.Category;
import com.example.cn.helloworld.data.model.Product;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

public class ProductRepository {

    private final Context appContext;
    private static final Map<String, Category> categories = new LinkedHashMap<>();
    private static final Map<String, List<String>> productIdsByCategory = new HashMap<>();
    private static final Map<String, Product> products = new LinkedHashMap<>();
    private static boolean initialized = false;

    public ProductRepository(Context context) {
        this.appContext = context.getApplicationContext();
        if (!initialized) {
            seedCategories();
            seedProducts();
            initialized = true;
        }
    }

    private void seedCategories() {
        int defaultIcon = appContext.getResources().getIdentifier(
                "ic_launcher",
                "mipmap",
                appContext.getPackageName());
        if (defaultIcon == 0) {
            defaultIcon = R.mipmap.ic_launcher;
        }

        Category coffee = new Category("coffee", "Coffee", defaultIcon);
        Category tea = new Category("tea", "Tea", defaultIcon);
        Category bakery = new Category("bakery", "Bakery", defaultIcon);

        categories.put(coffee.getId(), coffee);
        categories.put(tea.getId(), tea);
        categories.put(bakery.getId(), bakery);

        productIdsByCategory.put(coffee.getId(), new ArrayList<String>());
        productIdsByCategory.put(tea.getId(), new ArrayList<String>());
        productIdsByCategory.put(bakery.getId(), new ArrayList<String>());
    }

    private void seedProducts() {
        addProductInternal(new Product(
                "coffee_1",
                "Signature Latte",
                "A rich espresso with silky steamed milk and a hint of vanilla.",
                4.99,
                R.mipmap.ic_launcher,
                "coffee",
                24,
                Arrays.asList("Hot", "Seasonal"),
                Arrays.asList("Spring Launch")));

        addProductInternal(new Product(
                "coffee_2",
                "Cold Brew",
                "Slow-steeped cold brew with smooth chocolate notes.",
                3.49,
                R.mipmap.ic_launcher,
                "coffee",
                18,
                Arrays.asList("Cold", "House Favorite"),
                Collections.singletonList("Summer Specials")));

        addProductInternal(new Product(
                "tea_1",
                "Matcha Frappe",
                "Vibrant matcha blended with milk and ice for a refreshing treat.",
                4.59,
                R.mipmap.ic_launcher,
                "tea",
                30,
                Arrays.asList("Iced", "Limited"),
                Arrays.asList("Matcha Month", "Green Goodness")));

        addProductInternal(new Product(
                "bakery_1",
                "Blueberry Muffin",
                "Soft muffin with fresh blueberries and crunchy crumble topping.",
                2.79,
                R.mipmap.ic_launcher,
                "bakery",
                12,
                Collections.singletonList("Breakfast"),
                Collections.singletonList("Bakery Stars")));
    }

    private void addProductInternal(Product product) {
        products.put(product.getId(), product);
        List<String> ids = productIdsByCategory.get(product.getCategoryId());
        if (ids == null) {
            ids = new ArrayList<>();
            productIdsByCategory.put(product.getCategoryId(), ids);
        }
        if (!ids.contains(product.getId())) {
            ids.add(product.getId());
        }
    }

    public List<Category> getCategories() {
        return new ArrayList<>(categories.values());
    }

    public Category getCategoryById(String categoryId) {
        return categories.get(categoryId);
    }

    public List<Product> getProducts(String categoryId) {
        List<String> ids = productIdsByCategory.get(categoryId);
        if (ids == null) {
            return Collections.emptyList();
        }
        List<Product> result = new ArrayList<>();
        for (String id : ids) {
            Product product = products.get(id);
            if (product != null && product.isActive()) {
                result.add(product);
            }
        }
        return result;
    }

    public List<Product> getProductsForAdmin() {
        return new ArrayList<>(products.values());
    }

    public Product getProductById(String productId) {
        return products.get(productId);
    }

    public void saveProduct(Product product) {
        if (product == null) {
            return;
        }
        Product existing = products.get(product.getId());
        products.put(product.getId(), product);
        if (existing != null && !existing.getCategoryId().equals(product.getCategoryId())) {
            List<String> previous = productIdsByCategory.get(existing.getCategoryId());
            if (previous != null) {
                previous.remove(existing.getId());
            }
        }
        List<String> ids = productIdsByCategory.get(product.getCategoryId());
        if (ids == null) {
            ids = new ArrayList<>();
            productIdsByCategory.put(product.getCategoryId(), ids);
        }
        if (!ids.contains(product.getId())) {
            ids.add(product.getId());
        }
    }

    public void updateProductDetails(String productId,
                                     String name,
                                     String description,
                                     double price,
                                     int inventory,
                                     boolean active,
                                     String categoryId) {
        Product current = products.get(productId);
        if (current == null) {
            return;
        }
        Product updated = current.copyWith(name, description, price, inventory, active, categoryId);
        products.put(productId, updated);
        if (categoryId != null && !categoryId.equals(current.getCategoryId())) {
            List<String> oldIds = productIdsByCategory.get(current.getCategoryId());
            if (oldIds != null) {
                oldIds.remove(productId);
            }
            List<String> newIds = productIdsByCategory.get(categoryId);
            if (newIds == null) {
                newIds = new ArrayList<>();
                productIdsByCategory.put(categoryId, newIds);
            }
            if (!newIds.contains(productId)) {
                newIds.add(productId);
            }
        }
    }

    public void setProductActive(String productId, boolean active) {
        Product current = products.get(productId);
        if (current == null) {
            return;
        }
        updateProductDetails(productId,
                current.getName(),
                current.getDescription(),
                current.getPrice(),
                current.getInventory(),
                active,
                current.getCategoryId());
    }

    public String generateProductId(String prefix) {
        String base = TextUtils.isEmpty(prefix) ? "product" : prefix.toLowerCase(Locale.US);
        String candidate = base + "_" + UUID.randomUUID().toString().substring(0, 8);
        while (products.containsKey(candidate)) {
            candidate = base + "_" + UUID.randomUUID().toString().substring(0, 8);
        }
        return candidate;
    }
}
