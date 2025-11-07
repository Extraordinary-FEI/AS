package com.example.cn.helloworld.data.repository;

import android.content.Context;

import com.example.cn.helloworld.R;
import com.example.cn.helloworld.data.model.Category;
import com.example.cn.helloworld.data.model.Product;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProductRepository {

    private final Context appContext;
    private final Map<String, Category> categories = new HashMap<>();
    private final Map<String, List<Product>> productsByCategory = new HashMap<>();

    public ProductRepository(Context context) {
        this.appContext = context.getApplicationContext();
        seedCategories();
        seedProducts();
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
    }

    private void seedProducts() {
        List<Product> coffeeProducts = Arrays.asList(
                new Product(
                        "coffee_1",
                        "Signature Latte",
                        "A rich espresso with silky steamed milk and a hint of vanilla.",
                        4.99,
                        R.mipmap.ic_launcher,
                        "coffee",
                        24,
                        Arrays.asList("Hot", "Seasonal"),
                        Arrays.asList("Spring Launch")),
                new Product(
                        "coffee_2",
                        "Cold Brew",
                        "Slow-steeped cold brew with smooth chocolate notes.",
                        3.49,
                        R.mipmap.ic_launcher,
                        "coffee",
                        18,
                        Arrays.asList("Cold", "House Favorite"),
                        Collections.singletonList("Summer Specials"))
        );

        List<Product> teaProducts = Collections.singletonList(
                new Product(
                        "tea_1",
                        "Matcha Frappe",
                        "Vibrant matcha blended with milk and ice for a refreshing treat.",
                        4.59,
                        R.mipmap.ic_launcher,
                        "tea",
                        30,
                        Arrays.asList("Iced", "Limited"),
                        Arrays.asList("Matcha Month", "Green Goodness"))
        );

        List<Product> bakeryProducts = Collections.singletonList(
                new Product(
                        "bakery_1",
                        "Blueberry Muffin",
                        "Soft muffin with fresh blueberries and crunchy crumble topping.",
                        2.79,
                        R.mipmap.ic_launcher,
                        "bakery",
                        12,
                        Collections.singletonList("Breakfast"),
                        Collections.singletonList("Bakery Stars"))
        );

        productsByCategory.put("coffee", coffeeProducts);
        productsByCategory.put("tea", teaProducts);
        productsByCategory.put("bakery", bakeryProducts);
    }

    public List<Category> getCategories() {
        return new ArrayList<>(categories.values());
    }

    public List<Product> getProducts(String categoryId) {
        List<Product> products = productsByCategory.get(categoryId);
        if (products == null) {
            return Collections.emptyList();
        }
        return new ArrayList<>(products);
    }

    public Product getProductById(String productId) {
        for (List<Product> products : productsByCategory.values()) {
            for (Product product : products) {
                if (product.getId().equals(productId)) {
                    return product;
                }
            }
        }
        return null;
    }
}
