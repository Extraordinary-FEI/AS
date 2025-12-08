package com.example.cn.helloworld.ui.user;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.cn.helloworld.R;
import com.example.cn.helloworld.data.model.Product;

import java.util.List;
import java.util.Locale;

class FavoriteProductAdapter extends RecyclerView.Adapter<FavoriteProductAdapter.ViewHolder> {

    private final List<Product> products;
    private final FavoriteItemRemover<Product> remover;

    FavoriteProductAdapter(List<Product> products, FavoriteItemRemover<Product> remover) {
        this.products = products;
        this.remover = remover;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_favorite_simple, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        final Product product = products.get(position);
        holder.title.setText(product.getName());
        holder.subtitle.setText(String.format(Locale.getDefault(), "¥%.2f", product.getPrice()));
        holder.tag.setText(R.string.favorite_section_products);
        holder.icon.setImageResource(R.drawable.ic_category_merch);
        holder.removeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int adapterPosition = holder.getAdapterPosition();
                if (adapterPosition != RecyclerView.NO_POSITION && remover != null) {
                    remover.onRemove(product, adapterPosition);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return products == null ? 0 : products.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        final TextView title;
        final TextView subtitle;
        final TextView tag;
        final ImageView icon;
        final ImageButton removeButton;

        ViewHolder(View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.text_fav_title);
            subtitle = (TextView) itemView.findViewById(R.id.text_fav_subtitle);
            tag = (TextView) itemView.findViewById(R.id.text_fav_tag);
            icon = (ImageView) itemView.findViewById(R.id.image_fav_icon);
            removeButton = (ImageButton) itemView.findViewById(R.id.button_remove);
        }
    }
}

