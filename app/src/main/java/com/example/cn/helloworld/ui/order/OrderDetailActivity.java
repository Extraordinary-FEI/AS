package com.example.cn.helloworld.ui.order;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.widget.LinearLayout;

import com.example.cn.helloworld.R;
import com.example.cn.helloworld.data.model.CartItem;
import com.example.cn.helloworld.data.model.Order;
import com.example.cn.helloworld.util.OrderStatusFormatter;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.util.Date;
import java.util.List;

public class OrderDetailActivity extends AppCompatActivity {

    public static final String EXTRA_ORDER = "extra_order";

    private TextView orderIdTextView;
    private TextView statusTextView;
    private TextView totalTextView;
    private TextView addressTextView;
    private TextView createdAtTextView;
    private LinearLayout itemsContainer;

    public static void start(Context context, Order order) {
        Intent intent = new Intent(context, OrderDetailActivity.class);
        intent.putExtra(EXTRA_ORDER, order);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_detail);
        setupToolbar();


        orderIdTextView = (TextView) findViewById(R.id.text_order_id);
        statusTextView = (TextView) findViewById(R.id.text_order_status);
        totalTextView = (TextView) findViewById(R.id.text_order_total);
        addressTextView = (TextView) findViewById(R.id.text_order_address);
        createdAtTextView = (TextView) findViewById(R.id.text_order_created_at);
        itemsContainer = (LinearLayout) findViewById(R.id.layout_order_items);

        Order order = (Order) getIntent().getSerializableExtra(EXTRA_ORDER);
        if (order != null) {
            bindOrder(order);
        }
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void bindOrder(Order order) {
        orderIdTextView.setText(getString(R.string.order_id_template, order.getOrderId()));
        String statusLabel = OrderStatusFormatter.format(this, order.getStatus());
        statusTextView.setText(getString(R.string.order_status_template, statusLabel));
        DecimalFormat decimalFormat = new DecimalFormat("0.00");
        totalTextView.setText(getString(R.string.order_total_template,
                decimalFormat.format(order.getTotalAmount())));
        if (!TextUtils.isEmpty(order.getShippingAddress())) {
            addressTextView.setText(getString(R.string.order_address_template, order.getShippingAddress()));
        }
        DateFormat dateFormat = android.text.format.DateFormat.getDateFormat(this);
        createdAtTextView.setText(getString(R.string.order_created_template,
                dateFormat.format(new Date(order.getCreatedAt()))));

        List<CartItem> items = order.getItems();
        if (itemsContainer != null) {
            itemsContainer.removeAllViews();
            LayoutInflater inflater = LayoutInflater.from(this);
            for (int i = 0; i < items.size(); i++) {
                CartItem item = items.get(i);
                if (item == null) {
                    continue;
                }
                TextView title;
                TextView meta;
                android.view.View itemView = inflater.inflate(R.layout.item_checkout_summary, itemsContainer, false);
                title = (TextView) itemView.findViewById(R.id.text_checkout_item_title);
                meta = (TextView) itemView.findViewById(R.id.text_checkout_item_meta);
                title.setText(item.getProductName());
                meta.setText(getString(R.string.checkout_item_meta,
                        item.getQuantity(),
                        decimalFormat.format(item.getUnitPrice()),
                        decimalFormat.format(item.getSubtotal())));
                itemsContainer.addView(itemView);
            }
        }
    }

    private void setupToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(R.string.title_order_detail);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
        }
        }
    }

