package com.example.cn.helloworld.ui.main;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;

import com.example.cn.helloworld.R;

/**
 * 首页入口，负责拼装轮播、分类、歌单和应援任务模块。
 */
public class MainActivity extends AppCompatActivity {

    private ViewPager bannerPager;
    private RecyclerView categoryList;
    private RecyclerView playlistList;
    private RecyclerView taskList;
    private HomeDataSource dataSource;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle(R.string.title_main);

        dataSource = new FakeHomeDataSource();

        bannerPager = findViewById(R.id.bannerPager);
        categoryList = findViewById(R.id.categoryList);
        playlistList = findViewById(R.id.playlistList);
        taskList = findViewById(R.id.taskList);

        setupBanner();
        setupCategories();
        setupPlaylists();
        setupTasks();
    }

    private void setupBanner() {
        BannerAdapter bannerAdapter = new BannerAdapter(this, dataSource.loadBanners());
        bannerPager.setAdapter(bannerAdapter);
        bannerPager.setOffscreenPageLimit(3);
    }

    private void setupCategories() {
        categoryList.setLayoutManager(new GridLayoutManager(this, 3));
        categoryList.setNestedScrollingEnabled(false);
        categoryList.setAdapter(new CategoryAdapter(dataSource.loadCategories()));
    }

    private void setupPlaylists() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        playlistList.setLayoutManager(layoutManager);
        playlistList.setNestedScrollingEnabled(false);
        playlistList.setAdapter(new PlaylistAdapter(dataSource.loadPlaylists()));
    }

    private void setupTasks() {
        taskList.setLayoutManager(new LinearLayoutManager(this));
        taskList.setNestedScrollingEnabled(false);
        taskList.setAdapter(new TaskAdapter(dataSource.loadSupportTasks()));
    }
}
