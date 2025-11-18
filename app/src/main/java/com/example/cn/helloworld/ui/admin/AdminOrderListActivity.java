package com.example.cn.helloworld.ui.admin;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.cn.helloworld.R;
import com.example.cn.helloworld.data.model.Order;
import com.example.cn.helloworld.data.repository.AdminMetricsRepository;
import com.example.cn.helloworld.data.session.SessionManager;
import com.example.cn.helloworld.ui.order.OrderDetailActivity;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class AdminOrderListActivity extends AppCompatActivity {

    private SessionManager sessionManager;
    private AdminMetricsRepository metricsRepository;
    private OrderAdapter adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_orders);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(R.string.title_admin_orders);
        }

        sessionManager = new SessionManager(this);
        if (!sessionManager.isAdmin()) {
            Toast.makeText(this, R.string.admin_access_denied, Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        metricsRepository = AdminMetricsRepository.getInstance(this);

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recyclerAdminOrders);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new OrderAdapter(new ArrayList<Order>(), new OrderAdapter.Listener() {
            @Override
            public void onView(Order order) {
                OrderDetailActivity.start(AdminOrderListActivity.this, order);
            }
        });
        recyclerView.setAdapter(adapter);

        loadOrders();
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    private void loadOrders() {
        adapter.submit(metricsRepository.getOrders());
    }

    private static class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.ViewHolder> {

        interface Listener {
            void onView(Order order);
        }

        private final List<Order> orders;
        private final Listener listener;

        OrderAdapter(List<Order> orders, Listener listener) {
            this.orders = orders;
            this.listener = listener;
        }

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
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_admin_order, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            final Order order = orders.get(position);
            holder.bind(order, listener);
        }

        @Override
        public int getItemCount() {
            return orders.size();
        }

        static class ViewHolder extends RecyclerView.ViewHolder {

            private final TextView orderIdView;
            private final TextView statusView;
            private final TextView totalView;
            private final TextView timeView;
            private final Button detailButton;

            ViewHolder(View itemView) {
                super(itemView);
                orderIdView = (TextView) itemView.findViewById(R.id.textAdminOrderId);
                statusView = (TextView) itemView.findViewById(R.id.textAdminOrderStatus);
                totalView = (TextView) itemView.findViewById(R.id.textAdminOrderTotal);
                timeView = (TextView) itemView.findViewById(R.id.textAdminOrderTime);
                detailButton = (Button) itemView.findViewById(R.id.buttonAdminOrderDetail);
            }

            void bind(final Order order, final Listener listener) {
                Context context = itemView.getContext();
                orderIdView.setText(context.getString(R.string.order_id_template, order.getOrderId()));
                statusView.setText(formatStatus(context, order.getStatus()));
                DecimalFormat format = new DecimalFormat("0.00");
                totalView.setText(context.getString(R.string.cart_total_template, format.format(order.getTotalAmount())));
                timeView.setText(DateFormat.format("MM-dd HH:mm", new Date(order.getCreatedAt())));

                detailButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (listener != null) {
                            listener.onView(order);
                        }
                    }
                });
            }

            private String formatStatus(Context context, String status) {
                if (TextUtils.equals("PAID", status)) {
                    return context.getString(R.string.order_status_paid);
                } else if (TextUtils.equals("PENDING_PAYMENT", status)) {
                    return context.getString(R.string.order_status_pending_payment);
                } else if (TextUtils.equals("CREATED", status)) {
                    return context.getString(R.string.order_status_created);
                }
                return context.getString(R.string.order_status_unknown);
            }
        }
    }
}
