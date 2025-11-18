package com.example.cn.helloworld.ui.main;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.cn.helloworld.R;
import com.example.cn.helloworld.data.model.Playlist;
import com.example.cn.helloworld.ui.playlist.PlaylistDetailActivity;
import com.example.cn.helloworld.ui.playlist.PlaylistOverviewActivity;

/**
 * 首页内容 Fragment，承载轮播、分类、歌单和应援任务模块。
 */
public class HomeFragment extends Fragment {

    private ViewPager bannerPager;
    private RecyclerView categoryList;
    private RecyclerView playlistList;
    private RecyclerView taskList;
    private View viewAllPlaylistsButton;
    private HomeDataSource dataSource;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_home, container, false);

        // ⭐ 关键修改：传入 Context
        dataSource = new FakeHomeDataSource(root.getContext());

        bannerPager = (ViewPager) root.findViewById(R.id.bannerPager);
        categoryList = (RecyclerView) root.findViewById(R.id.categoryList);
        playlistList = (RecyclerView) root.findViewById(R.id.playlistList);
        viewAllPlaylistsButton = root.findViewById(R.id.button_view_all_playlists);
        taskList = (RecyclerView) root.findViewById(R.id.taskList);

        setupBanner();
        setupCategories();
        setupPlaylists();
        setupTasks();

        return root;
    }

    private void setupBanner() {
        BannerAdapter bannerAdapter = new BannerAdapter(getContext(), dataSource.loadBanners());
        bannerPager.setAdapter(bannerAdapter);
        bannerPager.setOffscreenPageLimit(3);
    }

    @RequiresApi(api = Build.VERSION_CODES.GINGERBREAD)
    private void setupCategories() {
        categoryList.setLayoutManager(new GridLayoutManager(getContext(), 3));
        categoryList.setNestedScrollingEnabled(false);
        categoryList.setAdapter(new CategoryAdapter(dataSource.loadCategories()));
    }

    @RequiresApi(api = Build.VERSION_CODES.GINGERBREAD)
    private void setupPlaylists() {
        Context context = getContext();
        LinearLayoutManager layoutManager =
                new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);
        playlistList.setLayoutManager(layoutManager);
        playlistList.setNestedScrollingEnabled(false);

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
                        // legacy callback
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
        taskList.setLayoutManager(new LinearLayoutManager(getContext()));
        taskList.setNestedScrollingEnabled(false);
        taskList.setAdapter(new TaskAdapter(dataSource.loadSupportTasks()));
    }
}
