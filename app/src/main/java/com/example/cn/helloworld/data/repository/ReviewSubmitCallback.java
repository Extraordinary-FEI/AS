package com.example.cn.helloworld.data.repository;

import com.example.cn.helloworld.data.model.ProductReview;

public interface ReviewSubmitCallback {
    void onSuccess(ProductReview review);

    void onError(Throwable throwable);
}
