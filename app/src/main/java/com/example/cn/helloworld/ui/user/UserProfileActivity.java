package com.example.cn.helloworld.ui.user;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.os.Handler;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import com.example.cn.helloworld.MusicService;
import com.example.cn.helloworld.R;
import com.example.cn.helloworld.data.model.Playlist;
import com.example.cn.helloworld.data.session.SessionManager;
import com.example.cn.helloworld.ui.auth.LoginActivity;
import com.example.cn.helloworld.ui.main.FakeHomeDataSource;
import com.example.cn.helloworld.ui.main.HomeDataSource;
import com.example.cn.helloworld.ui.main.HomeModels;
import com.example.cn.helloworld.ui.main.BannerAdapter;
import com.example.cn.helloworld.ui.main.PlaylistAdapter;
import com.example.cn.helloworld.ui.playlist.PlaylistDetailActivity;
import com.example.cn.helloworld.ui.playlist.PlaylistOverviewActivity;
import com.example.cn.helloworld.ui.widget.MusicFloatingWidget;

public class UserProfileActivity extends AppCompatActivity {

    private SessionManager sessionManager;
    private RecyclerView playlistList;
    private View viewAllPlaylistsButton;
    private HomeDataSource homeDataSource;
    private MusicFloatingWidget musicFloatingWidget;
    private ViewPager bannerPager;
    private View bannerCard;
    private View bannerIndicator;
    private BannerAdapter bannerAdapter;
    private Handler bannerHandler;
    private Runnable bannerRunnable;
    private static final int BANNER_INTERVAL = 4000;
    private int bannerCount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(R.string.title_user_profile);
        }

        sessionManager = new SessionManager(this);
        homeDataSource = new FakeHomeDataSource(this);
        musicFloatingWidget = new MusicFloatingWidget(this);


        // 使用 layout 中真实存在的 ID
        ImageView avatar  = (ImageView) findViewById(R.id.avatarImage);
        TextView username = (TextView) findViewById(R.id.tvUsername);
        TextView userId   = (TextView) findViewById(R.id.tvUserId);
        Button  logout    = (Button) findViewById(R.id.btnLogout);
        playlistList = (RecyclerView) findViewById(R.id.playlistList);
        viewAllPlaylistsButton = findViewById(R.id.button_view_all_playlists);
        bannerPager = (ViewPager) findViewById(R.id.bannerPager);
        bannerCard = findViewById(R.id.bannerCard);
        bannerIndicator = findViewById(R.id.bannerIndicator);

        // 默认头像
        avatar.setImageResource(R.drawable.ic_user_default);

        // 显示用户信息
        username.setText(sessionManager.getUsername());
        userId.setText("UID: " + sessionManager.getUserId());

        // 退出登录
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sessionManager.logout();
                stopService(new Intent(UserProfileActivity.this, MusicService.class));
                sendBroadcast(new Intent("ACTION_STOP"));
                Intent intent = new Intent(UserProfileActivity.this, LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            }
        });

        setupPlaylists();
        setupBanner();
    }

    private void setupBanner() {
        if (bannerPager == null) {
            return;
        }

        final java.util.List<HomeModels.BannerItem> banners = homeDataSource.loadBanners();
        bannerCount = banners.size();

        if (bannerCount == 0) {
            if (bannerCard != null) {
                bannerCard.setVisibility(View.GONE);
            }
            return;
        }

        if (bannerCard != null) {
            bannerCard.setVisibility(View.VISIBLE);
        }

        bannerAdapter = new BannerAdapter(this, banners);
        bannerPager.setAdapter(bannerAdapter);
        bannerPager.setOffscreenPageLimit(3);
        bannerPager.setPageMargin(0);
        bannerPager.setClipToPadding(true);
        bannerPager.setPadding(0, 0, 0, 0);
        bannerPager.setPageTransformer(false, new ViewPager.PageTransformer() {
            @Override
            public void transformPage(View page, float position) {
                float scale = 0.92f - Math.abs(position) * 0.10f;
                if (scale < 0.82f) {
                    scale = 0.82f;
                }
                page.setScaleX(scale);
                page.setScaleY(scale);
                page.setAlpha(1f - Math.abs(position) * 0.3f);
            }
        });

        initBannerIndicator(bannerCount);
        bannerPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                updateBannerIndicator(position);
            }
        });

        bannerHandler = new Handler();
        bannerRunnable = new Runnable() {
            @Override
            public void run() {
                if (bannerCount <= 0 || bannerPager == null) {
                    return;
                }
                int next = (bannerPager.getCurrentItem() + 1) % bannerCount;
                bannerPager.setCurrentItem(next, true);
                bannerHandler.postDelayed(this, BANNER_INTERVAL);
            }
        };
    }

    private void initBannerIndicator(int count) {
        if (!(bannerIndicator instanceof android.widget.LinearLayout)) {
            return;
        }
        android.widget.LinearLayout indicatorLayout = (android.widget.LinearLayout) bannerIndicator;
        indicatorLayout.removeAllViews();

        for (int i = 0; i < count; i++) {
            View dot = new View(this);
            android.widget.LinearLayout.LayoutParams lp =
                    new android.widget.LinearLayout.LayoutParams(18, 8);
            lp.setMargins(6, 0, 6, 0);
            dot.setLayoutParams(lp);
            dot.setBackgroundResource(
                    i == 0 ?
                            R.drawable.bg_indicator_capsule_active :
                            R.drawable.bg_indicator_capsule_inactive
            );
            indicatorLayout.addView(dot);
        }
    }

    private void updateBannerIndicator(int pos) {
        if (!(bannerIndicator instanceof android.widget.LinearLayout)) {
            return;
        }
        android.widget.LinearLayout indicatorLayout = (android.widget.LinearLayout) bannerIndicator;
        int total = indicatorLayout.getChildCount();
        for (int i = 0; i < total; i++) {
            View dot = indicatorLayout.getChildAt(i);
            dot.setBackgroundResource(
                    i == pos ?
                            R.drawable.bg_indicator_capsule_active :
                            R.drawable.bg_indicator_capsule_inactive
            );
        }
    }

    private void setupPlaylists() {
        if (playlistList == null) {
            return;
        }

        playlistList.setLayoutManager(
                new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        playlistList.setNestedScrollingEnabled(false);

        playlistList.setAdapter(new PlaylistAdapter(
                homeDataSource.loadPlaylists(),
                new PlaylistAdapter.OnPlaylistClickListener() {
                    @Override
                    public void onPlaylistClick(Playlist playlist) {
                        startActivity(PlaylistDetailActivity.createIntent(
                                UserProfileActivity.this,
                                playlist.getId()
                        ));
                    }

                    @Override
                    public void onPlaylistClick(HomeModels.Playlist playlist) {
                        // legacy callback ignored
                    }
                }
        ));

        if (viewAllPlaylistsButton != null) {
            viewAllPlaylistsButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(UserProfileActivity.this, PlaylistOverviewActivity.class));
                }
            });
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (musicFloatingWidget != null) {
            musicFloatingWidget.start();
        }
        if (bannerHandler != null && bannerRunnable != null && bannerCount > 0) {
            bannerHandler.postDelayed(bannerRunnable, BANNER_INTERVAL);
        }
    }

    @Override
    protected void onStop() {
        if (musicFloatingWidget != null) {
            musicFloatingWidget.stop();
        }
        if (bannerHandler != null) {
            bannerHandler.removeCallbacks(bannerRunnable);
        }
        super.onStop();
    }
}
