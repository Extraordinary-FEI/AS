package com.example.cn.helloworld.ui.admin;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.cn.helloworld.R;
import com.example.cn.helloworld.data.model.Order;
import com.example.cn.helloworld.util.OrderStatusFormatter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Adapter for displaying orders in the admin order list.
 */
class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.ViewHolder> {

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
            String statusLabel = OrderStatusFormatter.format(context, order.getStatus());
            statusView.setText(context.getString(R.string.order_status_template, statusLabel));
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
