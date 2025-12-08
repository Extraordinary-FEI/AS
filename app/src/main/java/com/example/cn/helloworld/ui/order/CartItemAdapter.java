package com.example.cn.helloworld.ui.order;

import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.cn.helloworld.R;
import com.example.cn.helloworld.data.model.CartItem;

import java.text.DecimalFormat;
import java.util.List;

public class CartItemAdapter extends RecyclerView.Adapter<CartItemAdapter.CartViewHolder> {

    public interface OnCartChangedListener {
        void onQuantityChanged();

        void onSelectionChanged();

        void onItemsChanged();
    }

    private List<CartItem> items;
    private OnCartChangedListener listener;
    private boolean manageMode = false;

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

    public void setManageMode(boolean manageMode) {
        this.manageMode = manageMode;
        notifyDataSetChanged();
    }

    private void deleteItem(int position) {
        if (items == null || position < 0 || position >= items.size()) {
            return;
        }
        items.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, getItemCount() - position);
        if (listener != null) {
            listener.onItemsChanged();
        }
    }

    class CartViewHolder extends RecyclerView.ViewHolder {

        private TextView nameTextView;
        private TextView priceTextView;
        private TextView quantityTextView;
        private ImageButton minusButton;
        private ImageButton plusButton;
        private ImageButton deleteButton;
        private CheckBox selectCheckBox;
        private DecimalFormat priceFormat = new DecimalFormat("0.00");

        CartViewHolder(View itemView) {
            super(itemView);
            nameTextView = (TextView) itemView.findViewById(R.id.text_item_name);
            priceTextView = (TextView) itemView.findViewById(R.id.text_item_price);
            quantityTextView = (TextView) itemView.findViewById(R.id.text_quantity);
            minusButton = (ImageButton) itemView.findViewById(R.id.button_minus);
            plusButton = (ImageButton) itemView.findViewById(R.id.button_plus);
            deleteButton = (ImageButton) itemView.findViewById(R.id.button_delete);
            selectCheckBox = (CheckBox) itemView.findViewById(R.id.checkbox_select);
        }

        void bind(final CartItem item) {
            nameTextView.setText(item.getProductName());
            priceTextView.setText(itemView.getContext().getString(R.string.cart_price_template,
                    priceFormat.format(item.getUnitPrice())));
            quantityTextView.setText(String.valueOf(item.getQuantity()));
            selectCheckBox.setChecked(item.isSelected());
            deleteButton.setVisibility(manageMode ? View.VISIBLE : View.GONE);

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

            quantityTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showQuantityDialog(item);
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

            deleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    deleteItem(getAdapterPosition());
                }
            });
        }

        private void showQuantityDialog(final CartItem item) {
            final EditText input = new EditText(itemView.getContext());
            input.setInputType(InputType.TYPE_CLASS_NUMBER);
            input.setHint(R.string.cart_quantity_dialog_hint);
            input.setText(String.valueOf(item.getQuantity()));
            input.setSelection(input.getText().length());

            new AlertDialog.Builder(itemView.getContext())
                    .setTitle(R.string.cart_quantity_dialog_title)
                    .setView(input)
                    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            String text = input.getText().toString().trim();
                            if (TextUtils.isEmpty(text)) {
                                Toast.makeText(itemView.getContext(), R.string.cart_quantity_invalid, Toast.LENGTH_SHORT).show();
                                return;
                            }

                            int newQuantity;
                            try {
                                newQuantity = Integer.parseInt(text);
                            } catch (NumberFormatException e) {
                                newQuantity = -1;
                            }

                            if (newQuantity <= 0) {
                                Toast.makeText(itemView.getContext(), R.string.cart_quantity_invalid, Toast.LENGTH_SHORT).show();
                                return;
                            }

                            item.setQuantity(newQuantity);
                            quantityTextView.setText(String.valueOf(item.getQuantity()));
                            if (listener != null) {
                                listener.onQuantityChanged();
                            }
                        }
                    })
                    .setNegativeButton(android.R.string.cancel, null)
                    .show();
        }
    }
}
