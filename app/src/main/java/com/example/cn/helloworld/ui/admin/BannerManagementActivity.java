package com.example.cn.helloworld.ui.admin;

import android.content.DialogInterface;
import android.os.Bundle;
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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.cn.helloworld.R;
import com.example.cn.helloworld.data.model.Banner;
import com.example.cn.helloworld.data.repository.BannerRepository;
import com.example.cn.helloworld.data.session.SessionManager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 管理首页轮播图内容的后台页面。
 */
public class BannerManagementActivity extends AppCompatActivity {

    private SessionManager sessionManager;
    private BannerRepository bannerRepository;
    private RecyclerView recyclerView;
    private BannerAdapter adapter;
    private FloatingActionButton fabAdd;

    private final int[] imageOptions = new int[]{
            R.drawable.cover_nishuo,
            R.drawable.cover_baobei,
            R.drawable.cover_friend,
            R.drawable.cover_fenwuhai,
            R.drawable.cover_lisao,
            R.drawable.song_cover
    };

    private final int layoutPreview = R.layout.dialog_banner_editor;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_banner_management);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(R.string.title_admin_manage_banners);
        }

        sessionManager = new SessionManager(this);
        if (!sessionManager.isAdmin()) {
            Toast.makeText(this, R.string.admin_access_denied, Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        bannerRepository = new BannerRepository(this);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerBanners);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter = new BannerAdapter(new ArrayList<Banner>(), new BannerAdapter.Callback() {
            @Override
            public void onEdit(Banner banner) {
                showEditorDialog(banner);
            }

            @Override
            public void onDelete(Banner banner) {
                confirmDelete(banner);
            }
        });
        recyclerView.setAdapter(adapter);

        fabAdd = (FloatingActionButton) findViewById(R.id.fabAddBanner);
        fabAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showEditorDialog(null);
            }
        });

        loadBanners();
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    private void loadBanners() {
        adapter.submit(new ArrayList<Banner>(bannerRepository.getAllBanners()));
    }

    private void confirmDelete(final Banner banner) {
        if (banner == null) {
            return;
        }
        new AlertDialog.Builder(this)
                .setTitle(R.string.dialog_title_delete_banner)
                .setMessage(getString(R.string.dialog_confirm_delete_banner, banner.getTitle()))
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        bannerRepository.deleteBanner(banner.getId());
                        loadBanners();
                    }
                })
                .setNegativeButton(android.R.string.cancel, null)
                .show();
    }

    private void showEditorDialog(@Nullable final Banner banner) {
        View dialogView = LayoutInflater.from(this).inflate(layoutPreview, null, false);
        final EditText titleInput = (EditText) dialogView.findViewById(R.id.editBannerTitle);
        final EditText descInput = (EditText) dialogView.findViewById(R.id.editBannerDescription);
        final Spinner imageSpinner = (Spinner) dialogView.findViewById(R.id.spinnerBannerImage);
        final ImageView preview = (ImageView) dialogView.findViewById(R.id.imageBannerPreview);

        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item,
                getImageOptionNames());
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        imageSpinner.setAdapter(spinnerAdapter);

        if (banner != null) {
            titleInput.setText(banner.getTitle());
            descInput.setText(banner.getDescription());
            int index = findImageIndex(banner.getImageResId());
            if (index >= 0) {
                imageSpinner.setSelection(index);
            }
            preview.setImageResource(banner.getImageResId());
        } else {
            preview.setImageResource(imageOptions[0]);
        }

        imageSpinner.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(android.widget.AdapterView<?> parent, View view, int position, long id) {
                int imageRes = imageOptions[position];
                preview.setImageResource(imageRes);
            }

            @Override
            public void onNothingSelected(android.widget.AdapterView<?> parent) {
                // no-op
            }
        });

        final AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle(banner == null ? R.string.dialog_title_add_banner : R.string.dialog_title_edit_banner)
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
                        String title = titleInput.getText() == null ? "" : titleInput.getText().toString().trim();
                        String description = descInput.getText() == null ? "" : descInput.getText().toString().trim();
                        int imageRes = imageOptions[imageSpinner.getSelectedItemPosition()];

                        if (TextUtils.isEmpty(title)) {
                            titleInput.setError(getString(R.string.error_banner_title_required));
                            return;
                        }
                        if (TextUtils.isEmpty(description)) {
                            descInput.setError(getString(R.string.error_banner_description_required));
                            return;
                        }

                        if (banner == null) {
                            String id = bannerRepository.generateBannerId();
                            bannerRepository.createBanner(new Banner(id, title, description, imageRes));
                        } else {
                            bannerRepository.updateBanner(new Banner(banner.getId(), title, description, imageRes));
                        }
                        loadBanners();
                        dialogInterface.dismiss();
                    }
                });
            }
        });

        dialog.show();
    }

    private List<String> getImageOptionNames() {
        return Arrays.asList(
                getString(R.string.banner_image_birthday),
                getString(R.string.banner_image_public),
                getString(R.string.banner_image_friend),
                getString(R.string.banner_image_fenwuhai),
                getString(R.string.banner_image_lisao),
                getString(R.string.banner_image_song_cover)
        );
    }

    private int findImageIndex(int imageResId) {
        for (int i = 0; i < imageOptions.length; i++) {
            if (imageOptions[i] == imageResId) {
                return i;
            }
        }
        return -1;
    }

    private static class BannerAdapter extends RecyclerView.Adapter<BannerViewHolder> {

        interface Callback {
            void onEdit(Banner banner);
            void onDelete(Banner banner);
        }

        private final List<Banner> banners;
        private final Callback callback;

        BannerAdapter(List<Banner> banners, Callback callback) {
            this.banners = banners;
            this.callback = callback;
        }

        @Override
        public BannerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_admin_banner, parent, false);
            return new BannerViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(BannerViewHolder holder, int position) {
            holder.bind(banners.get(position), callback);
        }

        @Override
        public int getItemCount() {
            return banners == null ? 0 : banners.size();
        }

        void submit(List<Banner> newBanners) {
            banners.clear();
            if (newBanners != null) {
                banners.addAll(newBanners);
            }
            notifyDataSetChanged();
        }
    }

    private static class BannerViewHolder extends RecyclerView.ViewHolder {

        private final ImageView coverView;
        private final TextView titleView;
        private final TextView descriptionView;
        private final Button editButton;
        private final Button deleteButton;

        BannerViewHolder(View itemView) {
            super(itemView);
            coverView = (ImageView) itemView.findViewById(R.id.imageBannerCover);
            titleView = (TextView) itemView.findViewById(R.id.textBannerTitle);
            descriptionView = (TextView) itemView.findViewById(R.id.textBannerDescription);
            editButton = (Button) itemView.findViewById(R.id.buttonEditBanner);
            deleteButton = (Button) itemView.findViewById(R.id.buttonDeleteBanner);
        }

        void bind(final Banner banner, final BannerAdapter.Callback callback) {
            coverView.setImageResource(banner.getImageResId());
            titleView.setText(banner.getTitle());
            descriptionView.setText(banner.getDescription());

            editButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (callback != null) {
                        callback.onEdit(banner);
                    }
                }
            });
            deleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (callback != null) {
                        callback.onDelete(banner);
                    }
                }
            });
        }
    }
}
