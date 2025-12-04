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
import android.widget.TextView;

import com.example.cn.helloworld.R;
import com.example.cn.helloworld.data.model.Playlist;
import com.example.cn.helloworld.data.model.Product;
import com.example.cn.helloworld.ui.catalog.ProductDetailActivity;
import com.example.cn.helloworld.ui.catalog.ProductListActivity;
import com.example.cn.helloworld.ui.playlist.PlaylistDetailActivity;
import com.example.cn.helloworld.ui.playlist.PlaylistOverviewActivity;
import com.example.cn.helloworld.ui.product.ReviewWallActivity;

import java.util.List;

/**
 * 首页内容 Fragment，承载轮播、分类、歌单和应援任务模块。
 */
public class HomeFragment extends Fragment {

    private ViewPager bannerPager;
    private RecyclerView categoryList;
    private RecyclerView playlistList;
    private RecyclerView taskList;
    private RecyclerView productList;
    private View viewAllPlaylistsButton;
    private View viewAllProductsButton;
    private View bannerCard;
    private LinearLayout bannerIndicator;

    private HomeDataSource dataSource;

    // Banner 相关
    private Handler bannerHandler;
    private Runnable bannerRunnable;
    private static final int BANNER_INTERVAL = 4000; // 4 秒
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

        bannerPager = (ViewPager) root.findViewById(R.id.bannerPager);
        categoryList = (RecyclerView) root.findViewById(R.id.categoryList);
        playlistList = (RecyclerView) root.findViewById(R.id.playlistList);
        taskList = (RecyclerView) root.findViewById(R.id.taskList);
        productList = (RecyclerView) root.findViewById(R.id.productList);
        viewAllPlaylistsButton = root.findViewById(R.id.button_view_all_playlists);
        viewAllProductsButton = root.findViewById(R.id.button_view_all_products);
        bannerCard = root.findViewById(R.id.bannerCard);
        bannerIndicator = (LinearLayout) root.findViewById(R.id.bannerIndicator);

        setupBanner();
        setupCategories();
        setupProducts();
        setupPlaylists();
        setupTasks();

