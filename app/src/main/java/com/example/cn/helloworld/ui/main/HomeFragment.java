package com.example.cn.helloworld.ui.main;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.LinearSnapHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SnapHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.example.cn.helloworld.R;
import com.example.cn.helloworld.data.model.Playlist;
import com.example.cn.helloworld.data.model.Product;
import com.example.cn.helloworld.ui.catalog.ProductDetailActivity;
import com.example.cn.helloworld.ui.catalog.ProductListActivity;
import com.example.cn.helloworld.ui.playlist.PlaylistDetailActivity;
import com.example.cn.helloworld.ui.playlist.PlaylistOverviewActivity;
import com.example.cn.helloworld.ui.product.ReviewWallActivity;

import java.util.List;

public class HomeFragment extends Fragment {

    private ViewPager bannerPager;
    private LinearLayout bannerIndicator;
    private View bannerCard;

    private RecyclerView categoryList;
    private RecyclerView productList;
    private RecyclerView playlistList;
    private RecyclerView taskList;

    private View viewAllPlaylistsButton;
    private View viewAllProductsButton;

    private HomeDataSource dataSource;

    // Banner 自动轮播
    private Handler bannerHandler;
    private Runnable bannerRunnable;
    private static final int BANNER_INTERVAL = 4000;
    private int bannerCount = 0;
    private int currentBannerIndex = 0;

