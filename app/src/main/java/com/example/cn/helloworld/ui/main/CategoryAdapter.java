package com.example.cn.helloworld.ui.main;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.cn.helloworld.R;

import java.util.List;

/**
 * 首页分类入口的网格适配器。
 */
public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder> {

    private final List<HomeModels.HomeCategory> categories;
    private final OnCategoryClickListener clickListener;

    public interface OnCategoryClickListener {
        void onCategoryClick(HomeModels.HomeCategory category);
    }

    public CategoryAdapter(List<HomeModels.HomeCategory> categories,
                           OnCategoryClickListener clickListener) {
        this.categories = categories;
        this.clickListener = clickListener;
    }

    @Override
    public CategoryViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_category, parent, false);
        return new CategoryViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(CategoryViewHolder holder, int position) {
        HomeModels.HomeCategory category = categories.get(position);
        holder.nameView.setText(category.getName());
        holder.subtitleView.setText(category.getSubtitle());
        holder.iconView.setImageResource(category.getIconResId());
        holder.bind(category, clickListener);
    }

    @Override
    public int getItemCount() {
        return categories == null ? 0 : categories.size();
    }

    static class CategoryViewHolder extends RecyclerView.ViewHolder {
        final TextView nameView;
        final TextView subtitleView;
        final ImageView iconView;

        CategoryViewHolder(View itemView) {
            super(itemView);
            nameView = (TextView) itemView.findViewById(R.id.categoryName);
            subtitleView = (TextView) itemView.findViewById(R.id.categorySubtitle);
            iconView = (ImageView) itemView.findViewById(R.id.categoryIcon);
        }

        void bind(final HomeModels.HomeCategory category,
                  final OnCategoryClickListener clickListener) {
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (clickListener != null) {
                        clickListener.onCategoryClick(category);
                    }
                }
            });
        }
    }
}
