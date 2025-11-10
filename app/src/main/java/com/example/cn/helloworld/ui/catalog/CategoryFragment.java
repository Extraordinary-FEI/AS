package com.example.cn.helloworld.ui.catalog;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


import com.example.cn.helloworld.R;
import com.example.cn.helloworld.data.model.Category;
import com.example.cn.helloworld.data.repository.ProductRepository;

import java.util.ArrayList;
import java.util.List;

public class CategoryFragment extends android.app.Fragment {

    public interface OnCategorySelectedListener {
        void onCategorySelected(Category category);
    }

    private View recyclerView;
    private CategoryAdapter adapter;
    private ProductRepository productRepository;
    private OnCategorySelectedListener listener;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof OnCategorySelectedListener) {
            listener = (OnCategorySelectedListener) context;
        }
        productRepository = new ProductRepository(context);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_category, container, false);
        recyclerView = (RecyclerView) view.findViewById(R.id.categoryRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new CategoryAdapter(new ArrayList<Category>(), new CategoryAdapter.OnCategoryClickListener() {
            @Override
            public void onCategoryClick(Category category) {
                if (listener != null) {
                    listener.onCategorySelected(category);
                }
            }
        });
        recyclerView.setAdapter(adapter);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (productRepository != null) {
            adapter.updateCategories(productRepository.getCategories());
        }
    }

    private static class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder> {

        interface OnCategoryClickListener {
            void onCategoryClick(Category category);
        }

        private final List<Category> categories;
        private final OnCategoryClickListener clickListener;

        CategoryAdapter(List<Category> categories, OnCategoryClickListener clickListener) {
            this.categories = categories;
            this.clickListener = clickListener;
        }

        void updateCategories(List<Category> newCategories) {
            categories.clear();
            categories.addAll(newCategories);
            notifyDataSetChanged();
        }

        @NonNull
        @Override
        public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_category, parent, false);
            return new CategoryViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(@NonNull CategoryViewHolder holder, int position) {
            Category category = categories.get(position);
            holder.bind(category, clickListener);
        }

        @Override
        public int getItemCount() {
            return categories.size();
        }

        static class CategoryViewHolder extends RecyclerView.ViewHolder {
            private final TextView nameView;
            private final ImageView iconView;

            CategoryViewHolder(@NonNull View itemView) {
                super(itemView);
                nameView = itemView.findViewById(R.id.categoryName);
                iconView = itemView.findViewById(R.id.categoryIcon);
            }

            void bind(final Category category, final OnCategoryClickListener listener) {
                nameView.setText(category.getName());
                iconView.setImageResource(category.getIconResId());
                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (listener != null) {
                            listener.onCategoryClick(category);
                        }
                    }
                });
            }
        }
    }
}
