package com.example.cn.helloworld.ui.main;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.cn.helloworld.MusicActivity;
import com.example.cn.helloworld.MusicService;
import com.example.cn.helloworld.R;
import com.example.cn.helloworld.util.BottomNavigationViewHelper;
import com.example.cn.helloworld.ui.order.CartFragment;
import com.example.cn.helloworld.ui.playlist.PlaylistLibraryFragment;
import com.example.cn.helloworld.ui.user.UserCenterFragment;

import com.example.cn.helloworld.data.session.SessionManager;

/**
 * 应用主界面的入口 Activity：
 * 1. 控制底部导航切换不同 Fragment（满足表格中的 Activity、Fragment 要求）。
 * 2. 管理悬浮音乐控件的布局与事件（涉及常见控件与布局容器的使用）。
 * 3. 记录当前选中的导航项以便恢复（演示基础数据存储/状态保存）。
 */
public class MainActivity extends AppCompatActivity
        implements BottomNavigationView.OnNavigationItemSelectedListener {

    private static final String TAG_HOME = "home";
    private static final String TAG_CART = "cart";
    private static final String TAG_SUPPORT = "support";
    private static final String TAG_PLAYLIST = "playlist";
    private static final String TAG_PROFILE = "profile";

    private static final String KEY_SELECTED_NAV = "selected_nav";

    private Fragment homeFragment;
    private Fragment cartFragment;
    private Fragment supportFragment;
    private Fragment playlistFragment;
    private Fragment profileFragment;

    private Fragment activeFragment;

    private int selectedNavId = R.id.navigation_home;

    private SessionManager sessionManager;
    private boolean isAdmin = false;

    // ============= 悬浮播放器 =============
    private View floatingMusicContainer;
    private View floatingMusicCard;
    private ImageView floatingMusicCover;
    private ImageView floatingMusicBubble;
    private TextView floatingMusicTitle;
    private TextView floatingMusicSubtitle;
    private ImageButton floatingMusicPlayPause;
    private ImageButton floatingMusicClose;

    private boolean floatingMusicPlaying = false;
    private boolean floatingMusicManuallyClosed = false;
    private boolean floatingMusicCollapsed = false;

    private BroadcastReceiver floatingMusicReceiver;
    private boolean floatingMusicReceiverRegistered = false;

    private float lastFloatingX;
    private float lastFloatingY;
    private boolean floatingDragging = false;
    private int floatingTouchSlop;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sessionManager = new SessionManager(this);
        isAdmin = sessionManager.isAdmin();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle(R.string.title_main);

        if (savedInstanceState != null) {
            selectedNavId = savedInstanceState.getInt(KEY_SELECTED_NAV, R.id.navigation_home);
        }

        initFragments();

        BottomNavigationView bottomNavigationView =
                (BottomNavigationView) findViewById(R.id.bottom_navigation);
        BottomNavigationViewHelper.disableShiftMode(bottomNavigationView);
        bottomNavigationView.setOnNavigationItemSelectedListener(this);
        bottomNavigationView.setSelectedItemId(selectedNavId);

        initFloatingMusicWidget();
    }

    // ========================================
    // fragment 初始化（只 add 首页）
    // ========================================
    /**
     * 初始化底部五个 Tab 对应的 Fragment，只 add 首页，其他在首次点击时再 add，
     * 可以避免重复创建导致的界面闪动或崩溃，符合“Activity 与 Fragment 之间切换”考点。
     */
    private void initFragments() {
        FragmentManager fm = getSupportFragmentManager();

        homeFragment = new HomeFragment();
        cartFragment = new CartFragment();
        supportFragment = new SupportTasksFragment();
        playlistFragment = new PlaylistLibraryFragment();
        profileFragment = new UserCenterFragment();

        activeFragment = homeFragment;

        fm.beginTransaction()
                .add(R.id.main_container, homeFragment, TAG_HOME)
                .commit();
    }

    @Override
    /**
     * 处理底部导航点击事件，按不同导航项切换显示的 Fragment，同时更新标题栏，
     * 展示了常见控件（BottomNavigationView、Toolbar）的交互写法。
     */
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        selectedNavId = item.getItemId();

        if (isAdmin && item.getItemId() == R.id.navigation_cart) {
            BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_navigation);
            if (bottomNavigationView != null) {
                int currentNav = getActiveNavId();
                bottomNavigationView.setSelectedItemId(currentNav);
                selectedNavId = currentNav;
                updateTitleForNav(currentNav);
            }
            android.widget.Toast.makeText(this, R.string.admin_cart_hidden, android.widget.Toast.LENGTH_SHORT).show();
            return false;
        }

        switch (item.getItemId()) {
            case R.id.navigation_home:
                switchFragment(homeFragment, TAG_HOME);
                setTitle(R.string.title_main);
                return true;

            case R.id.navigation_cart:
                switchFragment(cartFragment, TAG_CART);
                setTitle(R.string.nav_cart);
                return true;

            case R.id.navigation_support:
                switchFragment(supportFragment, TAG_SUPPORT);
                setTitle(R.string.nav_support);
                return true;

            case R.id.navigation_playlist:
                switchFragment(playlistFragment, TAG_PLAYLIST);
                setTitle(R.string.nav_playlist);
                return true;

            case R.id.navigation_profile:
                switchFragment(profileFragment, TAG_PROFILE);
                setTitle(R.string.title_user_profile);
                return true;

            default:
                return false;
        }
    }

    // ========================================
    // 核心部分：fragment 不重复 add（修复崩溃）
    // ========================================
    /**
     * 统一的 Fragment 切换方法：隐藏当前、按需 add 目标，再 show。
     * 既满足表格中“Fragment 的使用”，也说明了布局容器 FrameLayout 的作用。
     */
    private void switchFragment(Fragment target, String tag) {
        if (activeFragment == target) return;

        FragmentManager fm = getSupportFragmentManager();
        android.support.v4.app.FragmentTransaction ft = fm.beginTransaction();

        ft.hide(activeFragment);

        if (!target.isAdded()) {
            ft.add(R.id.main_container, target, tag);
        } else {
            ft.show(target);
        }

        ft.commitAllowingStateLoss();
        activeFragment = target;
    }

    private int getActiveNavId() {
        if (activeFragment == supportFragment) return R.id.navigation_support;
        if (activeFragment == playlistFragment) return R.id.navigation_playlist;
        if (activeFragment == profileFragment) return R.id.navigation_profile;
        if (activeFragment == cartFragment) return R.id.navigation_cart;
        return R.id.navigation_home;
    }

    private void updateTitleForNav(int navId) {
        switch (navId) {
            case R.id.navigation_support: setTitle(R.string.nav_support); break;
            case R.id.navigation_playlist: setTitle(R.string.nav_playlist); break;
            case R.id.navigation_profile: setTitle(R.string.title_user_profile); break;
            case R.id.navigation_cart: setTitle(R.string.nav_cart); break;
            default: setTitle(R.string.title_main);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        registerFloatingMusicReceiver();
    }

    @Override
    protected void onStop() {
        unregisterFloatingMusicReceiver();
        super.onStop();
    }

    // ========================================
    // 悬浮音乐窗口（你的代码保持不动，仅修复生命周期）
    // ========================================
    private void initFloatingMusicWidget() {
        floatingMusicContainer = findViewById(R.id.layout_floating_music);
        if (floatingMusicContainer == null) return;

        floatingTouchSlop = ViewConfiguration.get(this).getScaledTouchSlop();

        floatingMusicCard = floatingMusicContainer.findViewById(R.id.card_floating_music);
        floatingMusicCover = (ImageView) floatingMusicContainer.findViewById(R.id.image_floating_cover);
        floatingMusicBubble = (ImageView) floatingMusicContainer.findViewById(R.id.view_floating_bubble);
        floatingMusicTitle = (TextView) floatingMusicContainer.findViewById(R.id.text_floating_title);
        floatingMusicSubtitle = (TextView) floatingMusicContainer.findViewById(R.id.text_floating_subtitle);
        floatingMusicPlayPause = (ImageButton) floatingMusicContainer.findViewById(R.id.button_floating_play);
        floatingMusicClose = (ImageButton) floatingMusicContainer.findViewById(R.id.button_floating_close);

        // 触摸拖动
        floatingMusicContainer.setOnTouchListener(new View.OnTouchListener() {
            @Override public boolean onTouch(View v, MotionEvent event) {
                switch (event.getActionMasked()) {
                    case MotionEvent.ACTION_DOWN:
                        lastFloatingX = event.getRawX();
                        lastFloatingY = event.getRawY();
                        floatingDragging = false;
                        return true;

                    case MotionEvent.ACTION_MOVE:
                        float dx = event.getRawX() - lastFloatingX;
                        float dy = event.getRawY() - lastFloatingY;

                        if (!floatingDragging && (Math.abs(dx) > floatingTouchSlop || Math.abs(dy) > floatingTouchSlop))
                            floatingDragging = true;

                        if (floatingDragging) {
                            moveFloatingWidget(v, dx, dy);
                            if (Math.abs(dx) > Math.abs(dy) && Math.abs(dx) > floatingTouchSlop)
                                collapseFloatingMusic();
                        }

                        lastFloatingX = event.getRawX();
                        lastFloatingY = event.getRawY();
                        return true;

                    case MotionEvent.ACTION_UP:
                        if (!floatingDragging) {
                            expandFloatingMusic();
                            startActivity(new Intent(MainActivity.this, MusicActivity.class));
                        } else {
                            keepFloatingInsideParent(v);
                        }
                        floatingDragging = false;
                        return true;

                    default: return false;
                }
            }
        });

        floatingMusicPlayPause.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                toggleFloatingPlayback();
            }
        });

        floatingMusicClose.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                sendBroadcast(new Intent("ACTION_PAUSE"));
                floatingMusicManuallyClosed = true;
                hideFloatingMusic();
            }
        });

        floatingMusicBubble.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                expandFloatingMusic();
                startActivity(new Intent(MainActivity.this, MusicActivity.class));
            }
        });

        floatingMusicReceiver = new BroadcastReceiver() {
            @Override public void onReceive(Context c, Intent i) {
                if (i == null) return;

                if (MusicService.ACTION_HIDE_FLOATING_MUSIC.equals(i.getAction())) {
                    hideFloatingMusic();
                } else if (MusicService.ACTION_UPDATE_UI.equals(i.getAction())) {
                    updateFloatingMusic(i);
                }
            }
        };
    }

    private void toggleFloatingPlayback() {
        Intent intent = new Intent(floatingMusicPlaying ? "ACTION_PAUSE" : "ACTION_PLAY");
        sendBroadcast(intent);
    }

    private void updateFloatingMusic(Intent intent) {
        if (floatingMusicContainer == null) return;

        floatingMusicPlaying = intent.getBooleanExtra("playing", false);
        if (floatingMusicPlaying) floatingMusicManuallyClosed = false;

        if (floatingMusicManuallyClosed && !floatingMusicPlaying) {
            hideFloatingMusic();
            return;
        }

        String title = intent.getStringExtra("title");
        String artist = intent.getStringExtra("artist");
        int coverResId = intent.getIntExtra("coverResId", R.drawable.cover_playlist_placeholder);

        String safeTitle = TextUtils.isEmpty(title)
                ? getString(R.string.floating_music_title_placeholder)
                : title;

        floatingMusicTitle.setText(
                floatingMusicPlaying
                        ? getString(R.string.floating_music_title_playing, safeTitle)
                        : getString(R.string.floating_music_title_paused, safeTitle)
        );

        if (!TextUtils.isEmpty(artist)) {
            floatingMusicSubtitle.setText(getString(R.string.floating_music_subtitle_artist, artist));
        } else {
            floatingMusicSubtitle.setText(R.string.floating_music_subtitle_empty);
        }

        floatingMusicCover.setImageResource(coverResId);
        floatingMusicBubble.setImageResource(coverResId);
        floatingMusicPlayPause.setImageResource(floatingMusicPlaying ? R.drawable.pause : R.drawable.play);

        floatingMusicContainer.setVisibility(View.VISIBLE);

        if (floatingMusicCollapsed) collapseFloatingMusic();
        else expandFloatingMusic();
    }

    private void hideFloatingMusic() {
        floatingMusicContainer.setVisibility(View.GONE);
        floatingMusicBubble.setVisibility(View.GONE);
        floatingMusicCard.setVisibility(View.GONE);
        floatingMusicCollapsed = false;
        floatingMusicPlaying = false;
    }

    private void collapseFloatingMusic() {
        floatingMusicCollapsed = true;
        floatingMusicCard.setVisibility(View.GONE);
        floatingMusicBubble.setVisibility(View.VISIBLE);
    }

    private void expandFloatingMusic() {
        floatingMusicCollapsed = false;
        floatingMusicCard.setVisibility(View.VISIBLE);
        floatingMusicBubble.setVisibility(View.GONE);
    }

    private void moveFloatingWidget(View target, float dx, float dy) {
        float newX = target.getX() + dx;
        float newY = target.getY() + dy;

        if (target.getParent() instanceof ViewGroup) {
            ViewGroup parent = (ViewGroup) target.getParent();
            int pw = parent.getWidth();
            int ph = parent.getHeight();
            int vw = target.getWidth();
            int vh = target.getHeight();

            newX = Math.max(0, Math.min(newX, pw - vw));
            newY = Math.max(0, Math.min(newY, ph - vh));
        }

        target.setX(newX);
        target.setY(newY);
    }

    private void keepFloatingInsideParent(View target) {
        moveFloatingWidget(target, 0, 0);
    }

    private void registerFloatingMusicReceiver() {
        if (floatingMusicContainer == null || floatingMusicReceiverRegistered || floatingMusicReceiver == null) {
            return;
        }
        IntentFilter filter = new IntentFilter();
        filter.addAction(MusicService.ACTION_UPDATE_UI);
        filter.addAction(MusicService.ACTION_HIDE_FLOATING_MUSIC);
        registerReceiver(floatingMusicReceiver, filter);
        floatingMusicReceiverRegistered = true;
    }

    private void unregisterFloatingMusicReceiver() {
        if (!floatingMusicReceiverRegistered || floatingMusicReceiver == null) return;

        unregisterReceiver(floatingMusicReceiver);
        floatingMusicReceiverRegistered = false;
    }
}
