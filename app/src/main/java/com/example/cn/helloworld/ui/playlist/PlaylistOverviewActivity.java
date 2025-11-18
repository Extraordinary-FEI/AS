package com.example.cn.helloworld.ui.playlist;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.example.cn.helloworld.R;
import com.example.cn.helloworld.data.model.Playlist;
import com.example.cn.helloworld.data.playlist.PlaylistRepository;
import com.example.cn.helloworld.ui.main.HomeModels;
import com.example.cn.helloworld.ui.main.PlaylistAdapter;

import java.util.List;

/**
 * 歌单总览页，展示全部易烊千玺主题歌单。
 */
public class PlaylistOverviewActivity extends AppCompatActivity {

    private RecyclerView playlistRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playlist_overview);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(R.string.title_playlist_overview);
        }
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        playlistRecyclerView = (RecyclerView) findViewById(R.id.recycler_all_playlists);
        playlistRecyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        playlistRecyclerView.setHasFixedSize(true);

        List<Playlist> playlists = PlaylistRepository.getInstance(this).getHomeSummaries();
        playlistRecyclerView.setAdapter(new PlaylistAdapter(playlists, new PlaylistAdapter.OnPlaylistClickListener() {
            @Override
            public void onPlaylistClick(Playlist playlist) {

            }

            @Override
            public void onPlaylistClick(HomeModels.Playlist playlist) {
                startActivity(PlaylistDetailActivity.createIntent(PlaylistOverviewActivity.this, playlist.getId()));
            }
        }));
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
