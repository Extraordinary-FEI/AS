package com.example.cn.helloworld.data.repository;

import com.example.cn.helloworld.data.model.ProductReview;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Simple in-memory review repository for demo purposes.
 */
public class InMemoryReviewRepository implements ReviewSubmitter {

    private final List<ProductReview> reviews = new ArrayList<ProductReview>();

    public List<ProductReview> getReviewsForProduct(String productId) {
        List<ProductReview> filtered = new ArrayList<ProductReview>();
        for (int i = 0; i < reviews.size(); i++) {
            ProductReview review = reviews.get(i);
            if (review != null && productId.equals(review.getProductId())) {
                filtered.add(review);
            }
        }
        return Collections.unmodifiableList(filtered);
    }

    public void clear() {
        reviews.clear();
    }

    public List<ProductReview> getAllReviews() {
        return Collections.unmodifiableList(reviews);
    }

    public void addReview(ProductReview review) {
        if (review != null) {
            reviews.add(review);
        }
    }

    public void submitReview(final ProductReview review, final ReviewSubmitCallback callback) {
        try {
            addReview(review);
            if (callback != null) {
                callback.onSuccess(review);
            }
        } catch (Exception e) {
            if (callback != null) {
                callback.onError(e);
            }
        }
    }
}
