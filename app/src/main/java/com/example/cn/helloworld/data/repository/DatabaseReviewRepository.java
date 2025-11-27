package com.example.cn.helloworld.data.repository;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;

import com.example.cn.helloworld.DBHelper;
import com.example.cn.helloworld.data.model.ProductReview;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 基于 SQLite 的评论仓库，支持跨页面持久化。
 */
public class DatabaseReviewRepository implements ReviewSubmitter {

    private final DBHelper dbHelper;

    public DatabaseReviewRepository(Context context) {
        dbHelper = new DBHelper(context.getApplicationContext());
    }

    public List<ProductReview> getReviewsForProduct(String productId) {
        if (TextUtils.isEmpty(productId)) {
            return Collections.emptyList();
        }
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = null;
        List<ProductReview> reviews = new ArrayList<ProductReview>();
        try {
            cursor = db.query(DBHelper.T_REVIEW,
                    null,
                    DBHelper.C_PRODUCT_ID + "=?",
                    new String[]{productId},
                    null,
                    null,
                    DBHelper.C_REVIEW_TIME + " DESC");
            while (cursor != null && cursor.moveToNext()) {
                reviews.add(fromCursor(cursor));
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return reviews;
    }

    public List<ProductReview> getAllReviews() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = null;
        List<ProductReview> reviews = new ArrayList<ProductReview>();
        try {
            cursor = db.query(DBHelper.T_REVIEW,
                    null,
                    null,
                    null,
                    null,
                    null,
                    DBHelper.C_REVIEW_TIME + " DESC");
            while (cursor != null && cursor.moveToNext()) {
                reviews.add(fromCursor(cursor));
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return reviews;
    }

    @Override
    public void submitReview(ProductReview review, ReviewSubmitCallback callback) {
        try {
            insert(review);
            if (callback != null) {
                callback.onSuccess(review);
            }
        } catch (Exception e) {
            if (callback != null) {
                callback.onError(e);
            }
        }
    }

    private void insert(ProductReview review) {
        if (review == null) {
            return;
        }
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DBHelper.C_PRODUCT_ID, review.getProductId());
        values.put(DBHelper.C_USER_NAME, review.getUserName());
        values.put(DBHelper.C_CONTENT, review.getContent());
        values.put(DBHelper.C_RATING, review.getRating());
        values.put(DBHelper.C_REVIEW_TIME, review.getCreatedAt());
        db.insert(DBHelper.T_REVIEW, null, values);
    }

    private ProductReview fromCursor(Cursor cursor) {
        String productId = cursor.getString(cursor.getColumnIndex(DBHelper.C_PRODUCT_ID));
        String userName = cursor.getString(cursor.getColumnIndex(DBHelper.C_USER_NAME));
        String content = cursor.getString(cursor.getColumnIndex(DBHelper.C_CONTENT));
        float rating = cursor.getFloat(cursor.getColumnIndex(DBHelper.C_RATING));
        long createdAt = cursor.getLong(cursor.getColumnIndex(DBHelper.C_REVIEW_TIME));
        return new ProductReview(productId, userName, content, rating, createdAt);
    }
}

