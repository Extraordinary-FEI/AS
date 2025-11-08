package com.example.cn.helloworld.ui.product;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.cn.helloworld.R;
import com.example.cn.helloworld.data.model.ProductReview;
import com.example.cn.helloworld.data.repository.InMemoryReviewRepository;
import com.example.cn.helloworld.data.repository.ReviewSubmitCallback;

import java.util.List;

public class ProductDetailActivity extends AppCompatActivity {

    public static final String EXTRA_PRODUCT_ID = "extra_product_id";
    public static final String EXTRA_PRODUCT_NAME = "extra_product_name";

    private TextView productNameTextView;
    private TextView productDescriptionTextView;
    private ImageButton favoriteButton;
    private Button commentEntryButton;
    private Button submitButton;
    private EditText commentEditText;
    private RatingBar ratingBar;

    private InMemoryReviewRepository reviewRepository;
    private boolean isFavorite;
    private String productId;
    private String productName;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_detail);

        productNameTextView = (TextView) findViewById(R.id.text_product_name);
        productDescriptionTextView = (TextView) findViewById(R.id.text_product_description);
        favoriteButton = (ImageButton) findViewById(R.id.button_favorite);
        commentEntryButton = (Button) findViewById(R.id.button_open_comments);
        submitButton = (Button) findViewById(R.id.button_submit_comment);
        commentEditText = (EditText) findViewById(R.id.edit_comment_content);
        ratingBar = (RatingBar) findViewById(R.id.rating_bar);

        reviewRepository = new InMemoryReviewRepository();

        Intent intent = getIntent();
        if (intent != null) {
            productId = intent.getStringExtra(EXTRA_PRODUCT_ID);
            productName = intent.getStringExtra(EXTRA_PRODUCT_NAME);
        }
        if (TextUtils.isEmpty(productId)) {
            productId = "P1001";
        }
        if (TextUtils.isEmpty(productName)) {
            productName = getString(R.string.default_product_name);
        }

        productNameTextView.setText(productName);
        productDescriptionTextView.setText(getString(R.string.default_product_description, productName));

        loadFavoriteState();
        updateFavoriteIcon();

        favoriteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleFavorite();
            }
        });

        commentEntryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showExistingComments();
            }
        });

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitReview();
            }
        });
    }

    private void loadFavoriteState() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        isFavorite = preferences.getBoolean(getFavoriteKey(), false);
    }

    private void saveFavoriteState() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        preferences.edit().putBoolean(getFavoriteKey(), isFavorite).apply();
    }

    private String getFavoriteKey() {
        return "favorite_" + productId;
    }

    private void toggleFavorite() {
        isFavorite = !isFavorite;
        updateFavoriteIcon();
        saveFavoriteState();
        Toast.makeText(this,
                isFavorite ? R.string.favorite_added : R.string.favorite_removed,
                Toast.LENGTH_SHORT).show();
    }

    private void updateFavoriteIcon() {
        favoriteButton.setImageResource(isFavorite ? android.R.drawable.btn_star_big_on
                : android.R.drawable.btn_star_big_off);
        favoriteButton.setContentDescription(getString(isFavorite
                ? R.string.favorite_added
                : R.string.favorite_removed));
    }

    private void showExistingComments() {
        List<ProductReview> reviews = reviewRepository.getReviewsForProduct(productId);
        StringBuilder builder = new StringBuilder();
        if (reviews.isEmpty()) {
            builder.append(getString(R.string.no_reviews_yet));
        } else {
            for (int i = 0; i < reviews.size(); i++) {
                ProductReview review = reviews.get(i);
                builder.append(review.getUserName()).append(": ")
                        .append(review.getContent()).append(" (")
                        .append(review.getRating()).append(")\n");
            }
        }

        new AlertDialog.Builder(this)
                .setTitle(R.string.dialog_reviews_title)
                .setMessage(builder.toString())
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .show();
    }

    private void submitReview() {
        String content = commentEditText.getText().toString();
        if (TextUtils.isEmpty(content)) {
            commentEditText.setError(getString(R.string.error_empty_review));
            return;
        }
        float rating = ratingBar.getRating();
        ProductReview review = new ProductReview(productId, "匿名粉丝", content, rating);
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
}
