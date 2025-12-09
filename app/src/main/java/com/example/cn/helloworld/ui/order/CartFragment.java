package com.example.cn.helloworld.ui.order;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.cn.helloworld.R;
import com.example.cn.helloworld.data.model.CartItem;
import com.example.cn.helloworld.data.storage.CartStorage;
import com.example.cn.helloworld.data.session.SessionManager;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;


/**
 * 购物车 Fragment：示例列表控件、按钮和文本展示；
 * 同时结合本地存储（CartStorage）保存勾选数量，符合“Fragment+控件+数据存储”三类要求。
 */
public class CartFragment extends Fragment implements CartItemAdapter.OnCartChangedListener {

    private TextView totalTextView;
    private Button checkoutButton;
    private Button manageButton;
    private CartItemAdapter adapter;
    private List<CartItem> cartItems = new ArrayList<CartItem>();
    private CartStorage cartStorage;
    private SessionManager sessionManager;
    private boolean manageMode = false;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_cart, container, false);
        // 展示商品列表的 RecyclerView（常见控件/布局示例）
        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.recycler_cart);//展示商品
        totalTextView = (TextView) view.findViewById(R.id.text_total_price);
        checkoutButton = (Button) view.findViewById(R.id.button_checkout);
        manageButton = (Button) view.findViewById(R.id.button_manage_cart);

        sessionManager = new SessionManager(view.getContext());
        Context context = view.getContext();
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        adapter = new CartItemAdapter(cartItems, this);
        recyclerView.setAdapter(adapter);

        cartStorage = CartStorage.getInstance(context);

        // 管理员账号不展示购物车，直接提示（Activity/Fragment 间角色控制逻辑）
        if (sessionManager.isAdmin()) {
            recyclerView.setVisibility(View.GONE);
            checkoutButton.setVisibility(View.GONE);
            manageButton.setVisibility(View.GONE);
            totalTextView.setText(R.string.admin_cart_hidden);
            return view;
        }

        checkoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startCheckout();
            }
        });

        manageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleManageMode();
            }
        });

        loadCartItems();
        updateTotal();

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        loadCartItems();
        updateTotal();
    }

    /**
     * 从本地存储中读取购物车数据，若为空则写入示例数据方便展示控件效果。
     */
    private void loadCartItems() {
        cartItems.clear();
        cartItems.addAll(cartStorage.getItems());
        if (cartItems.isEmpty()) {
            seedSampleData();
        }
        if (adapter != null) {
            adapter.notifyDataSetChanged();
        }
    }

    private void seedSampleData() {
        cartItems.add(new CartItem("P1001", "官方周边写真集", 128.0, 1, null, true));
        cartItems.add(new CartItem("P1002", "演唱会应援灯牌", 89.9, 2, null, true));
        cartItems.add(new CartItem("P1003", "定制T恤", 158.0, 1, null, true));
        cartStorage.save(cartItems);
    }

    /**
     * 计算用户已勾选商品并跳转结算界面，演示 Activity 之间的跳转与数据传递。
     */
    private void startCheckout() {
        Context context = getActivity();
        if (context == null) {
            return;
        }
        ArrayList<CartItem> selectedItems = new ArrayList<CartItem>();
        double total = 0.0;
        for (int i = 0; i < cartItems.size(); i++) {
            CartItem item = cartItems.get(i);
            if (item != null && item.isSelected()) {
                selectedItems.add(new CartItem(
                        item.getProductId(),
                        item.getProductName(),
                        item.getUnitPrice(),
                        item.getQuantity(),
                        item.getImageUrl(),
                        true
                ));
                total = total + item.getSubtotal();
            }
        }

        if (selectedItems.isEmpty()) {
            Toast.makeText(context, R.string.checkout_empty_cart, Toast.LENGTH_SHORT).show();
            return;
        }

        Intent intent = CheckoutActivity.createIntent(context, selectedItems, total);
        startActivity(intent);
    }

    /**
     * 累加已选择商品的小计并刷新总价，顺便持久化到 CartStorage（对应表格“数据存储”考点）。
     */
    private void updateTotal() {
        double total = 0.0;
        for (int i = 0; i < cartItems.size(); i++) {
            CartItem item = cartItems.get(i);
            if (item != null && item.isSelected()) {
                total = total + item.getSubtotal();
            }
        }
        DecimalFormat formatter = new DecimalFormat("0.00");
        totalTextView.setText(getString(R.string.cart_total_template, formatter.format(total)));
        cartStorage.save(cartItems);
    }

    @Override
    public void onQuantityChanged() {
        updateTotal();
    }

    @Override
    public void onSelectionChanged() {
        updateTotal();
    }

    @Override
    public void onItemsChanged() {
        updateTotal();
        if (manageMode) {
            Context context = getContext();
            if (context != null) {
                Toast.makeText(context, R.string.cart_item_deleted, Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void toggleManageMode() {
        manageMode = !manageMode;
        if (adapter != null) {
            adapter.setManageMode(manageMode);
        }
        manageButton.setText(manageMode ? R.string.cart_manage_done : R.string.cart_manage);
    }
}
