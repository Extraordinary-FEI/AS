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

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;


public class CartFragment extends Fragment implements CartItemAdapter.OnCartChangedListener {

    private TextView totalTextView;
    private Button checkoutButton;
    private CartItemAdapter adapter;
    private List<CartItem> cartItems = new ArrayList<CartItem>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_cart, container, false);
        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.recycler_cart);
        totalTextView = (TextView) view.findViewById(R.id.text_total_price);
        checkoutButton = (Button) view.findViewById(R.id.button_checkout);

        Context context = view.getContext();
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        adapter = new CartItemAdapter(cartItems, this);
        recyclerView.setAdapter(adapter);

        checkoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startCheckout();
            }
        });

        loadSampleData();
        updateTotal();

        return view;
    }

    private void loadSampleData() {
        cartItems.clear();
        cartItems.add(new CartItem("P1001", "官方周边写真集", 128.0, 1, null, true));
        cartItems.add(new CartItem("P1002", "演唱会应援灯牌", 89.9, 2, null, true));
        cartItems.add(new CartItem("P1003", "定制T恤", 158.0, 1, null, true));
        if (adapter != null) {
            adapter.notifyDataSetChanged();
        }
    }

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
    }

    @Override
    public void onQuantityChanged() {
        updateTotal();
    }

    @Override
    public void onSelectionChanged() {
        updateTotal();
    }
}
