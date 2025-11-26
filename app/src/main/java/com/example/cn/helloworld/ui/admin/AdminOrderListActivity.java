package com.example.cn.helloworld.ui.admin;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.cn.helloworld.R;
import com.example.cn.helloworld.data.model.Order;
import com.example.cn.helloworld.data.repository.AdminOrderRepository;
import com.example.cn.helloworld.data.session.SessionManager;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class AdminOrderListActivity extends AppCompatActivity implements OrderAdapter.Callback {

    public static final String EXTRA_ORDERS = "extra_orders";
    private static final String[] DEFAULT_STATUSES = new String[]{
            "CREATED",
            "PAID",
            "SHIPPED",
            "FULFILLED",
            "CANCELLED"
    };

    public static Intent createIntent(Context context, ArrayList<Order> orders) {
        Intent intent = new Intent(context, AdminOrderListActivity.class);
        intent.putExtra(EXTRA_ORDERS, orders);
        return intent;
    }

    private RecyclerView recyclerView;
    private TextView emptyView;
    private OrderAdapter adapter;
    private FloatingActionButton fabAdd;
    private AdminOrderRepository orderRepository;
    private SessionManager sessionManager;

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

        sessionManager = new SessionManager(this);
        if (!sessionManager.isAdmin()) {
            Toast.makeText(this, R.string.admin_entry_only, Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        orderRepository = new AdminOrderRepository(this);

        recyclerView = (RecyclerView) findViewById(R.id.recyclerOrders);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new OrderAdapter(this);
        recyclerView.setAdapter(adapter);

        emptyView = (TextView) findViewById(R.id.textEmptyOrders);
        fabAdd = (FloatingActionButton) findViewById(R.id.fabAddOrder);
        fabAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showOrderEditor(null);
            }
        });

        ArrayList<Order> fromIntent = (ArrayList<Order>) getIntent().getSerializableExtra(EXTRA_ORDERS);
        if (fromIntent != null && !fromIntent.isEmpty()) {
            for (int i = 0; i < fromIntent.size(); i++) {
                orderRepository.saveOrUpdate(fromIntent.get(i));
            }
        }
        submitOrders(orderRepository.getOrders());
    }

    private void submitOrders(List<Order> orders) {
        adapter.submit(orders);
        boolean isEmpty = orders == null || orders.isEmpty();
        emptyView.setVisibility(isEmpty ? View.VISIBLE : View.GONE);
    }

    private void showOrderEditor(final Order origin) {
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_admin_order_editor, null, false);
        final EditText idInput = (EditText) dialogView.findViewById(R.id.editOrderId);
        final EditText amountInput = (EditText) dialogView.findViewById(R.id.editOrderAmount);
        final EditText addressInput = (EditText) dialogView.findViewById(R.id.editOrderAddress);
        final Spinner statusSpinner = (Spinner) dialogView.findViewById(R.id.spinnerOrderStatus);

        ArrayAdapter<String> statusAdapter;
        try {
            statusAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item,
                    Arrays.asList(getResources().getStringArray(R.array.admin_order_statuses)));
        } catch (android.content.res.Resources.NotFoundException e) {
            statusAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item,
                    Arrays.asList(DEFAULT_STATUSES));
        }
        statusAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        statusSpinner.setAdapter(statusAdapter);

        if (origin != null) {
            idInput.setText(origin.getOrderId());
            amountInput.setText(String.valueOf(origin.getTotalAmount()));
            addressInput.setText(origin.getShippingAddress());
            int statusIndex = statusAdapter.getPosition(origin.getStatus());
            if (statusIndex >= 0) {
                statusSpinner.setSelection(statusIndex);
            }
        }

        new AlertDialog.Builder(this)
                .setTitle(origin == null ? R.string.admin_order_new : R.string.admin_order_save)
                .setView(dialogView)
                .setPositiveButton(R.string.admin_order_save, new android.content.DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(android.content.DialogInterface dialog, int which) {
                        Order edited = origin != null ? origin : new Order(idInput.getText().toString());
                        if (edited.getOrderId() == null || edited.getOrderId().isEmpty()) {
                            edited.setOrderId("order-" + System.currentTimeMillis());
                        } else {
                            edited.setOrderId(idInput.getText().toString());
                        }
                        double amount = 0.0;
                        try {
                            amount = Double.parseDouble(amountInput.getText().toString());
                        } catch (NumberFormatException e) {
                            amount = edited.getTotalAmount();
                        }
                        edited.setTotalAmount(amount);
                        edited.setShippingAddress(addressInput.getText().toString());
                        edited.setStatus(statusSpinner.getSelectedItem().toString());
                        if (origin == null) {
                            edited.setCreatedAt(System.currentTimeMillis());
                        }
                        orderRepository.saveOrUpdate(edited);
                        submitOrders(orderRepository.getOrders());
                        Toast.makeText(AdminOrderListActivity.this, R.string.admin_order_saved, Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton(android.R.string.cancel, null)
                .show();
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

    @Override
    public void onEdit(Order order) {
        showOrderEditor(order);
    }

    @Override
    public void onDelete(final Order order) {
        new AlertDialog.Builder(this)
                .setMessage(R.string.admin_order_delete_confirm)
                .setPositiveButton(android.R.string.ok, new android.content.DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(android.content.DialogInterface dialog, int which) {
                        orderRepository.delete(order.getOrderId());
                        submitOrders(orderRepository.getOrders());
                        Toast.makeText(AdminOrderListActivity.this, R.string.admin_order_deleted, Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton(android.R.string.cancel, null)
                .show();
    }

    static class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.ViewHolder> {

        interface Callback {
            void onEdit(Order order);

            void onDelete(Order order);
        }

        private final List<Order> orders = new ArrayList<>();
        private final SimpleDateFormat formatter = new SimpleDateFormat("MM-dd HH:mm", Locale.getDefault());
        private final Callback callback;

        OrderAdapter(Callback callback) {
            this.callback = callback;
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
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_admin_order, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            Order order = orders.get(position);
            holder.bind(order, formatter, callback);
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
            private final Button editButton;
            private final Button deleteButton;

            ViewHolder(View itemView) {
                super(itemView);
                orderIdView = (TextView) itemView.findViewById(R.id.textOrderId);
                statusView = (TextView) itemView.findViewById(R.id.textOrderStatus);
                amountView = (TextView) itemView.findViewById(R.id.textOrderAmount);
                timeView = (TextView) itemView.findViewById(R.id.textOrderTime);
                editButton = (Button) itemView.findViewById(R.id.buttonEditOrder);
                deleteButton = (Button) itemView.findViewById(R.id.buttonDeleteOrder);
            }

            void bind(final Order order, SimpleDateFormat formatter, final Callback callback) {
                Context context = itemView.getContext();
                orderIdView.setText(context.getString(R.string.order_id_template, order.getOrderId()));
                statusView.setText(context.getString(R.string.order_status_template, order.getStatus()));
                amountView.setText(context.getString(R.string.order_total_template,
                        String.format(Locale.getDefault(), "%.2f", order.getTotalAmount())));
                Date date = new Date(order.getCreatedAt());
                timeView.setText(context.getString(R.string.order_created_template, formatter.format(date)));

                if (callback != null) {
                    editButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            callback.onEdit(order);
                        }
                    });
                    deleteButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            callback.onDelete(order);
                        }
                    });
                }
            }
        }
    }
}
