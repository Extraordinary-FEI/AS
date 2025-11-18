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

        // 顶部标题栏
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle(R.string.title_main);

        if (savedInstanceState != null) {
            selectedNavId = savedInstanceState.getInt(KEY_SELECTED_NAV, R.id.navigation_home);
        }

        // 初始化 Fragment
        initFragments();

        // 初始化底部导航
        BottomNavigationView bottomNavigationView =
                (BottomNavigationView) findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(this);
        bottomNavigationView.setSelectedItemId(selectedNavId);
    }

    /**
     * 初始化所有 Fragment
     */
    private void initFragments() {
        FragmentManager manager = getSupportFragmentManager();

        homeFragment = manager.findFragmentByTag(TAG_HOME);
        cartFragment = manager.findFragmentByTag(TAG_CART);
        supportFragment = manager.findFragmentByTag(TAG_SUPPORT);
        playlistFragment = manager.findFragmentByTag(TAG_PLAYLIST);
        profileFragment = manager.findFragmentByTag(TAG_PROFILE);

        if (homeFragment == null) homeFragment = new HomeFragment();
        if (cartFragment == null) cartFragment = new CartFragment();
        if (supportFragment == null) supportFragment = new SupportTasksFragment();
        if (playlistFragment == null) playlistFragment = new PlaylistLibraryFragment();
        if (profileFragment == null) profileFragment = new UserCenterFragment();

        // 根据当前选择的导航项确定默认显示的 Fragment
        activeFragment = getFragmentByNavId(selectedNavId);
        if (activeFragment == null) {
            // 如果还没有选中过其他 tab，则默认显示首页
            activeFragment = homeFragment;
            selectedNavId = R.id.navigation_home;
        }

        // 首次进入应用时需要把首页添加到容器中，重复添加会抛出异常
        if (!homeFragment.isAdded()) {
            manager.beginTransaction()
                    .add(R.id.main_container, homeFragment, TAG_HOME)
                    .commit();
        }
    }

    private Fragment getFragmentByNavId(int navId) {
        Fragment fragment;
        switch (navId) {
            case R.id.navigation_home:
                fragment = homeFragment;
                break;
            case R.id.navigation_cart:
                fragment = cartFragment;
                break;
            case R.id.navigation_support:
                fragment = supportFragment;
                break;
            case R.id.navigation_playlist:
                fragment = playlistFragment;
                break;
            case R.id.navigation_profile:
                fragment = profileFragment;
                break;
            default:
                fragment = null;
        }
        return fragment != null && fragment.isAdded() ? fragment : null;
    }

    /**
     * 底部导航切换
     */
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        selectedNavId = item.getItemId();

        switch (item.getItemId()) {
            case R.id.navigation_home:
                showFragment(homeFragment, TAG_HOME);
                setTitle(R.string.title_main);
                return true;

            case R.id.navigation_cart:
                showFragment(cartFragment, TAG_CART);
                setTitle(R.string.nav_cart);
                return true;

            case R.id.navigation_support:
                showFragment(supportFragment, TAG_SUPPORT);
                setTitle(R.string.nav_support);
                return true;

            case R.id.navigation_playlist:
                showFragment(playlistFragment, TAG_PLAYLIST);
                setTitle(R.string.nav_playlist);
                return true;

            case R.id.navigation_profile:
                showFragment(profileFragment, TAG_PROFILE);
                setTitle(R.string.title_user_profile);
                return true;
        }
        return false;
    }

    /**
     * 显示某个 Fragment
     */
    private void showFragment(Fragment fragment, String tag) {
        if (fragment == null) return;

        FragmentManager manager = getSupportFragmentManager();
        android.support.v4.app.FragmentTransaction transaction = manager.beginTransaction();

        if (activeFragment != null && activeFragment.isAdded()) {
            transaction.hide(activeFragment);
        }

        if (!fragment.isAdded()) {
            transaction.add(R.id.main_container, fragment, tag);
        } else {
            transaction.show(fragment);
        }

        transaction.commit();
        activeFragment = fragment;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putInt(KEY_SELECTED_NAV, selectedNavId);
        super.onSaveInstanceState(outState);
    }
}
