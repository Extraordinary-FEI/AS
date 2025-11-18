package com.example.cn.helloworld.ui.main;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.example.cn.helloworld.R;
import com.example.cn.helloworld.data.model.Playlist;
import com.example.cn.helloworld.ui.playlist.PlaylistDetailActivity;
import com.example.cn.helloworld.ui.playlist.PlaylistOverviewActivity;
import com.example.cn.helloworld.ui.user.UserProfileActivity;


/**
 * 首页入口，负责拼装轮播、分类、歌单和应援任务模块。
 */
public class MainActivity extends AppCompatActivity {

    private ViewPager bannerPager;
    private RecyclerView categoryList;
    private RecyclerView playlistList;
    private RecyclerView taskList;
    private View viewAllPlaylistsButton;
    private HomeDataSource dataSource;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);
        setTitle(R.string.title_main);

        dataSource = new FakeHomeDataSource(this);


        bannerPager = (ViewPager) findViewById(R.id.bannerPager);
        categoryList = (RecyclerView) findViewById(R.id.categoryList);
        playlistList = (RecyclerView) findViewById(R.id.playlistList);
        viewAllPlaylistsButton = findViewById(R.id.button_view_all_playlists);
        taskList = (RecyclerView) findViewById(R.id.taskList);


        setupBanner();
        setupCategories();
        setupPlaylists();
        setupTasks();
        setupUserFab();
    }

    private void setupBanner() {
        BannerAdapter bannerAdapter = new BannerAdapter(this, dataSource.loadBanners());
        bannerPager.setAdapter(bannerAdapter);
        bannerPager.setOffscreenPageLimit(3);
    }

    @RequiresApi(api = Build.VERSION_CODES.GINGERBREAD)
    private void setupCategories() {
        categoryList.setLayoutManager(new GridLayoutManager(this, 3));
        categoryList.setNestedScrollingEnabled(false);
        categoryList.setAdapter(new CategoryAdapter(dataSource.loadCategories()));
    }

    @RequiresApi(api = Build.VERSION_CODES.GINGERBREAD)
    private void setupPlaylists() {
        LinearLayoutManager layoutManager =
                new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        playlistList.setLayoutManager(layoutManager);
        playlistList.setNestedScrollingEnabled(false);

        playlistList.setAdapter(new PlaylistAdapter(
                dataSource.loadPlaylists(),
                new PlaylistAdapter.OnPlaylistClickListener() {
                    @Override
                    public void onPlaylistClick(Playlist playlist) {
                        startActivity(PlaylistDetailActivity.createIntent(
                                MainActivity.this,
                                playlist.getId()
                        ));
                    }

                    @Override
                    public void onPlaylistClick(HomeModels.Playlist playlist) {

                    }
                }));
        viewAllPlaylistsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, PlaylistOverviewActivity.class));
            }
        });
    }


    private void setupUserFab() {
        android.support.design.widget.FloatingActionButton fabProfile =
                (android.support.design.widget.FloatingActionButton) findViewById(R.id.fab_profile);

        fabProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, UserProfileActivity.class);
                try {
                    startActivity(intent);
                } catch (ActivityNotFoundException notFound) {
                    android.widget.Toast.makeText(
                            MainActivity.this,
                            R.string.error_profile_not_found,
                            android.widget.Toast.LENGTH_SHORT
                    ).show();
                }
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.GINGERBREAD)
    private void setupTasks() {
        taskList.setLayoutManager(new LinearLayoutManager(this));
        taskList.setNestedScrollingEnabled(false);
        taskList.setAdapter(new TaskAdapter(dataSource.loadSupportTasks()));
    }
}
