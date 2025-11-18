package com.example.cn.helloworld.ui.user;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.cn.helloworld.R;
import com.example.cn.helloworld.data.model.Playlist;
import com.example.cn.helloworld.data.session.SessionManager;
import com.example.cn.helloworld.ui.auth.LoginActivity;
import com.example.cn.helloworld.ui.main.FakeHomeDataSource;
import com.example.cn.helloworld.ui.main.HomeDataSource;
import com.example.cn.helloworld.ui.main.HomeModels;
import com.example.cn.helloworld.ui.main.PlaylistAdapter;
import com.example.cn.helloworld.ui.playlist.PlaylistDetailActivity;
import com.example.cn.helloworld.ui.playlist.PlaylistOverviewActivity;

public class UserProfileActivity extends AppCompatActivity {

    private SessionManager sessionManager;
    private RecyclerView playlistList;
    private View viewAllPlaylistsButton;
    private HomeDataSource homeDataSource;

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
        homeDataSource = new FakeHomeDataSource();

        // 使用 layout 中真实存在的 ID
        ImageView avatar  = (ImageView) findViewById(R.id.avatarImage);
        TextView username = (TextView) findViewById(R.id.tvUsername);
        TextView userId   = (TextView) findViewById(R.id.tvUserId);
        Button  logout    = (Button) findViewById(R.id.btnLogout);
        playlistList = (RecyclerView) findViewById(R.id.playlistList);
        viewAllPlaylistsButton = findViewById(R.id.button_view_all_playlists);

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
                Intent intent = new Intent(UserProfileActivity.this, LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            }
        });

        setupPlaylists();
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
}
