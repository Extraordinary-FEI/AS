package com.example.cn.helloworld.ui.main;

import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.cn.helloworld.R;
import com.example.cn.helloworld.data.model.Product;

import java.util.List;
import java.util.Locale;

/**
 * 首页「应援好物」横向列表适配器。
 */
public class HomeProductAdapter extends RecyclerView.Adapter<HomeProductAdapter.ProductViewHolder> {

    interface OnProductClickListener {
        void onProductClick(Product product);
    }

    private final List<Product> products;
    private final OnProductClickListener clickListener;

    public HomeProductAdapter(List<Product> products, OnProductClickListener clickListener) {
        this.products = products;
        this.clickListener = clickListener;
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_home_product, parent, false);
        return new ProductViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        final Product product = products.get(position);
        holder.nameView.setText(product.getName());
        holder.descView.setText(product.getDescription());
        holder.priceView.setText(String.format(Locale.getDefault(), "¥%.2f", product.getPrice()));

        if (!TextUtils.isEmpty(product.getCoverUrl())) {
            holder.coverView.setImageURI(Uri.parse(product.getCoverUrl()));
        } else if (product.getImageResId() > 0) {
            holder.coverView.setImageResource(product.getImageResId());
        } else {
            holder.coverView.setImageResource(R.drawable.song_cover);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (clickListener != null) {
                    clickListener.onProductClick(product);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return products == null ? 0 : products.size();
    }

    Product getProductAt(int position) {
        if (products == null || position < 0 || position >= products.size()) {
            return null;
        }
        return products.get(position);
    }

    static class ProductViewHolder extends RecyclerView.ViewHolder {
        final ImageView coverView;
        final TextView nameView;
        final TextView descView;
        final TextView priceView;

        ProductViewHolder(View itemView) {
            super(itemView);
            coverView = (ImageView) itemView.findViewById(R.id.image_home_product);
            nameView = (TextView) itemView.findViewById(R.id.text_home_product_name);
            descView = (TextView) itemView.findViewById(R.id.text_home_product_desc);
            priceView = (TextView) itemView.findViewById(R.id.text_home_product_price);
        }
    }
}

