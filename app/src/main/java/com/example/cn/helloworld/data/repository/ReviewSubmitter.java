package com.example.cn.helloworld.data.repository;

import com.example.cn.helloworld.data.model.ProductReview;

public interface ReviewSubmitter {
    void submitReview(ProductReview review, ReviewSubmitCallback callback);
}
