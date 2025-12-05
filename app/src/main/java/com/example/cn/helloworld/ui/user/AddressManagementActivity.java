package com.example.cn.helloworld.ui.user;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
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

    private boolean selectMode = false; // 是否是选择地址模式

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_address_management);

        // 判断是否为选择模式
        selectMode = getIntent().getBooleanExtra("select_mode", false);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(selectMode ? "选择地址" : "地址管理");
        }

        View backButton = findViewById(R.id.button_back);
        if (backButton != null) {
            backButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });
        }

        repository = new AddressRepository(this);
        addresses = repository.loadAddresses();

        emptyView = (TextView) findViewById(R.id.empty_addresses);
        if (emptyView == null) {
            emptyView = new TextView(this);
        }

        RecyclerView recycler = (RecyclerView) findViewById(R.id.recycler_addresses);
        if (recycler != null) {
            recycler.setLayoutManager(new LinearLayoutManager(this));
            adapter = new AddressAdapter(addresses);
            recycler.setAdapter(adapter);
        }

        updateEmptyState();

        View addButton = findViewById(R.id.button_add_address);
        if (selectMode) {
            // 选择地址模式隐藏“新增地址”
            if (addButton != null) addButton.setVisibility(View.GONE);
        } else {
            if (addButton != null) {
                addButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showAddressEditor(null);
                    }
                });
            }
        }
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
    // 新增 / 编辑 地址
    // ============================================================================

    private void showAddressEditor(final Address origin) {

        View view = LayoutInflater.from(this)
                .inflate(R.layout.dialog_address_editor, null, false);

        final EditText nameInput = (EditText) view.findViewById(R.id.input_address_name);
        final EditText phoneInput = (EditText) view.findViewById(R.id.input_address_phone);
        final EditText detailInput = (EditText) view.findViewById(R.id.input_address_detail);

        final boolean isEdit = origin != null;

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

                        if (name.isEmpty() || detail.isEmpty()) return;

                        if (!isEdit) {
                            Address newAddr = new Address(
                                    "id-" + System.currentTimeMillis(),
                                    name,
                                    phone,
                                    detail
                            );
                            addresses.add(newAddr);
                            repository.saveAddresses(addresses);

                        } else {
                            origin.setContactName(name);
                            origin.setPhone(phone);
                            origin.setDetail(detail);
                            repository.updateAddress(origin);
                        }

                        if (adapter != null) adapter.notifyDataSetChanged();
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
            detailView.setText("电话：" + address.getPhone() + "\n地址：" + address.getDetail());

            // 点击地址 → 选择模式下返回数据
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (selectMode) {
                        String resultText =
                                address.getContactName() + " · " + address.getPhone() +
                                        "\n" + address.getDetail();

                        Intent result = new Intent();
                        result.putExtra("selected_address", resultText);
                        setResult(RESULT_OK, result);

                        finish();
                    }
                }
            });

            // 非选择模式 → 显示编辑/删除按钮
            if (!selectMode) {

                if (editBtn != null) {
                    editBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            showAddressEditor(address);
                        }
                    });
                }

                if (deleteBtn != null) {
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

                                            if (adapter != null) adapter.notifyDataSetChanged();
                                            updateEmptyState();
                                        }
                                    })
                                    .setNegativeButton("取消", null)
                                    .show();
                        }
                    });
                }

            } else {
                // 选择模式隐藏按钮
                if (editBtn != null) editBtn.setVisibility(View.GONE);
                if (deleteBtn != null) deleteBtn.setVisibility(View.GONE);
            }
        }
    }
}