        return root;
    }

    /**
     * Banner：设置适配器 + 自动轮播 + 胶囊指示器
     */
    private void setupBanner() {
        Context ctx = getContext();
        if (ctx == null) return;

        final List<HomeModels.BannerItem> banners = dataSource.loadBanners();
        bannerCount = banners.size();

        if (bannerCount <= 0) {
            if (bannerCard != null) {
                bannerCard.setVisibility(View.GONE);
            }
            return;
        }

        if (bannerCard != null) {
            bannerCard.setVisibility(View.VISIBLE);
        }

        bannerAdapter = new BannerAdapter(ctx, banners);
        bannerPager.setAdapter(bannerAdapter);
        bannerPager.setOffscreenPageLimit(3);

        // 初始化胶囊指示器
        initBannerIndicator(bannerCount);

        // ViewPager 页面切换时更新指示器
        bannerPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                currentBannerIndex = position % bannerCount;
                updateBannerIndicator(currentBannerIndex);
            }
        });

        // 自动轮播
        bannerHandler = new Handler();
        bannerRunnable = new Runnable() {
            @Override
            public void run() {
                if (bannerCount <= 0 || bannerPager == null || bannerAdapter == null) {
                    return;
                }
                int next = (bannerPager.getCurrentItem() + 1) % bannerAdapter.getCount();
                bannerPager.setCurrentItem(next, true);
                bannerHandler.postDelayed(this, BANNER_INTERVAL);
            }
        };
    }

    /**
     * 创建胶囊指示器
     */
    private void initBannerIndicator(int count) {
        bannerIndicator.removeAllViews();
        if (count <= 0 || getContext() == null) return;

        for (int i = 0; i < count; i++) {
            View dot = new View(getContext());
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);
            lp.setMargins(6, 0, 6, 0);
            dot.setLayoutParams(lp);
            dot.setBackgroundResource(
                    i == 0 ? R.drawable.bg_indicator_capsule_active
                            : R.drawable.bg_indicator_capsule_inactive
            );
            bannerIndicator.addView(dot);
        }
    }

    /**
     * 切换胶囊的选中状态
     */
    private void updateBannerIndicator(int position) {
        int childCount = bannerIndicator.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View child = bannerIndicator.getChildAt(i);
            child.setBackgroundResource(
                    i == position
                            ? R.drawable.bg_indicator_capsule_active
                            : R.drawable.bg_indicator_capsule_inactive
            );
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.GINGERBREAD)
    private void setupCategories() {
        Context ctx = getContext();
        if (ctx == null) return;

        categoryList.setLayoutManager(new GridLayoutManager(ctx, 3));
        categoryList.setNestedScrollingEnabled(false);
        categoryList.setAdapter(new CategoryAdapter(dataSource.loadCategories(),
                new CategoryAdapter.OnCategoryClickListener() {
                    @Override
                    public void onCategoryClick(HomeModels.HomeCategory category) {
                        handleCategoryClick(category);
                    }
                }));
    }

    private void handleCategoryClick(HomeModels.HomeCategory category) {
        Context context = getContext();
        if (context == null || category == null) {
            return;
        }
        String action = category.getAction();
        if ("action_stage_review".equals(action)) {
            startActivity(new Intent(context, PlaylistOverviewActivity.class));
        } else if ("action_new_arrival".equals(action)) {
            ProductListActivity.start(context);
        } else if ("action_calendar".equals(action) || "action_check_in".equals(action)) {
            if (getActivity() != null) {
                BottomNavigationView nav = (BottomNavigationView) getActivity().findViewById(R.id.bottom_navigation);
                if (nav != null) {
                    nav.setSelectedItemId(R.id.navigation_support);
                }
            }
        } else if ("action_profile".equals(action)) {
            if (getActivity() != null) {
                BottomNavigationView nav = (BottomNavigationView) getActivity().findViewById(R.id.bottom_navigation);
                if (nav != null) {
                    nav.setSelectedItemId(R.id.navigation_profile);
                }
            }
        } else if ("action_review_wall".equals(action)) {
            startActivity(ReviewWallActivity.createIntent(context, null, null));
        } else if ("action_news".equals(action)) {
            startActivity(new Intent(context, PlaylistOverviewActivity.class));
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.GINGERBREAD)
    private void setupProducts() {
        final Context context = getContext();
        if (context == null) return;

        LinearLayoutManager layoutManager =
                new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);
        productList.setLayoutManager(layoutManager);
        productList.setNestedScrollingEnabled(false);
        final HomeProductAdapter adapter = new HomeProductAdapter(
                dataSource.loadFeaturedProducts(),
                new HomeProductAdapter.OnProductClickListener() {
                    @Override
                    public void onProductClick(Product product) {
                        ProductDetailActivity.start(context, product.getId());
                    }
                }
        );
        productList.setAdapter(adapter);

        viewAllProductsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ProductListActivity.start(context);
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.GINGERBREAD)
    private void setupPlaylists() {
        final Context context = getContext();
        if (context == null) return;

        LinearLayoutManager layoutManager =
                new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);
        playlistList.setLayoutManager(layoutManager);
        playlistList.setNestedScrollingEnabled(false);

        // Snap 效果（类似 ViewPager 翻页）
        SnapHelper snapHelper = new LinearSnapHelper();
        snapHelper.attachToRecyclerView(playlistList);

        playlistList.setAdapter(new PlaylistAdapter(
                dataSource.loadPlaylists(),
                new PlaylistAdapter.OnPlaylistClickListener() {
                    @Override
                    public void onPlaylistClick(Playlist playlist) {
                        Context ctx = getContext();
                        if (ctx != null) {
                            startActivity(PlaylistDetailActivity.createIntent(
                                    ctx,
                                    playlist.getId()
                            ));
                        }
                    }

                    @Override
                    public void onPlaylistClick(HomeModels.Playlist playlist) {
                        // 兼容旧接口，不用处理
                    }
                }));

        viewAllPlaylistsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Context ctx = getContext();
                if (ctx != null) {
                    startActivity(new Intent(ctx, PlaylistOverviewActivity.class));
                }
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

    // ---------- 自动轮播生命周期控制 ----------

    @Override
    public void onResume() {
        super.onResume();
        if (bannerHandler != null && bannerRunnable != null && bannerCount > 0) {
            bannerHandler.removeCallbacks(bannerRunnable);
            bannerHandler.postDelayed(bannerRunnable, BANNER_INTERVAL);
        }
    }

    @Override
    public void onPause() {
        if (bannerHandler != null && bannerRunnable != null) {
            bannerHandler.removeCallbacks(bannerRunnable);
        }
        super.onPause();
    }

    @Override
    public void onDestroyView() {
        if (bannerHandler != null && bannerRunnable != null) {
            bannerHandler.removeCallbacks(bannerRunnable);
        }
        super.onDestroyView();
    }
}
