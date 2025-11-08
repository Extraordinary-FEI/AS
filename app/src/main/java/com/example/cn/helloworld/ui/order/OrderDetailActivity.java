package com.example.cn.helloworld.ui.order;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.example.cn.helloworld.R;
import com.example.cn.helloworld.data.model.CartItem;
import com.example.cn.helloworld.data.model.Order;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class OrderDetailActivity extends AppCompatActivity {

    public static final String EXTRA_ORDER = "extra_order";

    private TextView orderIdTextView;
    private TextView statusTextView;
    private TextView totalTextView;
    private TextView addressTextView;
    private TextView createdAtTextView;
    private ListView listView;

    public static void start(Context context, Order order) {
        Intent intent = new Intent(context, OrderDetailActivity.class);
        intent.putExtra(EXTRA_ORDER, order);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_detail);
        setTitle(R.string.title_order_detail);

        orderIdTextView = (TextView) findViewById(R.id.text_order_id);
        statusTextView = (TextView) findViewById(R.id.text_order_status);
        totalTextView = (TextView) findViewById(R.id.text_order_total);
        addressTextView = (TextView) findViewById(R.id.text_order_address);
        createdAtTextView = (TextView) findViewById(R.id.text_order_created_at);
        listView = (ListView) findViewById(R.id.list_order_items);

        Order order = (Order) getIntent().getSerializableExtra(EXTRA_ORDER);
        if (order != null) {
            bindOrder(order);
        }
    }

    private void bindOrder(Order order) {
        orderIdTextView.setText(getString(R.string.order_id_template, order.getOrderId()));
        statusTextView.setText(getString(R.string.order_status_template, order.getStatus()));
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
        List<String> itemDescriptions = new ArrayList<String>();
        for (int i = 0; i < items.size(); i++) {
            CartItem item = items.get(i);
            itemDescriptions.add(item.getProductName() + " x" + item.getQuantity());
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, itemDescriptions);
        listView.setAdapter(adapter);
    }
}
