package com.example.cn.helloworld.ui.catalog;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.cn.helloworld.R;
import com.example.cn.helloworld.data.model.CartItem;
import com.example.cn.helloworld.data.model.Product;
import com.example.cn.helloworld.data.model.ProductReview;
import com.example.cn.helloworld.data.repository.DatabaseReviewRepository;
import com.example.cn.helloworld.data.repository.ProductRepository;
import com.example.cn.helloworld.data.repository.ReviewSubmitCallback;
import com.example.cn.helloworld.data.storage.CartStorage;
import com.example.cn.helloworld.ui.product.ReviewWallActivity;

import java.util.Locale;
import java.util.Map;

public class ProductDetailActivity extends AppCompatActivity {

    private static final String EXTRA_PRODUCT_ID = "extra_product_id";

    private Product product;
    private DatabaseReviewRepository reviewRepository;
    private CartStorage cartStorage;
    private EditText commentEditText;
    private RatingBar ratingBar;
    private Button submitButton;
    private Button commentEntryButton;
    private Button addToCartButton;

    public static void start(Context context, String productId) {
        Intent intent = new Intent(context, ProductDetailActivity.class);
        intent.putExtra(EXTRA_PRODUCT_ID, productId);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_detail);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_product_detail);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(R.string.title_product_detail);
        }

        String productId = getIntent().getStringExtra(EXTRA_PRODUCT_ID);
        ProductRepository repository = new ProductRepository(this);
        product = repository.getProductById(productId);

        if (product == null) {
            finish();
            return;
        }

        reviewRepository = new DatabaseReviewRepository(this);
        cartStorage = CartStorage.getInstance(this);

        ImageView imageView = (ImageView) findViewById(R.id.detailProductImage);
        TextView nameView = (TextView) findViewById(R.id.detailProductName);
        TextView priceView = (TextView) findViewById(R.id.detailProductPrice);
        TextView inventoryView = (TextView) findViewById(R.id.detailProductInventory);
        TextView tagsView = (TextView) findViewById(R.id.detailProductTags);
        TextView starEventsView = (TextView) findViewById(R.id.detailProductStarEvents);
        TextView releaseView = (TextView) findViewById(R.id.detailProductReleaseTime);
        TextView limitView = (TextView) findViewById(R.id.detailProductLimited);
        TextView descriptionView = (TextView) findViewById(R.id.detailProductDescription);
        commentEditText = (EditText) findViewById(R.id.edit_comment_content);
        ratingBar = (RatingBar) findViewById(R.id.rating_bar);
        submitButton = (Button) findViewById(R.id.button_submit_comment);
        commentEntryButton = (Button) findViewById(R.id.button_open_comments);
        addToCartButton = (Button) findViewById(R.id.button_add_to_cart);
        TextView attributesView = (TextView) findViewById(R.id.detailProductAttributes);

        imageView.setImageResource(product.getImageResId());
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
        if (product.getCategoryAttributes().isEmpty()) {
            attributesView.setVisibility(View.GONE);
        } else {
            attributesView.setText(getString(R.string.product_attributes_format, formatAttributes((Map<String, String>) product.getCategoryAttributes())));
            attributesView.setVisibility(View.VISIBLE);
        }
        descriptionView.setText(product.getDescription());

        commentEntryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(ReviewWallActivity.createIntent(ProductDetailActivity.this, product.getId(), product.getName()));
            }
        });

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitReview();
            }
        });

        addToCartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addProductToCart();
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void submitReview() {
        String content = commentEditText.getText().toString();
        if (TextUtils.isEmpty(content)) {
            commentEditText.setError(getString(R.string.error_empty_review));
            return;
        }
        float rating = ratingBar.getRating();
        ProductReview review = new ProductReview(product.getId(), "千纸鹤小分队", content, rating);
        reviewRepository.submitReview(review, new ReviewSubmitCallback() {
            @Override
            public void onSuccess(ProductReview review) {
                Toast.makeText(ProductDetailActivity.this, R.string.review_submitted,
                        Toast.LENGTH_SHORT).show();
                commentEditText.setText("");
                ratingBar.setRating(0f);
            }

            @Override
            public void onError(Throwable throwable) {
                Toast.makeText(ProductDetailActivity.this, R.string.review_submit_error,
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void addProductToCart() {
        if (product == null) {
            return;
        }
        CartItem item = new CartItem(product.getId(), product.getName(), product.getPrice(), 1,
                product.getCoverUrl(), true);
        cartStorage.addOrIncrease(item);
        Toast.makeText(this, R.string.cart_added_success, Toast.LENGTH_SHORT).show();
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
