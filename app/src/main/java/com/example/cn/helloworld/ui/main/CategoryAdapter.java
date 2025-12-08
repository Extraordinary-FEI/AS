package com.example.cn.helloworld.ui.main;

import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
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
        holder.decorateStageEntry(category);
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
        final TextView badgeView;
        final View iconContainer;

        CategoryViewHolder(View itemView) {
            super(itemView);
            nameView = (TextView) itemView.findViewById(R.id.categoryName);
            subtitleView = (TextView) itemView.findViewById(R.id.categorySubtitle);
            iconView = (ImageView) itemView.findViewById(R.id.categoryIcon);
            badgeView = (TextView) itemView.findViewById(R.id.categoryBadge);
            iconContainer = itemView.findViewById(R.id.categoryIconContainer);
        }

        void decorateStageEntry(HomeModels.HomeCategory category) {
            boolean isStageReview = category != null
                    && "action_stage_review".equals(category.getAction());

            if (badgeView != null) {
                badgeView.setVisibility(isStageReview ? View.VISIBLE : View.GONE);
            }

            if (iconContainer != null) {
                int tint = pickTint(iconContainer, category == null ? null : category.getAction());
                Drawable bg = DrawableCompat.wrap(iconContainer.getBackground().mutate());
                DrawableCompat.setTint(bg, tint);
                iconContainer.setBackground(bg);
            }
        }

        private int pickTint(View iconContainer, String action) {
            if (iconContainer == null) {
                return 0xFFE5E2FF; // fallback
            }
            if ("action_stage_review".equals(action)) {
                return ContextCompat.getColor(iconContainer.getContext(), R.color.quickEntryStage);
            }
            if ("action_new_arrival".equals(action)) {
                return ContextCompat.getColor(iconContainer.getContext(), R.color.quickEntryMerch);
            }
            if ("action_calendar".equals(action)) {
                return ContextCompat.getColor(iconContainer.getContext(), R.color.quickEntrySupport);
            }
            if ("action_review_wall".equals(action)) {
                return ContextCompat.getColor(iconContainer.getContext(), R.color.quickEntryPlaylist);
            }
            if ("action_news".equals(action)) {
                return ContextCompat.getColor(iconContainer.getContext(), R.color.quickEntryNews);
            }
            if ("action_profile".equals(action)) {
                return ContextCompat.getColor(iconContainer.getContext(), R.color.quickEntryProfile);
            }
            return ContextCompat.getColor(iconContainer.getContext(), R.color.homeCategoryBackground);
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
