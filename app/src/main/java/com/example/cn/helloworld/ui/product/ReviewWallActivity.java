package com.example.cn.helloworld.ui.product;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.example.cn.helloworld.R;
import com.example.cn.helloworld.data.model.Product;
import com.example.cn.helloworld.data.model.ProductReview;
import com.example.cn.helloworld.data.repository.DatabaseReviewRepository;
import com.example.cn.helloworld.data.repository.ProductRepository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ReviewWallActivity extends AppCompatActivity {

    private static final String EXTRA_PRODUCT_ID = "extra_product_id";
    private static final String EXTRA_PRODUCT_NAME = "extra_product_name";

    public static Intent createIntent(Context context, String productId, String productName) {
        Intent intent = new Intent(context, ReviewWallActivity.class);
        intent.putExtra(EXTRA_PRODUCT_ID, productId);
        intent.putExtra(EXTRA_PRODUCT_NAME, productName);
        return intent;
    }

    private RecyclerView recyclerView;
    private TextView emptyView;
    private TextView filterView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review_wall);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_review);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(R.string.title_review_wall);
        }
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        recyclerView = (RecyclerView) findViewById(R.id.recycler_reviews);
        emptyView = (TextView) findViewById(R.id.text_review_empty);
        filterView = (TextView) findViewById(R.id.text_review_filter);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        DatabaseReviewRepository reviewRepository = new DatabaseReviewRepository(this);
        ProductRepository productRepository = new ProductRepository(this);

        String productId = getIntent().getStringExtra(EXTRA_PRODUCT_ID);
        String productName = getIntent().getStringExtra(EXTRA_PRODUCT_NAME);

        List<ProductReview> reviews;
        if (!TextUtils.isEmpty(productId)) {
            reviews = reviewRepository.getReviewsForProduct(productId);
        } else {
            reviews = reviewRepository.getAllReviews();
        }

        Map<String, String> productNames = new HashMap<String, String>();
        List<Product> allProducts = productRepository.getAll();
        for (int i = 0; i < allProducts.size(); i++) {
            Product product = allProducts.get(i);
            if (product != null) {
                productNames.put(product.getId(), product.getName());
            }
        }

        if (!TextUtils.isEmpty(productName)) {
            filterView.setText(getString(R.string.review_wall_filter_format, productName));
        } else {
            filterView.setText(getString(R.string.dialog_reviews_title));
        }

        if (reviews.isEmpty()) {
            emptyView.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        } else {
            emptyView.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
            recyclerView.setAdapter(new ReviewAdapter(new ArrayList<ProductReview>(reviews), productNames));
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}

