package com.example.cn.helloworld.ui.catalog;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.cn.helloworld.R;
import com.example.cn.helloworld.data.model.Product;
import com.example.cn.helloworld.data.repository.ProductRepository;

import java.util.Locale;
import java.util.Map;

public class ProductDetailActivity extends AppCompatActivity {

    private static final String EXTRA_PRODUCT_ID = "extra_product_id";

    public static void start(Context context, String productId) {
        Intent intent = new Intent(context, ProductDetailActivity.class);
        intent.putExtra(EXTRA_PRODUCT_ID, productId);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_detail);

        String productId = getIntent().getStringExtra(EXTRA_PRODUCT_ID);
        ProductRepository repository = ProductRepository.getInstance(this);
        Product product = repository.getProductById(productId);

        if (product == null) {
            finish();
            return;
        }

        ImageView imageView = (ImageView) findViewById(R.id.detailProductImage);
        TextView nameView = (TextView) findViewById(R.id.detailProductName);
        TextView priceView = (TextView) findViewById(R.id.detailProductPrice);
        TextView inventoryView = (TextView) findViewById(R.id.detailProductInventory);
        TextView tagsView = (TextView) findViewById(R.id.detailProductTags);
        TextView starEventsView = (TextView) findViewById(R.id.detailProductStarEvents);
        TextView releaseView = (TextView) findViewById(R.id.detailProductReleaseTime);
        TextView limitView = (TextView) findViewById(R.id.detailProductLimited);
        TextView descriptionView = (TextView) findViewById(R.id.detailProductDescription);
        TextView attributesView = (TextView) findViewById(R.id.detailProductAttributes);

        int imageResId = product.getImageResId();
        if (imageResId != 0) {
            imageView.setImageResource(imageResId);
        } else {
            imageView.setImageResource(R.mipmap.ic_launcher);
        }
        nameView.setText(product.getName());
        priceView.setText(String.format(Locale.getDefault(), "¥%.2f", product.getPrice()));
        inventoryView.setText(getString(R.string.product_inventory_format, product.getInventory()));
        tagsView.setText(getString(R.string.product_tags_format, joinWithSeparator(product.getTags())));
        starEventsView.setText(getString(R.string.product_star_events_format, joinWithSeparator(product.getStarEvents())));
        if (!TextUtils.isEmpty(product.getReleaseTime())) {
            releaseView.setText(getString(R.string.product_release_time_format, product.getReleaseTime()));
            releaseView.setVisibility(View.VISIBLE);
        } else {
            releaseView.setVisibility(View.GONE);
        }
        if (!TextUtils.isEmpty(product.getLimitedQuantity())) {
            limitView.setText(getString(R.string.product_limit_format, product.getLimitedQuantity()));
            limitView.setVisibility(View.VISIBLE);
        } else {
            limitView.setVisibility(View.GONE);
        }
        Map<String, String> categoryAttributes = product.getCategoryAttributes();
        if (categoryAttributes == null || categoryAttributes.isEmpty()) {
            attributesView.setVisibility(View.GONE);
        } else {
            attributesView.setText(getString(R.string.product_attributes_format, formatAttributes(categoryAttributes)));
            attributesView.setVisibility(View.VISIBLE);
        }
        descriptionView.setText(product.getDescription());
    }

    private String joinWithSeparator(Iterable<String> values) {
        StringBuilder builder = new StringBuilder();
        for (String value : values) {
            if (builder.length() > 0) {
                builder.append("、");
            }
            builder.append(value);
        }
        return builder.toString();
    }

    private String formatAttributes(Map<String, String> attributes) {
        StringBuilder builder = new StringBuilder();
        for (Map.Entry<String, String> entry : attributes.entrySet()) {
            if (builder.length() > 0) {
                builder.append("\n");
            }
            builder.append(entry.getKey()).append("：").append(entry.getValue());
        }
        return builder.toString();
    }
}
