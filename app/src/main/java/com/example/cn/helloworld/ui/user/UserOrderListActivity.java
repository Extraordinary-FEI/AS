package com.example.cn.helloworld.ui.user;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.example.cn.helloworld.R;
import com.example.cn.helloworld.data.model.Order;
import com.example.cn.helloworld.data.repository.AdminOrderRepository;
import com.example.cn.helloworld.ui.admin.OrderAdapter;
import com.example.cn.helloworld.ui.order.OrderDetailActivity;

import java.util.List;

/**
 * 用户端订单列表：只读查看状态。
 */
public class UserOrderListActivity extends AppCompatActivity implements OrderAdapter.Callback {

    private RecyclerView recyclerView;
    private TextView emptyView;
    private OrderAdapter adapter;
    private AdminOrderRepository repository;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_order_list);

        setTitle(R.string.user_action_orders);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        recyclerView = (RecyclerView) findViewById(R.id.recycler_user_orders);
        emptyView = (TextView) findViewById(R.id.text_empty_user_orders);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter = new OrderAdapter(this, false);
        recyclerView.setAdapter(adapter);

        repository = new AdminOrderRepository(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        List<Order> orders = repository.getOrders();
        adapter.submit(orders);
        emptyView.setVisibility(orders == null || orders.isEmpty() ? View.VISIBLE : View.GONE);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onEdit(Order order) {
        // no-op in user mode
    }

    @Override
    public void onDelete(Order order) {
        // no-op in user mode
    }

    @Override
    public void onView(Order order) {
        OrderDetailActivity.start(this, order);
    }
}


