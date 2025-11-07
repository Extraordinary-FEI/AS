package com.example.cn.helloworld.ui.main;

import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.cn.helloworld.R;

import java.util.List;

/**
 * 首页分类入口的网格适配器。
 */
public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder> {

    private final List<HomeModels.HomeCategory> categories;

    public CategoryAdapter(List<HomeModels.HomeCategory> categories) {
        this.categories = categories;
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
    }

    @Override
    public int getItemCount() {
        return categories == null ? 0 : categories.size();
    }

    static class CategoryViewHolder extends RecyclerView.ViewHolder {
        final TextView nameView;

        CategoryViewHolder(View itemView) {
            super(itemView);
            nameView = (TextView) itemView.findViewById(R.id.category_name);


        }
    }
}
