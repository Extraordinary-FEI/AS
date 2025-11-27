package com.example.cn.helloworld.ui.user;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.example.cn.helloworld.R;
import com.example.cn.helloworld.data.model.Address;
import com.example.cn.helloworld.data.repository.AddressRepository;

import java.util.ArrayList;
import java.util.List;

public class AddressManagementActivity extends AppCompatActivity {

    private AddressRepository repository;
    private List<Address> addresses = new ArrayList<>();
    private AddressAdapter adapter;
    private TextView emptyView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_address_management);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("地址管理");
        }

        repository = new AddressRepository(this);
        addresses = repository.loadAddresses();

        emptyView = (TextView) findViewById(R.id.empty_addresses);

        RecyclerView recycler = (RecyclerView) findViewById(R.id.recycler_addresses);
        recycler.setLayoutManager(new LinearLayoutManager(this));
        adapter = new AddressAdapter(addresses);
        recycler.setAdapter(adapter);

        updateEmptyState();

        // 新增地址
        findViewById(R.id.button_add_address).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAddressEditor(null);
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void updateEmptyState() {
        emptyView.setVisibility(addresses.isEmpty() ? View.VISIBLE : View.GONE);
    }

    // ============================================================================
    // 编辑 / 新增 弹窗
    // ============================================================================

    private void showAddressEditor(final Address origin) {

        View view = LayoutInflater.from(this)
                .inflate(R.layout.dialog_address_editor, null, false);

        final EditText nameInput = (EditText) view.findViewById(R.id.input_address_name);
        final EditText phoneInput = (EditText) view.findViewById(R.id.input_address_phone);
        final EditText detailInput = (EditText) view.findViewById(R.id.input_address_detail);

        boolean isEdit = origin != null;

        // 如果是编辑模式，填入原数据
        if (isEdit) {
            nameInput.setText(origin.getContactName());
            phoneInput.setText(origin.getPhone());
            detailInput.setText(origin.getDetail());
        }

        new AlertDialog.Builder(this)
                .setTitle(isEdit ? "编辑地址" : "新增地址")
                .setView(view)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        String name = nameInput.getText().toString().trim();
                        String phone = phoneInput.getText().toString().trim();
                        String detail = detailInput.getText().toString().trim();

                        if (name.isEmpty() || detail.isEmpty()) {
                            return;
                        }

                        if (origin == null) {
                            // 新地址
                            Address newAddr = new Address(
                                    "id-" + System.currentTimeMillis(),
                                    name,
                                    phone,
                                    detail
                            );
                            addresses.add(newAddr);
                            repository.saveAddresses(addresses);

                        } else {
                            // 更新地址
                            origin.setContactName(name);
                            origin.setPhone(phone);
                            origin.setDetail(detail);

                            repository.updateAddress(origin);
                        }

                        adapter.notifyDataSetChanged();
                        updateEmptyState();
                    }
                })
                .setNegativeButton("取消", null)
                .show();
    }

    // ============================================================================
    // Adapter
    // ============================================================================

    private class AddressAdapter extends RecyclerView.Adapter<AddressViewHolder> {

        private List<Address> list;

        AddressAdapter(List<Address> list) {
            this.list = list;
        }

        @Override
        public AddressViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_address, parent, false);
            return new AddressViewHolder(view);
        }

        @Override
        public void onBindViewHolder(AddressViewHolder holder, int position) {
            holder.bind(list.get(position));
        }

        @Override
        public int getItemCount() {
            return list.size();
        }
    }

    // ============================================================================
    // ViewHolder
    // ============================================================================

    private class AddressViewHolder extends RecyclerView.ViewHolder {

        private TextView titleView;
        private TextView detailView;
        private View editBtn;
        private View deleteBtn;

        public AddressViewHolder(View itemView) {
            super(itemView);

            titleView = (TextView) itemView.findViewById(R.id.text_address_title);
            detailView = (TextView) itemView.findViewById(R.id.text_address_detail);
            editBtn = itemView.findViewById(R.id.button_edit_address);
            deleteBtn = itemView.findViewById(R.id.button_delete_address);
        }

        public void bind(final Address address) {

            titleView.setText(address.getContactName());
            detailView.setText(
                    "电话：" + address.getPhone() + "\n地址：" + address.getDetail()
            );

            editBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showAddressEditor(address);
                }
            });

            deleteBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    new AlertDialog.Builder(AddressManagementActivity.this)
                            .setTitle("删除地址")
                            .setMessage("确定删除此地址？")
                            .setPositiveButton("删除", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                    addresses.remove(address);
                                    repository.saveAddresses(addresses);

                                    adapter.notifyDataSetChanged();
                                    updateEmptyState();
                                }
                            })
                            .setNegativeButton("取消", null)
                            .show();
                }
            });
        }
    }
}
