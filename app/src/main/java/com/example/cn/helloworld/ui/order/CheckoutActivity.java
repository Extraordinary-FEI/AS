package com.example.cn.helloworld.ui.order;

import android.content.Context;
import android.content.Intent;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.support.v7.widget.Toolbar;

import com.example.cn.helloworld.R;
import com.example.cn.helloworld.data.model.CartItem;
import com.example.cn.helloworld.data.model.Order;
import com.example.cn.helloworld.data.repository.AdminOrderRepository;
import com.example.cn.helloworld.data.model.Address;
import com.example.cn.helloworld.data.repository.AddressRepository;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class CheckoutActivity extends AppCompatActivity {

    private static final String EXTRA_SELECTED_ITEMS = "extra_selected_items";
    private static final String EXTRA_TOTAL = "extra_total";

    private ArrayList<CartItem> selectedItems;
    private double totalAmount = 0.0;

    private LinearLayout itemsContainer;
    private TextView totalTextView;

    private TextView addressTextView;     // ★ 改成 TextView
    private View selectAddressButton;

    private EditText noteEditText;

    private List<Address> savedAddresses = new ArrayList<Address>();
    private AddressRepository addressRepository;

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

        // Toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(R.string.checkout_title);
        }

        // Bind Views
        itemsContainer = (LinearLayout) findViewById(R.id.layout_checkout_items);
        totalTextView = (TextView) findViewById(R.id.text_checkout_total);

        addressTextView = (TextView) findViewById(R.id.text_selected_address); // ★ 替换
        selectAddressButton = findViewById(R.id.button_select_address);

        noteEditText = (EditText) findViewById(R.id.edit_checkout_note);

        Button submitButton = (Button) findViewById(R.id.button_submit_order);

        // Load data
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
                if (item != null) totalAmount += item.getSubtotal();
            }
        }

        bindSummary();

        // Load addresses
        addressRepository = new AddressRepository(this);
        savedAddresses = addressRepository.loadAddresses();

        // 点击选择地址
        View.OnClickListener selector = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAddressSelectionDialog();
            }
        };

        addressTextView.setOnClickListener(selector);
        selectAddressButton.setOnClickListener(selector);

        // 默认选第一个地址
        if (!savedAddresses.isEmpty()) {
            applyAddress(savedAddresses.get(0));
        }

        // 提交订单
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitOrder();
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    // -----------------------------
    // 购物车商品摘要
    // -----------------------------
    private void bindSummary() {
        LayoutInflater inflater = LayoutInflater.from(this);
        DecimalFormat df = new DecimalFormat("0.00");
        itemsContainer.removeAllViews();

        for (int i = 0; i < selectedItems.size(); i++) {
            CartItem item = selectedItems.get(i);
            if (item == null) continue;

            View view = inflater.inflate(R.layout.item_checkout_summary, itemsContainer, false);
            ((TextView) view.findViewById(R.id.text_checkout_item_title)).setText(item.getProductName());
            ((TextView) view.findViewById(R.id.text_checkout_item_meta)).setText(
                    getString(R.string.checkout_item_meta,
                            item.getQuantity(),
                            df.format(item.getUnitPrice()),
                            df.format(item.getSubtotal()))
            );
            itemsContainer.addView(view);
        }

        totalTextView.setText(getString(R.string.checkout_total_template, df.format(totalAmount)));
    }

    // -----------------------------
    // 地址选择弹窗
    // -----------------------------
    private void showAddressSelectionDialog() {

        if (savedAddresses == null || savedAddresses.isEmpty()) {
            Toast.makeText(this, R.string.checkout_no_addresses, Toast.LENGTH_SHORT).show();
            return;
        }

        CharSequence[] list = new CharSequence[savedAddresses.size()];
        for (int i = 0; i < savedAddresses.size(); i++) {
            list[i] = formatAddress(savedAddresses.get(i));
        }

        new AlertDialog.Builder(this)
                .setTitle(R.string.checkout_select_address_title)
                .setItems(list, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        applyAddress(savedAddresses.get(which));
                    }
                })
                .setNegativeButton(android.R.string.cancel, null)
                .show();
    }

    // -----------------------------
    // 应用选中的地址
    // -----------------------------
    private void applyAddress(Address address) {
        addressTextView.setText(formatAddress(address));
    }

    private String formatAddress(Address a) {
        StringBuilder sb = new StringBuilder();

        if (!TextUtils.isEmpty(a.getContactName())) {
            sb.append(a.getContactName());
        }
        if (!TextUtils.isEmpty(a.getPhone())) {
            sb.append("  ").append(a.getPhone());
        }
        if (!TextUtils.isEmpty(a.getDetail())) {
            sb.append("\n").append(a.getDetail());
        }

        return sb.toString();
    }

    // -----------------------------
    // 提交订单
    // -----------------------------
    private void submitOrder() {
        String address = addressTextView.getText().toString().trim();

        if (TextUtils.isEmpty(address) || address.equals("请选择收货地址")) {
            Toast.makeText(this, "请选择收货地址", Toast.LENGTH_SHORT).show();
            return;
        }

        String note = noteEditText.getText().toString().trim();
        if (!TextUtils.isEmpty(note)) {
            address = address + "（留言：" + note + "）";
        }

        Order order = new Order(
                UUID.randomUUID().toString(),
                new ArrayList<CartItem>(selectedItems),
                totalAmount,
                "CREATED",
                address,
                System.currentTimeMillis()
        );
        order.recalculateTotal();

        new AdminOrderRepository(this).saveOrUpdate(order);

        OrderDetailActivity.start(this, order);
        finish();
    }
}
