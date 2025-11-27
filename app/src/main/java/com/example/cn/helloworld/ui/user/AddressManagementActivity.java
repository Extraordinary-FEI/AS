package com.example.cn.helloworld.ui.user;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
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
    private AddressAdapter adapter;
    private List<Address> addresses = new ArrayList<Address>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_address_management);

        setTitle(R.string.title_address_management);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        repository = new AddressRepository(this);
        addresses = repository.loadAddresses();

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycler_addresses);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new AddressAdapter(addresses, new AddressAdapter.Listener() {
            @Override
            public void onEdit(Address address) {
                showEditor(address);
            }

            @Override
            public void onDelete(Address address) {
                confirmDelete(address);
            }
        });
        recyclerView.setAdapter(adapter);

        findViewById(R.id.button_add_address).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showEditor(null);
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

    private void showEditor(@Nullable final Address origin) {
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_address_editor, null, false);
        final EditText nameInput = (EditText) view.findViewById(R.id.input_address_name);
        final EditText phoneInput = (EditText) view.findViewById(R.id.input_address_phone);
        final EditText detailInput = (EditText) view.findViewById(R.id.input_address_detail);

        if (origin != null) {
            nameInput.setText(origin.getContactName());
            phoneInput.setText(origin.getPhone());
            detailInput.setText(origin.getDetail());
        }

        new AlertDialog.Builder(this)
                .setTitle(origin == null ? R.string.action_add_address : R.string.action_edit_address)
                .setView(view)
                .setPositiveButton(R.string.action_save, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String name = nameInput.getText().toString().trim();
                        String phone = phoneInput.getText().toString().trim();
                        String detail = detailInput.getText().toString().trim();
                        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(detail)) {
                            return;
                        }

                        if (origin == null) {
                            addresses.add(Address.create(name, phone, detail));
                        } else {
                            int index = findIndex(origin.getId());
                            if (index >= 0) {
                                addresses.set(index, origin.withContent(name, phone, detail));
                            }
                        }
                        persist();
                    }
                })
                .setNegativeButton(android.R.string.cancel, null)
                .show();
    }

    private void confirmDelete(final Address address) {
        new AlertDialog.Builder(this)
                .setMessage(getString(R.string.address_delete_confirm, address.getContactName()))
                .setPositiveButton(R.string.action_delete, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        addresses.remove(address);
                        persist();
                    }
                })
                .setNegativeButton(android.R.string.cancel, null)
                .show();
    }

    private int findIndex(String id) {
        for (int i = 0; i < addresses.size(); i++) {
            if (addresses.get(i).getId().equals(id)) {
                return i;
            }
        }
        return -1;
    }

    private void persist() {
        repository.saveAddresses(addresses);
        adapter.update(addresses);
    }

    private static class AddressAdapter extends RecyclerView.Adapter<AddressViewHolder> {

        interface Listener {
            void onEdit(Address address);

            void onDelete(Address address);
        }

        private final Listener listener;
        private List<Address> addresses;

        AddressAdapter(List<Address> addresses, Listener listener) {
            this.addresses = new ArrayList<Address>(addresses);
            this.listener = listener;
        }

        @Override
        public AddressViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_address, parent, false);
            return new AddressViewHolder(view);
        }

        @Override
        public void onBindViewHolder(AddressViewHolder holder, int position) {
            final Address address = addresses.get(position);
            holder.bind(address, listener);
        }

        @Override
        public int getItemCount() {
            return addresses.size();
        }

        void update(List<Address> newAddresses) {
            this.addresses = new ArrayList<Address>(newAddresses);
            notifyDataSetChanged();
        }
    }

    private static class AddressViewHolder extends RecyclerView.ViewHolder {

        private final TextView titleView;
        private final TextView detailView;
        private final View editButton;
        private final View deleteButton;

        AddressViewHolder(View itemView) {
            super(itemView);
            titleView = (TextView) itemView.findViewById(R.id.text_address_title);
            detailView = (TextView) itemView.findViewById(R.id.text_address_detail);
            editButton = itemView.findViewById(R.id.button_edit_address);
            deleteButton = itemView.findViewById(R.id.button_delete_address);
        }

        void bind(final Address address, final AddressAdapter.Listener listener) {
            titleView.setText(address.getContactName());
            String phone = address.getPhone();
            if (TextUtils.isEmpty(phone)) {
                detailView.setText(address.getDetail());
            } else {
                detailView.setText(phone + " · " + address.getDetail());
            }

            editButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onEdit(address);
                }
            });
            deleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onDelete(address);
                }
            });
        }
    }
}

