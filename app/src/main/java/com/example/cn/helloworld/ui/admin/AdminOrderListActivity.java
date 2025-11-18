package com.example.cn.helloworld.ui.admin;

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
import android.widget.TextView;

import com.example.cn.helloworld.R;
import com.example.cn.helloworld.data.model.Order;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class AdminOrderListActivity extends AppCompatActivity {

    public static final String EXTRA_ORDERS = "extra_orders";

    public static Intent createIntent(Context context, ArrayList<Order> orders) {
        Intent intent = new Intent(context, AdminOrderListActivity.class);
        intent.putExtra(EXTRA_ORDERS, orders);
        return intent;
    }

    private RecyclerView recyclerView;
    private TextView emptyView;
    private OrderAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_order_list);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(R.string.title_admin_order_list);
        }

        recyclerView = (RecyclerView) findViewById(R.id.recyclerOrders);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new OrderAdapter();
        recyclerView.setAdapter(adapter);

        emptyView = (TextView) findViewById(R.id.textEmptyOrders);

        ArrayList<Order> orders = (ArrayList<Order>) getIntent().getSerializableExtra(EXTRA_ORDERS);
        if (orders == null) {
            orders = new ArrayList<>();
        }
        submitOrders(orders);
    }

    private void submitOrders(List<Order> orders) {
        adapter.submit(orders);
        boolean isEmpty = orders == null || orders.isEmpty();
        emptyView.setVisibility(isEmpty ? View.VISIBLE : View.GONE);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    private static class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.ViewHolder> {

        private final List<Order> orders = new ArrayList<>();
        private final SimpleDateFormat formatter = new SimpleDateFormat("MM-dd HH:mm", Locale.getDefault());

        void submit(List<Order> list) {
            orders.clear();
            if (list != null) {
                orders.addAll(list);
            }
            notifyDataSetChanged();
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_admin_order, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            Order order = orders.get(position);
            holder.bind(order, formatter);
        }

        @Override
        public int getItemCount() {
            return orders.size();
        }

        static class ViewHolder extends RecyclerView.ViewHolder {

            private final TextView orderIdView;
            private final TextView statusView;
            private final TextView amountView;
            private final TextView timeView;

            ViewHolder(View itemView) {
                super(itemView);
                orderIdView = (TextView) itemView.findViewById(R.id.textOrderId);
                statusView = (TextView) itemView.findViewById(R.id.textOrderStatus);
                amountView = (TextView) itemView.findViewById(R.id.textOrderAmount);
                timeView = (TextView) itemView.findViewById(R.id.textOrderTime);
            }

            void bind(Order order, SimpleDateFormat formatter) {
                Context context = itemView.getContext();
                orderIdView.setText(context.getString(R.string.order_id_template, order.getOrderId()));
                statusView.setText(context.getString(R.string.order_status_template, order.getStatus()));
                amountView.setText(context.getString(R.string.order_total_template,
                        String.format(Locale.getDefault(), "%.2f", order.getTotalAmount())));
                Date date = new Date(order.getCreatedAt());
                timeView.setText(context.getString(R.string.order_created_template, formatter.format(date)));
            }
        }
    }
}
