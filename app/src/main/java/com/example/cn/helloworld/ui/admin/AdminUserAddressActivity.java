package com.example.cn.helloworld.ui.admin;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.cn.helloworld.R;
import com.example.cn.helloworld.data.model.Address;
import com.example.cn.helloworld.data.repository.AddressRepository;
import com.example.cn.helloworld.data.session.SessionManager;

import java.util.ArrayList;
import java.util.List;

/**
 * 管理员用户地址管理：用于在特殊场景下协助用户修改或删除收货地址。
 */
public class AdminUserAddressActivity extends AppCompatActivity {

    private EditText inputUserKey;
    private TextView currentUserText;
    private TextView emptyView;
    private View addButton;
    private RecyclerView recyclerView;

    private AddressRepository repository;
    private final List<Address> addresses = new ArrayList<Address>();
    private AddressAdapter adapter;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_user_address);
        setTitle(R.string.title_admin_manage_addresses);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        sessionManager = new SessionManager(this);
        if (!sessionManager.isAdmin()) {
            Toast.makeText(this, R.string.admin_entry_only, Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        bindViews();
        setupList();
        setupActions();
        updateEmptyState();
    }

    private void bindViews() {
        inputUserKey = (EditText) findViewById(R.id.input_user_identity);
        currentUserText = (TextView) findViewById(R.id.text_current_user);
        emptyView = (TextView) findViewById(R.id.text_empty_addresses);
        recyclerView = (RecyclerView) findViewById(R.id.recycler_user_addresses);
        addButton = findViewById(R.id.button_add_address);
    }

    private void setupList() {
        if (recyclerView != null) {
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            adapter = new AddressAdapter(addresses);
            recyclerView.setAdapter(adapter);
        }
        if (addButton != null) {
            addButton.setEnabled(false);
        }
    }

    private void setupActions() {
        Button loadButton = (Button) findViewById(R.id.button_load_user);
        if (loadButton != null) {
            loadButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    loadAddressesForUser();
                }
            });
        }

        if (addButton != null) {
            addButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showAddressEditor(null);
                }
            });
        }
    }

    private void loadAddressesForUser() {
        String target = inputUserKey != null ? inputUserKey.getText().toString().trim() : "";
        if (TextUtils.isEmpty(target)) {
            Toast.makeText(this, R.string.admin_address_need_input, Toast.LENGTH_SHORT).show();
            return;
        }

        repository = new AddressRepository(this, "user", target);
        addresses.clear();
        addresses.addAll(repository.loadAddresses());

        if (currentUserText != null) {
            currentUserText.setText(getString(R.string.admin_address_current_user, target));
        }
        if (adapter != null) adapter.notifyDataSetChanged();
        if (addButton != null) addButton.setEnabled(true);
        updateEmptyState();
    }

    private void updateEmptyState() {
        boolean isEmpty = addresses.isEmpty();
        if (emptyView != null) {
            emptyView.setVisibility(isEmpty ? View.VISIBLE : View.GONE);
        }
    }

    private void showAddressEditor(final Address origin) {
        if (repository == null) {
            Toast.makeText(this, R.string.admin_address_need_user, Toast.LENGTH_SHORT).show();
            return;
        }

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
                .setTitle(isEdit ? R.string.action_edit_address : R.string.admin_address_add_action)
                .setView(view)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        String name = nameInput.getText().toString().trim();
                        String phone = phoneInput.getText().toString().trim();
                        String detail = detailInput.getText().toString().trim();

                        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(detail)) {
                            Toast.makeText(AdminUserAddressActivity.this, R.string.admin_address_need_fields, Toast.LENGTH_SHORT).show();
                            return;
                        }

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
                .setNegativeButton(android.R.string.cancel, null)
                .show();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

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

    private class AddressViewHolder extends RecyclerView.ViewHolder {

        private TextView titleView;
        private TextView detailView;
        private View editBtn;
        private View deleteBtn;

        AddressViewHolder(View itemView) {
            super(itemView);

            titleView = (TextView) itemView.findViewById(R.id.text_address_title);
            detailView = (TextView) itemView.findViewById(R.id.text_address_detail);
            editBtn = itemView.findViewById(R.id.button_edit_address);
            deleteBtn = itemView.findViewById(R.id.button_delete_address);
        }

        void bind(final Address address) {

            titleView.setText(address.getContactName());
            detailView.setText("电话：" + address.getPhone() + "\n地址：" + address.getDetail());

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
                        confirmDelete(address);
                    }
                });
            }
        }
    }

    private void confirmDelete(final Address address) {
        if (repository == null) {
            Toast.makeText(this, R.string.admin_address_need_user, Toast.LENGTH_SHORT).show();
            return;
        }

        new AlertDialog.Builder(this)
                .setTitle(R.string.admin_action_delete)
                .setMessage(R.string.admin_address_delete_confirm)
                .setPositiveButton(R.string.admin_action_delete, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        addresses.remove(address);
                        repository.saveAddresses(addresses);
                        if (adapter != null) adapter.notifyDataSetChanged();
                        updateEmptyState();
                    }
                })
                .setNegativeButton(android.R.string.cancel, null)
                .show();
    }
}

