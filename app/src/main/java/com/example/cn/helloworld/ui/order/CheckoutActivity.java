package com.example.cn.helloworld.ui.order;

import android.content.Context;
import android.content.Intent;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.cn.helloworld.R;
import com.example.cn.helloworld.data.model.Address;
import com.example.cn.helloworld.data.model.CartItem;
import com.example.cn.helloworld.data.model.Order;
import com.example.cn.helloworld.data.repository.AdminOrderRepository;
import com.example.cn.helloworld.data.repository.AddressRepository;
import com.example.cn.helloworld.ui.user.AddressManagementActivity;

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

    // 新的 TextView 显示地址
    private TextView addressTextView;
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

        // View 绑定
        itemsContainer = (LinearLayout) findViewById(R.id.layout_checkout_items);
        totalTextView = (TextView) findViewById(R.id.text_checkout_total);
        noteEditText = (EditText) findViewById(R.id.edit_checkout_note);

        // 这两个必须对应布局中的 TextView
        addressTextView = (TextView) findViewById(R.id.text_selected_address);
        View selectAddressButton = findViewById(R.id.button_select_address);

        // 从 Intent 获取购物车数据
        selectedItems = (ArrayList<CartItem>) getIntent().getSerializableExtra(EXTRA_SELECTED_ITEMS);
        totalAmount = getIntent().getDoubleExtra(EXTRA_TOTAL, 0.0);

        if (selectedItems == null || selectedItems.isEmpty()) {
            Toast.makeText(this, R.string.checkout_empty_cart, Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        if (totalAmount <= 0.0) {
            for (CartItem item : selectedItems) {
                if (item != null) totalAmount += item.getSubtotal();
            }
        }

        bindSummary();

        // 加载地址
        addressRepository = new AddressRepository(this);
        savedAddresses = addressRepository.loadAddresses();

        // 点击事件（两个地方都能点）
        View.OnClickListener selector = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAddressBottomSheet();
            }
        };
        addressTextView.setOnClickListener(selector);
        if (selectAddressButton != null) selectAddressButton.setOnClickListener(selector);

        // 自动填充第一个地址
        if (!savedAddresses.isEmpty()) {
            applyAddress(savedAddresses.get(0));
        }

        // 提交订单
        Button submitButton = (Button) findViewById(R.id.button_submit_order);
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

    private void bindSummary() {
        LayoutInflater inflater = LayoutInflater.from(this);
        DecimalFormat df = new DecimalFormat("0.00");
        itemsContainer.removeAllViews();

        for (CartItem item : selectedItems) {
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

    // ================================
    //  底部弹窗选择地址
    // ================================
    private void showAddressBottomSheet() {

        if (savedAddresses == null || savedAddresses.isEmpty()) {
            Toast.makeText(this, "暂无地址，请先添加", Toast.LENGTH_SHORT).show();
            return;
        }

        final android.app.Dialog dialog = new android.app.Dialog(this, android.R.style.Theme_DeviceDefault_Light_Dialog_NoActionBar);
        dialog.setContentView(R.layout.dialog_select_address);

        ListView listView = (ListView) dialog.findViewById(R.id.list_addresses);
        TextView addNewBtn = (TextView) dialog.findViewById(R.id.button_add_new);

        // 数据
        List<String> list = new ArrayList<String>();
        for (Address a : savedAddresses) {
            list.add(a.getContactName() + "  " + a.getPhone() + "\n" + a.getDetail());
        }

        listView.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, list));

        // 选中地址
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                applyAddress(savedAddresses.get(position));
                dialog.dismiss();
            }
        });

        // 新增地址
        addNewBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                startActivity(new Intent(CheckoutActivity.this, AddressManagementActivity.class));
            }
        });

        dialog.show();
    }

    private void applyAddress(Address address) {
        addressTextView.setText(
                address.getContactName() + "  " +
                        address.getPhone() + "\n" +
                        address.getDetail()
        );
    }

    private void submitOrder() {

        String address = addressTextView.getText().toString().trim();
        if (TextUtils.isEmpty(address) || address.equals("请选择收货地址")) {
            Toast.makeText(this, "请选择地址", Toast.LENGTH_SHORT).show();
            return;
        }

        String note = noteEditText.getText().toString().trim();
        if (!TextUtils.isEmpty(note)) {
            address += "（留言：" + note + "）";
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
