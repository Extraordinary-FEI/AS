package com.example.cn.helloworld.ui.user;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.example.cn.helloworld.ui.main.PlaylistAdapter;
import com.example.cn.helloworld.ui.playlist.PlaylistDetailActivity;
import com.example.cn.helloworld.ui.playlist.PlaylistOverviewActivity;

/**
 * 个人中心 Fragment，复用活动中的展示内容以保证底部导航切换时不丢失。
 */
public class UserCenterFragment extends Fragment {

    private Context appContext;
    private SessionManager sessionManager;
    private HomeDataSource homeDataSource;
    private RecyclerView playlistList;
    private View viewAllPlaylistsButton;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        // 记录 applicationContext，避免在 onCreateView 中因临时 Context 为空导致崩溃
        appContext = context.getApplicationContext();
        sessionManager = new SessionManager(appContext);
        homeDataSource = new FakeHomeDataSource(appContext);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_user_center, container, false);

        ImageView avatar = (ImageView) root.findViewById(R.id.avatarImage);
        TextView username = (TextView) root.findViewById(R.id.tvUsername);
        TextView userId = (TextView) root.findViewById(R.id.tvUserId);
        Button logout = (Button) root.findViewById(R.id.btnLogout);
        playlistList = (RecyclerView) root.findViewById(R.id.playlistList);
        viewAllPlaylistsButton = root.findViewById(R.id.button_view_all_playlists);

        avatar.setImageResource(R.drawable.ic_user_default);
        username.setText(sessionManager.getUsername());
        userId.setText("UID: " + sessionManager.getUserId());

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sessionManager.logout();
                Context context = getContext();
                if (context != null) {
                    context.sendBroadcast(new Intent("ACTION_STOP"));
                    context.stopService(new Intent(context, MusicService.class));
                    context.sendBroadcast(new Intent(MusicService.ACTION_HIDE_FLOATING_MUSIC));
                    Intent intent = new Intent(context, LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    if (getActivity() != null) {
                        getActivity().finish();
                    }
                }
            }
        });

        setupPlaylists();

        return root;
    }

    private void setupPlaylists() {
        if (playlistList == null) {
            return;
        }

        playlistList.setLayoutManager(
                new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        playlistList.setNestedScrollingEnabled(false);

        playlistList.setAdapter(new PlaylistAdapter(
                homeDataSource.loadPlaylists(),
                new PlaylistAdapter.OnPlaylistClickListener() {
                    @Override
                    public void onPlaylistClick(Playlist playlist) {
                        Context context = getContext();
                        if (context != null) {
                            startActivity(PlaylistDetailActivity.createIntent(
                                    context,
                                    playlist.getId()
                            ));
                        }
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
                    Context context = getContext();
                    if (context != null) {
                        startActivity(new Intent(context, PlaylistOverviewActivity.class));
                    }
                }
            });
        }
    }
}
