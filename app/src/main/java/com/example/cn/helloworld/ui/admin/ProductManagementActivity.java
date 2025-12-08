package com.example.cn.helloworld.ui.admin;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.cn.helloworld.R;
import com.example.cn.helloworld.data.model.Category;
import com.example.cn.helloworld.data.model.Product;
import com.example.cn.helloworld.data.repository.ProductRepository;
import com.example.cn.helloworld.data.session.SessionManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

public class ProductManagementActivity extends AppCompatActivity {

    private static final int REQUEST_CODE_PICK_PRODUCT_IMAGE = 1101;

    private SessionManager sessionManager;
    private ProductRepository productRepository;
    private RecyclerView recyclerView;
    private ProductAdapter adapter;
    private FloatingActionButton fabAdd;
    private List<Category> categories = new ArrayList<>();
    private final int[] imageOptions = new int[]{
            R.drawable.cover_nishuo,
            R.drawable.cover_baobei,
            R.drawable.cover_friend,
            R.drawable.cover_fenwuhai,
            R.drawable.cover_lisao,
            R.drawable.song_cover
    };

    private ImageView activeImagePreview;
    private TextView activeImageHint;
    private Spinner activeImageSpinner;
    private String selectedImageUri;
    private int selectedImageResId;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_product_management);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(R.string.title_admin_manage_products);
        }

        sessionManager = new SessionManager(this);
        if (!sessionManager.isAdmin()) {
            Toast.makeText(this, R.string.admin_access_denied, Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        productRepository = new ProductRepository(this);
        categories = productRepository.getCategories();

        recyclerView = (RecyclerView) findViewById(R.id.recyclerProducts);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ProductAdapter(new ArrayList<Product>(), new ProductAdapter.Callback() {
            @Override
            public void onEdit(Product product) {
                showProductEditor(product);
            }

            @Override
            public void onToggle(Product product) {
                productRepository.setProductActive(product.getId(), !product.isActive());
                loadProducts();
            }
        });
        recyclerView.setAdapter(adapter);

        fabAdd = (FloatingActionButton) findViewById(R.id.fabAddProduct);
        fabAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showProductEditor(null);
            }
        });

        loadProducts();
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_PICK_PRODUCT_IMAGE && resultCode == RESULT_OK && data != null) {
            Uri uri = data.getData();
            if (uri != null) {
                selectedImageUri = uri.toString();
                selectedImageResId = 0;
                updateImagePreview(activeImagePreview, activeImageHint, selectedImageUri,
                        selectedImageResId > 0 ? selectedImageResId : imageOptions[0]);
                if (activeImageSpinner != null) {
                    activeImageSpinner.setSelection(0);
                }
            }
        }
    }

    private void loadProducts() {
        adapter.submit(productRepository.getProductsForAdmin());
    }

    private void showProductEditor(@Nullable final Product product) {
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_product_editor, null, false);
        final EditText nameInput = (EditText) dialogView.findViewById(R.id.editProductName);
        final EditText priceInput = (EditText) dialogView.findViewById(R.id.editProductPrice);
        final EditText inventoryInput = (EditText) dialogView.findViewById(R.id.editProductInventory);
        final EditText descriptionInput = (EditText) dialogView.findViewById(R.id.editProductDescription);
        final CheckBox activeCheckBox = (CheckBox) dialogView.findViewById(R.id.checkboxProductActive);
        final CheckBox homeSwitchCheckBox = (CheckBox) dialogView.findViewById(R.id.checkboxProductHomeSwitch);
        final Spinner categorySpinner = (Spinner) dialogView.findViewById(R.id.spinnerProductCategory);
        final Spinner imageSpinner = (Spinner) dialogView.findViewById(R.id.spinnerProductImage);
        final ImageView imagePreview = (ImageView) dialogView.findViewById(R.id.imageProductPreview);
        final TextView imageHint = (TextView) dialogView.findViewById(R.id.textProductImageHint);
        Button pickImageButton = (Button) dialogView.findViewById(R.id.buttonPickProductImage);

        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item,
                buildCategoryNames());
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categorySpinner.setAdapter(spinnerAdapter);

        ArrayAdapter<String> imageAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item,
                getResources().getStringArray(R.array.product_image_names));
        imageAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        imageSpinner.setAdapter(imageAdapter);

        activeImagePreview = imagePreview;
        activeImageHint = imageHint;
        activeImageSpinner = imageSpinner;
        selectedImageUri = product != null ? product.getCoverUrl() : null;
        selectedImageResId = (product != null && product.getImageResId() > 0)
                ? product.getImageResId()
                : imageOptions[0];

        if (product != null) {
            nameInput.setText(product.getName());
            priceInput.setText(String.format(Locale.getDefault(), "%.2f", product.getPrice()));
            inventoryInput.setText(String.valueOf(product.getInventory()));
            descriptionInput.setText(product.getDescription());
            activeCheckBox.setChecked(product.isActive());
            homeSwitchCheckBox.setChecked(product.isFeaturedOnHome());
            int index = findCategoryIndex(product.getCategoryId());
            if (index >= 0) {
                categorySpinner.setSelection(index);
            }
            int imageIndex = findImageIndex(product.getImageResId());
            if (imageIndex >= 0) {
                imageSpinner.setSelection(imageIndex);
            }
        } else {
            activeCheckBox.setChecked(true);
            homeSwitchCheckBox.setChecked(true);
        }

        updateImagePreview(imagePreview, imageHint, selectedImageUri, selectedImageResId);

        final boolean[] skipFirstSpinnerEvent = new boolean[]{!TextUtils.isEmpty(selectedImageUri)};
        imageSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (skipFirstSpinnerEvent[0]) {
                    skipFirstSpinnerEvent[0] = false;
                    return;
                }
                selectedImageResId = imageOptions[position];
                selectedImageUri = null;
                updateImagePreview(imagePreview, imageHint, selectedImageUri, selectedImageResId);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // no-op
            }
        });

        pickImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startPickProductImage();
            }
        });

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle(product == null ? R.string.dialog_title_add_product : R.string.dialog_title_edit_product)
                .setView(dialogView)
                .setPositiveButton(android.R.string.ok, null)
                .setNegativeButton(android.R.string.cancel, null)
                .create();

        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(final DialogInterface dialogInterface) {
                AlertDialog alertDialog = (AlertDialog) dialogInterface;
                Button button = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE);
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String name = nameInput.getText() == null ? "" : nameInput.getText().toString().trim();
                        String priceText = priceInput.getText() == null ? "" : priceInput.getText().toString().trim();
                        String inventoryText = inventoryInput.getText() == null ? "" : inventoryInput.getText().toString().trim();
                        String description = descriptionInput.getText() == null ? "" : descriptionInput.getText().toString().trim();
                        boolean active = activeCheckBox.isChecked();
                        boolean featuredOnHome = homeSwitchCheckBox.isChecked();
                        String categoryId = categories.isEmpty() ? null : categories.get(categorySpinner.getSelectedItemPosition()).getId();
                        String resolvedCategoryId = (categoryId == null && !categories.isEmpty())
                                ? categories.get(0).getId()
                                : categoryId;
                        String coverUri = selectedImageUri;
                        int imageResId = TextUtils.isEmpty(coverUri)
                                ? selectedImageResId
                                : 0;

                        if (TextUtils.isEmpty(name)) {
                            nameInput.setError(getString(R.string.error_product_name_required));
                            return;
                        }
                        double price;
                        int inventory;
                        try {
                            price = Double.parseDouble(priceText);
                        } catch (NumberFormatException exception) {
                            priceInput.setError(getString(R.string.error_product_price_invalid));
                            return;
                        }
                        try {
                            inventory = Integer.parseInt(inventoryText);
                        } catch (NumberFormatException exception) {
                            inventoryInput.setError(getString(R.string.error_product_inventory_invalid));
                            return;
                        }

                        if (product == null) {
                            String productId = productRepository.generateProductId(resolvedCategoryId);
                            Product newProduct = new Product(
                                    productId,
                                    name,
                                    description,
                                    price,
                                    inventory,
                                    resolvedCategoryId,
                                    5,
                                    Collections.<String>emptyList(),
                                    Collections.<String>emptyList(),
                                    active,
                                    coverUri,
                                    null,
                                    null,
                                    imageResId,
                                    "",
                                    null,
                                    featuredOnHome
                            );
                            productRepository.saveProduct(newProduct);
                        } else {
                            productRepository.updateProductDetails(product.getId(),
                                    name,
                                    description,
                                    price,
                                    inventory,
                                    active,
                                    resolvedCategoryId,
                                    featuredOnHome,
                                    imageResId,
                                    coverUri);
                        }
                        dialogInterface.dismiss();
                        loadProducts();
                    }
                });
            }
        });

        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                activeImagePreview = null;
                activeImageHint = null;
                activeImageSpinner = null;
                selectedImageUri = null;
                selectedImageResId = 0;
            }
        });

        dialog.show();
    }

    private void updateImagePreview(@Nullable ImageView preview, @Nullable TextView hint,
                                    @Nullable String coverUri, int imageResId) {
        if (preview == null || hint == null) {
            return;
        }
        if (!TextUtils.isEmpty(coverUri)) {
            preview.setImageURI(Uri.parse(coverUri));
            hint.setText(getString(R.string.product_image_local_source, extractDisplayName(coverUri)));
        } else {
            preview.setImageResource(imageResId);
            hint.setText(R.string.product_image_builtin_hint);
        }
    }

    private String extractDisplayName(String uriString) {
        if (TextUtils.isEmpty(uriString)) {
            return "";
        }
        try {
            Uri uri = Uri.parse(uriString);
            String segment = uri.getLastPathSegment();
            return TextUtils.isEmpty(segment) ? uriString : segment;
        } catch (Exception exception) {
            return uriString;
        }
    }

    private void startPickProductImage() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(Intent.createChooser(intent, getString(R.string.action_pick_product_image)),
                REQUEST_CODE_PICK_PRODUCT_IMAGE);
    }

    private List<String> buildCategoryNames() {
        List<String> names = new ArrayList<>();
        for (Category category : categories) {
            names.add(category.getName());
        }
        if (names.isEmpty()) {
            names.add(getString(R.string.product_category_default));
        }
        return names;
    }

    private int findCategoryIndex(String categoryId) {
        if (TextUtils.isEmpty(categoryId)) {
            return -1;
        }
        for (int i = 0; i < categories.size(); i++) {
            if (categoryId.equals(categories.get(i).getId())) {
                return i;
            }
        }
        return -1;
    }

    private int findImageIndex(int imageResId) {
        for (int i = 0; i < imageOptions.length; i++) {
            if (imageOptions[i] == imageResId) {
                return i;
            }
        }
        return 0;
    }

    private static class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> {

        interface Callback {
            void onEdit(Product product);

            void onToggle(Product product);
        }

        private final List<Product> products;
        private final Callback callback;

        ProductAdapter(List<Product> products, Callback callback) {
            this.products = products;
            this.callback = callback;
        }

        void submit(List<Product> newProducts) {
            products.clear();
            if (newProducts != null) {
                products.addAll(newProducts);
            }
            notifyDataSetChanged();
        }

        @NonNull
        @Override
        public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_admin_product, parent, false);
            return new ProductViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
            final Product product = products.get(position);
            holder.bind(product, callback);
        }

        @Override
        public int getItemCount() {
            return products.size();
        }

        static class ProductViewHolder extends RecyclerView.ViewHolder {

            private final TextView nameView;
            private final TextView descriptionView;
            private final TextView priceView;
            private final TextView statusView;
            private final Button editButton;
            private final Button toggleButton;

            ProductViewHolder(View itemView) {
                super(itemView);
                nameView = (TextView) itemView.findViewById(R.id.textProductName);
                descriptionView = (TextView) itemView.findViewById(R.id.textProductDescription);
                priceView = (TextView) itemView.findViewById(R.id.textProductPrice);
                statusView = (TextView) itemView.findViewById(R.id.textProductStatus);
                editButton = (Button) itemView.findViewById(R.id.buttonEditProduct);
                toggleButton = (Button) itemView.findViewById(R.id.buttonToggleProduct);
            }

            void bind(final Product product, final Callback callback) {
                nameView.setText(product.getName());
                descriptionView.setText(product.getDescription());
                priceView.setText(String.format(Locale.getDefault(), "¥%.2f · %d", product.getPrice(), product.getInventory()));
                String onlineStatus = itemView.getContext().getString(
                        product.isActive() ? R.string.product_status_online : R.string.product_status_offline);
                String homeSwitchStatus = itemView.getContext().getString(
                        product.isFeaturedOnHome() ? R.string.product_home_switch_on : R.string.product_home_switch_off);
                statusView.setText(itemView.getContext().getString(
                        R.string.product_status_with_home_switch,
                        onlineStatus,
                        homeSwitchStatus));
                toggleButton.setText(product.isActive() ? R.string.product_action_offline : R.string.product_action_online);

                editButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (callback != null) {
                            callback.onEdit(product);
                        }
                    }
                });

                toggleButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (callback != null) {
                            callback.onToggle(product);
                        }
                    }
                });
            }
        }
    }
}
