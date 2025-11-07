package com.example.cn.helloworld.ui.order;


import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.cn.helloworld.R;
import com.example.cn.helloworld.data.model.CartItem;

import java.text.DecimalFormat;
import java.util.List;

public class CartItemAdapter extends RecyclerView.Adapter<CartItemAdapter.CartViewHolder> {

    public interface OnCartChangedListener {
        void onQuantityChanged();

        void onSelectionChanged();
    }

    private List<CartItem> items;
    private OnCartChangedListener listener;

    public CartItemAdapter(List<CartItem> items, OnCartChangedListener listener) {
        this.items = items;
        this.listener = listener;
    }

    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_cart, parent, false);
        return new CartViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CartViewHolder holder, int position) {
        holder.bind(items.get(position));
    }

    @Override
    public int getItemCount() {
        return items == null ? 0 : items.size();
    }

    class CartViewHolder extends RecyclerView.ViewHolder {

        private TextView nameTextView;
        private TextView priceTextView;
        private TextView quantityTextView;
        private ImageButton minusButton;
        private ImageButton plusButton;
        private CheckBox selectCheckBox;
        private DecimalFormat priceFormat = new DecimalFormat("0.00");

        CartViewHolder(View itemView) {
            super(itemView);
            nameTextView = (TextView) itemView.findViewById(R.id.text_item_name);
            priceTextView = (TextView) itemView.findViewById(R.id.text_item_price);
            quantityTextView = (TextView) itemView.findViewById(R.id.text_quantity);
            minusButton = (ImageButton) itemView.findViewById(R.id.button_minus);
            plusButton = (ImageButton) itemView.findViewById(R.id.button_plus);
            selectCheckBox = (CheckBox) itemView.findViewById(R.id.checkbox_select);
        }

        void bind(final CartItem item) {
            nameTextView.setText(item.getProductName());
            priceTextView.setText(itemView.getContext().getString(R.string.cart_price_template,
                    priceFormat.format(item.getUnitPrice())));
            quantityTextView.setText(String.valueOf(item.getQuantity()));
            selectCheckBox.setChecked(item.isSelected());

            minusButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    item.decreaseQuantity();
                    quantityTextView.setText(String.valueOf(item.getQuantity()));
                    if (listener != null) {
                        listener.onQuantityChanged();
                    }
                }
            });

            plusButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    item.increaseQuantity();
                    quantityTextView.setText(String.valueOf(item.getQuantity()));
                    if (listener != null) {
                        listener.onQuantityChanged();
                    }
                }
            });

            selectCheckBox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    item.setSelected(selectCheckBox.isChecked());
                    if (listener != null) {
                        listener.onSelectionChanged();
                    }
                }
            });
        }
    }
}
