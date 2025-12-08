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
        holder.decorateBadge(category);
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

        CategoryViewHolder(View itemView) {
            super(itemView);
            nameView = (TextView) itemView.findViewById(R.id.categoryName);
            subtitleView = (TextView) itemView.findViewById(R.id.categorySubtitle);
            iconView = (ImageView) itemView.findViewById(R.id.categoryIcon);
            badgeView = (TextView) itemView.findViewById(R.id.categoryBadge);
        }

        void decorateBadge(HomeModels.HomeCategory category) {
            if (badgeView == null) {
                return;
            }

            String action = category == null ? null : category.getAction();
            String label = resolveBadgeLabel(action);
            if (label == null) {
                badgeView.setVisibility(View.GONE);
                return;
            }

            Drawable badgeBackground = DrawableCompat.wrap(badgeView.getBackground().mutate());
            int tint = pickTint(badgeView, action);
            DrawableCompat.setTint(badgeBackground, tint);
            badgeView.setBackground(badgeBackground);
            badgeView.setText(label);
            badgeView.setVisibility(View.VISIBLE);
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

        private String resolveBadgeLabel(String action) {
            if ("action_stage_review".equals(action)) {
                return "舞台";
            }
            if ("action_new_arrival".equals(action)) {
                return "新品";
            }
            if ("action_calendar".equals(action)) {
                return "日程";
            }
            if ("action_review_wall".equals(action)) {
                return "歌单";
            }
            if ("action_news".equals(action)) {
                return "资讯";
            }
            if ("action_profile".equals(action)) {
                return "档案";
            }
            return null;
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
