package com.example.cn.helloworld.ui.order;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.cn.helloworld.R;
import com.example.cn.helloworld.data.model.CartItem;
import com.example.cn.helloworld.data.model.Order;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.UUID;

public class CheckoutActivity extends AppCompatActivity {

    private static final String EXTRA_SELECTED_ITEMS = "extra_selected_items";
    private static final String EXTRA_TOTAL = "extra_total";

    private ArrayList<CartItem> selectedItems;
    private double totalAmount = 0.0;

    private LinearLayout itemsContainer;
    private TextView totalTextView;
    private EditText addressEditText;
    private EditText noteEditText;

    public static Intent createIntent(Context context, ArrayList<CartItem> items, double total) {
        Intent intent = new Intent(context, CheckoutActivity.class);
        intent.putExtra(EXTRA_SELECTED_ITEMS, items);
        intent.putExtra(EXTRA_TOTAL, total);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout);
        setTitle(R.string.checkout_title);

        itemsContainer = (LinearLayout) findViewById(R.id.layout_checkout_items);
        totalTextView = (TextView) findViewById(R.id.text_checkout_total);
        addressEditText = (EditText) findViewById(R.id.edit_checkout_address);
        noteEditText = (EditText) findViewById(R.id.edit_checkout_note);
        Button submitButton = (Button) findViewById(R.id.button_submit_order);

        selectedItems = (ArrayList<CartItem>) getIntent().getSerializableExtra(EXTRA_SELECTED_ITEMS);
        totalAmount = getIntent().getDoubleExtra(EXTRA_TOTAL, 0.0);

        if (selectedItems == null || selectedItems.isEmpty()) {
            Toast.makeText(this, R.string.checkout_empty_cart, Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        if (totalAmount <= 0.0) {
            for (int i = 0; i < selectedItems.size(); i++) {
                CartItem item = selectedItems.get(i);
                if (item != null) {
                    totalAmount = totalAmount + item.getSubtotal();
                }
            }
        }

        bindSummary();

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitOrder();
            }
        });
    }

    private void bindSummary() {
        if (itemsContainer == null) {
            return;
        }
        LayoutInflater inflater = LayoutInflater.from(this);
        DecimalFormat decimalFormat = new DecimalFormat("0.00");
        itemsContainer.removeAllViews();
        for (int i = 0; i < selectedItems.size(); i++) {
            CartItem item = selectedItems.get(i);
            if (item == null) {
                continue;
            }
            View itemView = inflater.inflate(R.layout.item_checkout_summary, itemsContainer, false);
            TextView title = (TextView) itemView.findViewById(R.id.text_checkout_item_title);
            TextView meta = (TextView) itemView.findViewById(R.id.text_checkout_item_meta);
            title.setText(item.getProductName());
            meta.setText(getString(R.string.checkout_item_meta,
                    item.getQuantity(),
                    decimalFormat.format(item.getUnitPrice()),
                    decimalFormat.format(item.getSubtotal())));
            itemsContainer.addView(itemView);
        }
        totalTextView.setText(getString(R.string.checkout_total_template,
                decimalFormat.format(totalAmount)));
    }

    private void submitOrder() {
        String address = addressEditText.getText().toString().trim();
        if (TextUtils.isEmpty(address)) {
            addressEditText.setError(getString(R.string.checkout_address_required));
            return;
        }

        String note = noteEditText.getText().toString().trim();
        String finalAddress = address;
        if (!TextUtils.isEmpty(note)) {
            finalAddress = finalAddress + "（留言：" + note + "）";
        }

        Order order = new Order(UUID.randomUUID().toString(),
                new ArrayList<CartItem>(selectedItems),
                totalAmount,
                "PENDING_PAYMENT",
                finalAddress,
                System.currentTimeMillis());
        order.recalculateTotal();

        OrderDetailActivity.start(this, order);
        finish();
    }
}