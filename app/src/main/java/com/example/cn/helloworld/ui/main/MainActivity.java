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
    private BroadcastReceiver floatingMusicReceiver;
    private boolean floatingMusicReceiverRegistered = false;
    private boolean floatingMusicCollapsed = false;
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

        // 顶部栏
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

    /**
     * 初始化 Fragment（全部只创建一次，不会重复添加）
     */
    private void initFragments() {
        FragmentManager fm = getSupportFragmentManager();

        homeFragment = fm.findFragmentByTag(TAG_HOME);
        cartFragment = fm.findFragmentByTag(TAG_CART);
        supportFragment = fm.findFragmentByTag(TAG_SUPPORT);
        playlistFragment = fm.findFragmentByTag(TAG_PLAYLIST);
        profileFragment = fm.findFragmentByTag(TAG_PROFILE);

        if (homeFragment == null) homeFragment = new HomeFragment();
        if (cartFragment == null) cartFragment = new CartFragment();
        if (supportFragment == null) supportFragment = new SupportTasksFragment();
        if (playlistFragment == null) playlistFragment = new PlaylistLibraryFragment();
        if (profileFragment == null) profileFragment = new UserCenterFragment();

        // 只 add 首页，其余保持未添加状态，避免重复添加
        activeFragment = homeFragment;

        fm.beginTransaction()
                .add(R.id.main_container, activeFragment, TAG_HOME)
                .commit();
    }

    @Override
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

    /**
     * 核心修复点：Fragment 切换，不重复 add
     */
    private void switchFragment(Fragment target, String tag) {
        if (activeFragment == target) return; // 已在当前，不重复操作

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
        if (activeFragment == supportFragment) {
            return R.id.navigation_support;
        }
        if (activeFragment == playlistFragment) {
            return R.id.navigation_playlist;
        }
        if (activeFragment == profileFragment) {
            return R.id.navigation_profile;
        }
        if (activeFragment == cartFragment) {
            return R.id.navigation_cart;
        }
        return R.id.navigation_home;
    }

    private void updateTitleForNav(int navId) {
        switch (navId) {
            case R.id.navigation_support:
                setTitle(R.string.nav_support);
                break;
            case R.id.navigation_playlist:
                setTitle(R.string.nav_playlist);
                break;
            case R.id.navigation_profile:
                setTitle(R.string.title_user_profile);
                break;
            case R.id.navigation_cart:
                setTitle(R.string.nav_cart);
                break;
            default:
                setTitle(R.string.title_main);
                break;
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putInt(KEY_SELECTED_NAV, selectedNavId);
        super.onSaveInstanceState(outState);
    }
    private void initFloatingMusicWidget() {
        floatingMusicContainer = findViewById(R.id.layout_floating_music);
        if (floatingMusicContainer == null) {
            return;
        }

        floatingTouchSlop = ViewConfiguration.get(this).getScaledTouchSlop();
        floatingMusicCard = floatingMusicContainer.findViewById(R.id.card_floating_music);
        floatingMusicCover = (ImageView) floatingMusicContainer.findViewById(R.id.image_floating_cover);
        floatingMusicBubble = (ImageView) floatingMusicContainer.findViewById(R.id.view_floating_bubble);
        floatingMusicTitle = (TextView) floatingMusicContainer.findViewById(R.id.text_floating_title);
        floatingMusicSubtitle = (TextView) floatingMusicContainer.findViewById(R.id.text_floating_subtitle);
        floatingMusicPlayPause = (ImageButton) floatingMusicContainer.findViewById(R.id.button_floating_play);
        floatingMusicClose = (ImageButton) floatingMusicContainer.findViewById(R.id.button_floating_close);

        floatingMusicContainer.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getActionMasked()) {
                    case MotionEvent.ACTION_DOWN:
                        lastFloatingX = event.getRawX();
                        lastFloatingY = event.getRawY();
                        floatingDragging = false;
                        return true;
                    case MotionEvent.ACTION_MOVE:
                        float dx = event.getRawX() - lastFloatingX;
                        float dy = event.getRawY() - lastFloatingY;
                        if (!floatingDragging && (Math.abs(dx) > floatingTouchSlop || Math.abs(dy) > floatingTouchSlop)) {
                            floatingDragging = true;
                        }
                        if (floatingDragging) {
                            moveFloatingWidget(v, dx, dy);
                            if (Math.abs(dx) > Math.abs(dy) && Math.abs(dx) > floatingTouchSlop) {
                                collapseFloatingMusic();
                            }
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
                    default:
                        return false;
                }
            }
        });

        floatingMusicPlayPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleFloatingPlayback();
            }
        });

        if (floatingMusicClose != null) {
            floatingMusicClose.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    sendBroadcast(new Intent("ACTION_PAUSE"));
                    floatingMusicManuallyClosed = true;
                    hideFloatingMusic();
                }
            });
        }

        if (floatingMusicBubble != null) {
            floatingMusicBubble.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    expandFloatingMusic();
                    startActivity(new Intent(MainActivity.this, MusicActivity.class));
                }
            });
        }

        floatingMusicReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent == null) {
                    return;
                }
                String action = intent.getAction();
                if (MusicService.ACTION_HIDE_FLOATING_MUSIC.equals(action)) {
                    hideFloatingMusic();
                } else if (MusicService.ACTION_UPDATE_UI.equals(action)) {
                    updateFloatingMusic(intent);
                }
            }
        };
    }

    private void toggleFloatingPlayback() {
        Intent intent = new Intent(floatingMusicPlaying ? "ACTION_PAUSE" : "ACTION_PLAY");
        sendBroadcast(intent);
    }

    private void updateFloatingMusic(Intent intent) {
        if (floatingMusicContainer == null) {
            return;
        }

        floatingMusicPlaying = intent.getBooleanExtra("playing", false);
        if (floatingMusicPlaying) {
            floatingMusicManuallyClosed = false;
        }

        if (floatingMusicManuallyClosed && !floatingMusicPlaying) {
            hideFloatingMusic();
            return;
        }
        String title = intent.getStringExtra("title");
        String artist = intent.getStringExtra("artist");
        String playlistTitle = intent.getStringExtra("playlistTitle");
        int coverResId = intent.getIntExtra("coverResId", R.drawable.cover_playlist_placeholder);

        String safeTitle = TextUtils.isEmpty(title)
                ? getString(R.string.floating_music_title_placeholder)
                : title;
        floatingMusicTitle.setText(floatingMusicPlaying
                ? getString(R.string.floating_music_title_playing, safeTitle)
                : getString(R.string.floating_music_title_paused, safeTitle));

        if (!TextUtils.isEmpty(artist)) {
            floatingMusicSubtitle.setText(
                    getString(R.string.floating_music_subtitle_artist, artist));
        } else if (!TextUtils.isEmpty(playlistTitle)) {
            floatingMusicSubtitle.setText(
                    getString(R.string.floating_music_subtitle_playlist, playlistTitle));
        } else {
            floatingMusicSubtitle.setText(R.string.floating_music_subtitle_empty);
        }

        floatingMusicCover.setImageResource(coverResId);
        if (floatingMusicBubble != null) {
            floatingMusicBubble.setImageResource(coverResId);
        }
        floatingMusicPlayPause.setImageResource(floatingMusicPlaying ? R.drawable.pause : R.drawable.play);
        floatingMusicContainer.setVisibility(View.VISIBLE);

        if (floatingMusicCollapsed) {
            collapseFloatingMusic();
        } else {
            expandFloatingMusic();
        }
    }

    private void hideFloatingMusic() {
        if (floatingMusicContainer != null) {
            floatingMusicContainer.setVisibility(View.GONE);
            if (floatingMusicBubble != null) {
                floatingMusicBubble.setVisibility(View.GONE);
            }
            if (floatingMusicCard != null) {
                floatingMusicCard.setVisibility(View.GONE);
            }
        }
        floatingMusicCollapsed = false;
        floatingMusicPlaying = false;
    }

    private void collapseFloatingMusic() {
        floatingMusicCollapsed = true;
        if (floatingMusicCard != null) {
            floatingMusicCard.setVisibility(View.GONE);
        }
        if (floatingMusicBubble != null) {
            floatingMusicBubble.setVisibility(View.VISIBLE);
        }
    }

    private void expandFloatingMusic() {
        floatingMusicCollapsed = false;
        if (floatingMusicCard != null) {
            floatingMusicCard.setVisibility(View.VISIBLE);
        }
        if (floatingMusicBubble != null) {
            floatingMusicBubble.setVisibility(View.GONE);
        }
    }

    private void moveFloatingWidget(View target, float dx, float dy) {
        float newX = target.getX() + dx;
        float newY = target.getY() + dy;

        if (target.getParent() instanceof ViewGroup) {
            ViewGroup parent = (ViewGroup) target.getParent();
            int parentWidth = parent.getWidth();
            int parentHeight = parent.getHeight();
            int viewWidth = target.getWidth();
            int viewHeight = target.getHeight();

            newX = Math.max(0, Math.min(newX, parentWidth - viewWidth));
            newY = Math.max(0, Math.min(newY, parentHeight - viewHeight));
        }

        target.setX(newX);
        target.setY(newY);
    }

    private void keepFloatingInsideParent(View target) {
        moveFloatingWidget(target, 0, 0);
    }

    private void registerFloatingMusicReceiver() {
        if (floatingMusicContainer == null || floatingMusicReceiver == null || floatingMusicReceiverRegistered) {
            return;
        }
        IntentFilter filter = new IntentFilter();
        filter.addAction(MusicService.ACTION_UPDATE_UI);
        filter.addAction(MusicService.ACTION_HIDE_FLOATING_MUSIC);
        registerReceiver(floatingMusicReceiver, filter);
        floatingMusicReceiverRegistered = true;
    }

    private void unregisterFloatingMusicReceiver() {
        if (!floatingMusicReceiverRegistered || floatingMusicReceiver == null) {
            return;
        }
        unregisterReceiver(floatingMusicReceiver);
        floatingMusicReceiverRegistered = false;
    }
}
