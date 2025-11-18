package com.example.cn.helloworld.ui.catalog;

import android.support.v4.app.Fragment;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.cn.helloworld.R;
import com.example.cn.helloworld.data.model.Product;
import com.example.cn.helloworld.data.repository.ProductRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@RequiresApi(api = Build.VERSION_CODES.HONEYCOMB)
public class ProductListFragment extends android.support.v4.app.Fragment {

    private static final String ARG_CATEGORY_ID = "arg_category_id";

    private RecyclerView recyclerView;
    private ProductAdapter adapter;
    private ProductRepository productRepository;
    private String categoryId;

    public static ProductListFragment newInstance(String categoryId) {
        ProductListFragment fragment = new ProductListFragment();
        Bundle args = new Bundle();
        args.putString(ARG_CATEGORY_ID, categoryId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Context context = getActivity();
        if (context != null) {
            productRepository = ProductRepository.getInstance(context);
        }
        if (getArguments() != null) {
            categoryId = getArguments().getString(ARG_CATEGORY_ID);
        }
    }



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_product_list, container, false);
        recyclerView = (RecyclerView) view.findViewById(R.id.productRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new ProductAdapter(new ArrayList<Product>(), new ProductAdapter.OnProductClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onProductClick(Product product) {
                Context context = getContext();
                if (context != null) {
                    ProductDetailActivity.start(context, product.getId());
                }
            }
        });
        recyclerView.setAdapter(adapter);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (categoryId != null && productRepository != null) {
            adapter.updateProducts(productRepository.getProducts(categoryId));
        }
    }

    private static class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> {

        interface OnProductClickListener {
            void onProductClick(Product product);
        }

        private final List<Product> products;
        private final OnProductClickListener clickListener;

        ProductAdapter(List<Product> products, OnProductClickListener clickListener) {
            this.products = products;
            this.clickListener = clickListener;
        }

        void updateProducts(List<Product> newProducts) {
            products.clear();
            products.addAll(newProducts);
            notifyDataSetChanged();
        }

        @NonNull
        @Override
        public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_product, parent, false);
            return new ProductViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
            Product product = products.get(position);
            holder.bind(product, clickListener);
        }

        @Override
        public int getItemCount() {
            return products.size();
        }

        static class ProductViewHolder extends RecyclerView.ViewHolder {
            private final ImageView imageView;
            private final TextView nameView;
            private final TextView priceView;

            ProductViewHolder(@NonNull View itemView) {
                super(itemView);
                imageView = (ImageView) itemView.findViewById(R.id.productImage);
                nameView = (TextView) itemView.findViewById(R.id.productName);
                priceView = (TextView) itemView.findViewById(R.id.productPrice);
            }

            void bind(final Product product, final OnProductClickListener listener) {
                nameView.setText(product.getName());
                priceView.setText(String.format(Locale.getDefault(), "¥%.2f", product.getPrice()));
                imageView.setImageResource(product.getImageResId());
                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (listener != null) {
                            listener.onProductClick(product);
                        }
                    }
                });
            }
        }
    }
}
