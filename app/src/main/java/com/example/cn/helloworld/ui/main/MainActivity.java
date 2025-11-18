package com.example.cn.helloworld.ui.main;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.example.cn.helloworld.R;
import com.example.cn.helloworld.ui.order.CartFragment;
import com.example.cn.helloworld.ui.playlist.PlaylistLibraryFragment;
import com.example.cn.helloworld.ui.user.UserCenterFragment;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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
        bottomNavigationView.setOnNavigationItemSelectedListener(this);
        bottomNavigationView.setSelectedItemId(selectedNavId);
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

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putInt(KEY_SELECTED_NAV, selectedNavId);
        super.onSaveInstanceState(outState);
    }
}