    private BannerAdapter bannerAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_home, container, false);

        dataSource = new FakeHomeDataSource(root.getContext());

        // 绑定控件
        bannerPager = (ViewPager) root.findViewById(R.id.bannerPager);
        bannerIndicator = (LinearLayout) root.findViewById(R.id.bannerIndicator);
        bannerCard = root.findViewById(R.id.bannerCard);

        categoryList = (RecyclerView) root.findViewById(R.id.categoryList);
        productList = (RecyclerView) root.findViewById(R.id.productList);
        playlistList = (RecyclerView) root.findViewById(R.id.playlistList);
        taskList = (RecyclerView) root.findViewById(R.id.taskList);

        viewAllPlaylistsButton = root.findViewById(R.id.button_view_all_playlists);
        viewAllProductsButton = root.findViewById(R.id.button_view_all_products);

        setupBanner();
        setupCategories();
        setupProducts();
        setupPlaylists();
        setupTasks();

        return root;
    }

    /**
     * ⭐ 高颜值轮播图
     */
    private void setupBanner() {
        Context ctx = getContext();
        if (ctx == null) return;

        final List<HomeModels.BannerItem> list = dataSource.loadBanners();
        bannerCount = list.size();

        if (bannerCount <= 0) {
            bannerCard.setVisibility(View.GONE);
            return;
        }

        bannerCard.setVisibility(View.VISIBLE);
        bannerAdapter = new BannerAdapter(ctx, list);
        bannerPager.setAdapter(bannerAdapter);
        bannerPager.setOffscreenPageLimit(3);

        // ⭐ 关键：左右露边 + 缩放
        bannerPager.setPageMargin(24);
        bannerPager.setClipToPadding(false);
        bannerPager.setPadding(60, 0, 60, 0);

        bannerPager.setPageTransformer(false, new ViewPager.PageTransformer() {
            @Override
            public void transformPage(View page, float position) {
                float scale = 0.92f - Math.abs(position) * 0.10f;
                if (scale < 0.82f) scale = 0.82f;

                page.setScaleX(scale);
                page.setScaleY(scale);
                page.setAlpha(1f - Math.abs(position) * 0.3f);
            }
        });

        initBannerIndicator(bannerCount);

        bannerPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int pos) {
                currentBannerIndex = pos;
                updateBannerIndicator(pos);
            }
        });

        // ⭐ 自动轮播
        bannerHandler = new Handler();
        bannerRunnable = new Runnable() {
            @Override
            public void run() {
                if (bannerCount <= 0) return;

                int next = (bannerPager.getCurrentItem() + 1) % bannerCount;
                bannerPager.setCurrentItem(next, true);

                bannerHandler.postDelayed(this, BANNER_INTERVAL);
            }
        };
    }
    private void initBannerIndicator(int count) {
        bannerIndicator.removeAllViews();

        Context ctx = getContext();
        if (ctx == null) return;

        for (int i = 0; i < count; i++) {
            View dot = new View(ctx);
            LinearLayout.LayoutParams lp =
                    new LinearLayout.LayoutParams(18, 8);
            lp.setMargins(6, 0, 6, 0);
            dot.setLayoutParams(lp);

            dot.setBackgroundResource(
                    i == 0 ?
                            R.drawable.bg_indicator_capsule_active :
                            R.drawable.bg_indicator_capsule_inactive
            );

            bannerIndicator.addView(dot);
        }
    }

    private void updateBannerIndicator(int pos) {
        int total = bannerIndicator.getChildCount();

        for (int i = 0; i < total; i++) {
            View dot = bannerIndicator.getChildAt(i);
            dot.setBackgroundResource(
                    i == pos ?
                            R.drawable.bg_indicator_capsule_active :
                            R.drawable.bg_indicator_capsule_inactive
            );
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (bannerHandler != null && bannerRunnable != null && bannerCount > 0) {
            bannerHandler.postDelayed(bannerRunnable, BANNER_INTERVAL);
        }
    }

    @Override
    public void onPause() {
        if (bannerHandler != null) {
            bannerHandler.removeCallbacks(bannerRunnable);
        }
        super.onPause();
    }

    @Override
    public void onDestroyView() {
        if (bannerHandler != null) {
            bannerHandler.removeCallbacks(bannerRunnable);
        }
        super.onDestroyView();
    }

    // --------------------
    // 分类、商品、歌单、任务（原样保留）
    // --------------------

    @RequiresApi(api = Build.VERSION_CODES.GINGERBREAD)
    private void setupCategories() {
        Context ctx = getContext();
        if (ctx == null) return;

        categoryList.setLayoutManager(new GridLayoutManager(ctx, 3));
        categoryList.setNestedScrollingEnabled(false);
        categoryList.setAdapter(new CategoryAdapter(
                dataSource.loadCategories(),
                new CategoryAdapter.OnCategoryClickListener() {
                    @Override
                    public void onCategoryClick(HomeModels.HomeCategory category) {
                        handleCategoryClick(category);
                    }
                }
        ));
    }

    private void handleCategoryClick(HomeModels.HomeCategory category) {
        Context ctx = getContext();
        if (ctx == null || category == null) return;

        String action = category.getAction();

        if ("action_stage_review".equals(action)) {
            startActivity(new Intent(ctx, PlaylistOverviewActivity.class));

        } else if ("action_new_arrival".equals(action)) {
            ProductListActivity.start(ctx);

        } else if ("action_calendar".equals(action)
                || "action_check_in".equals(action)) {

            if (getActivity() != null) {
                BottomNavigationView nav =
                        (BottomNavigationView) getActivity().findViewById(R.id.bottom_navigation);
                if (nav != null) nav.setSelectedItemId(R.id.navigation_support);
            }

        } else if ("action_profile".equals(action)) {
            if (getActivity() != null) {
                BottomNavigationView nav =
                        (BottomNavigationView) getActivity().findViewById(R.id.bottom_navigation);
                if (nav != null) nav.setSelectedItemId(R.id.navigation_profile);
            }

        } else if ("action_review_wall".equals(action)) {
            startActivity(ReviewWallActivity.createIntent(ctx, null, null));
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.GINGERBREAD)
    private void setupProducts() {
        final Context ctx = getContext();
        if (ctx == null) return;

        LinearLayoutManager layout =
                new LinearLayoutManager(ctx, LinearLayoutManager.HORIZONTAL, false);

        productList.setLayoutManager(layout);
        productList.setNestedScrollingEnabled(false);

        HomeProductAdapter adapter = new HomeProductAdapter(
                dataSource.loadFeaturedProducts(),
                new HomeProductAdapter.OnProductClickListener() {
                    @Override
                    public void onProductClick(Product product) {
                        ProductDetailActivity.start(ctx, product.getId());
                    }
                }
        );

        productList.setAdapter(adapter);

        viewAllProductsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ProductListActivity.start(ctx);
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.GINGERBREAD)
    private void setupPlaylists() {
        final Context ctx = getContext();
        if (ctx == null) return;

        LinearLayoutManager layout =
                new LinearLayoutManager(ctx, LinearLayoutManager.HORIZONTAL, false);

        playlistList.setLayoutManager(layout);
        playlistList.setNestedScrollingEnabled(false);

        SnapHelper snap = new LinearSnapHelper();
        snap.attachToRecyclerView(playlistList);

        playlistList.setAdapter(new PlaylistAdapter(
                dataSource.loadPlaylists(),
                new PlaylistAdapter.OnPlaylistClickListener() {
                    @Override
                    public void onPlaylistClick(Playlist playlist) {
                        startActivity(PlaylistDetailActivity.createIntent(
                                ctx, playlist.getId()
                        ));
                    }

                    @Override
                    public void onPlaylistClick(HomeModels.Playlist playlist) { }
                }
        ));

        viewAllPlaylistsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ctx, PlaylistOverviewActivity.class));
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.GINGERBREAD)
    private void setupTasks() {
        Context ctx = getContext();
        if (ctx == null) return;

        taskList.setLayoutManager(new LinearLayoutManager(ctx));
        taskList.setNestedScrollingEnabled(false);
        taskList.setAdapter(new TaskAdapter(dataSource.loadSupportTasks()));
    }
}
