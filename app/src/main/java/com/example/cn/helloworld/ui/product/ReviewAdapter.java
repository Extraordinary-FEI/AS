package com.example.cn.helloworld.ui.product;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import com.example.cn.helloworld.R;
import com.example.cn.helloworld.data.model.ProductReview;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ReviewViewHolder> {

    private final List<ProductReview> reviews;
    private final Map<String, String> productNames;
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("MM-dd HH:mm", Locale.getDefault());

    public ReviewAdapter(List<ProductReview> reviews, Map<String, String> productNames) {
        this.reviews = reviews;
        this.productNames = productNames;
    }

    @Override
    public ReviewViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_review, parent, false);
        return new ReviewViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ReviewViewHolder holder, int position) {
        holder.bind(reviews.get(position), productNames, dateFormat);
    }

    @Override
    public int getItemCount() {
        return reviews == null ? 0 : reviews.size();
    }

    static class ReviewViewHolder extends RecyclerView.ViewHolder {

        private final TextView userNameView;
        private final TextView productNameView;
        private final TextView timeView;
        private final TextView contentView;
        private final RatingBar ratingBar;

        ReviewViewHolder(View itemView) {
            super(itemView);
            userNameView = (TextView) itemView.findViewById(R.id.text_review_user);
            productNameView = (TextView) itemView.findViewById(R.id.text_review_product);
            timeView = (TextView) itemView.findViewById(R.id.text_review_time);
            contentView = (TextView) itemView.findViewById(R.id.text_review_content);
            ratingBar = (RatingBar) itemView.findViewById(R.id.rating_review);
        }

        void bind(ProductReview review, Map<String, String> productNames, SimpleDateFormat dateFormat) {
            userNameView.setText(review.getUserName());
            String productName = productNames != null ? productNames.get(review.getProductId()) : null;
            productNameView.setText(productName != null ? productName : review.getProductId());
            ratingBar.setRating(review.getRating());
            contentView.setText(review.getContent());
            timeView.setText(dateFormat.format(new Date(review.getCreatedAt())));
        }
    }
}

