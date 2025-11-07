package com.example.cn.helloworld.ui.catalog;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;



import com.example.cn.helloworld.R;
import com.example.cn.helloworld.data.model.Product;
import com.example.cn.helloworld.data.repository.ProductRepository;

import java.util.Locale;

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
        ProductRepository repository = new ProductRepository(this);
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
        TextView descriptionView = (TextView) findViewById(R.id.detailProductDescription);

        imageView.setImageResource(product.getImageResId());
        nameView.setText(product.getName());
        priceView.setText(String.format(Locale.getDefault(), "$%.2f", product.getPrice()));
        inventoryView.setText(getString(R.string.product_inventory_format, product.getInventory()));
        tagsView.setText(getString(R.string.product_tags_format, joinWithComma(product.getTags())));
        starEventsView.setText(getString(R.string.product_star_events_format, joinWithComma(product.getStarEvents())));
        descriptionView.setText(product.getDescription());
    }

    private String joinWithComma(Iterable<String> values) {
        StringBuilder builder = new StringBuilder();
        for (String value : values) {
            if (builder.length() > 0) {
                builder.append(", ");
            }
            builder.append(value);
        }
        return builder.toString();
    }
}
