package com.example.cn.helloworld.ui.catalog;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
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

/**
 * 展示全部应援好物的列表页，供首页「全部好物」跳转使用。
 */
public class ProductListActivity extends AppCompatActivity {

    public static void start(Context context) {
        Intent intent = new Intent(context, ProductListActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_list);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_product_list);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(R.string.title_product_list);
        }

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycler_all_products);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        ProductRepository repository = new ProductRepository(this);
        List<Product> activeProducts = new ArrayList<Product>();
        for (Product product : repository.getAll()) {
            if (product != null && product.isActive()) {
                activeProducts.add(product);
            }
        }

        recyclerView.setAdapter(new ProductListAdapter(activeProducts));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private static class ProductListAdapter extends RecyclerView.Adapter<ProductListAdapter.ProductViewHolder> {

        private final List<Product> products;

        ProductListAdapter(List<Product> products) {
            this.products = products;
        }

        @NonNull
        @Override
        public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_product, parent, false);
            return new ProductViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
            final Product product = products.get(position);
            holder.nameView.setText(product.getName());
            holder.priceView.setText(String.format(Locale.getDefault(), "¥%.2f", product.getPrice()));
            if (product.getImageResId() > 0) {
                holder.imageView.setImageResource(product.getImageResId());
            } else {
                holder.imageView.setImageResource(R.drawable.song_cover);
            }

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Context context = v.getContext();
                    if (context != null) {
                        ProductDetailActivity.start(context, product.getId());
                    }
                }
            });
        }

        @Override
        public int getItemCount() {
            return products == null ? 0 : products.size();
        }

        static class ProductViewHolder extends RecyclerView.ViewHolder {
            final ImageView imageView;
            final TextView nameView;
            final TextView priceView;

            ProductViewHolder(View itemView) {
                super(itemView);
                imageView = (ImageView) itemView.findViewById(R.id.productImage);
                nameView = (TextView) itemView.findViewById(R.id.productName);
                priceView = (TextView) itemView.findViewById(R.id.productPrice);
            }
        }
    }
}

