package com.example.cn.helloworld.ui.admin;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.cn.helloworld.R;
import com.example.cn.helloworld.data.model.Playlist;
import com.example.cn.helloworld.data.model.Song;
import com.example.cn.helloworld.data.repository.PlaylistRepository;
import com.example.cn.helloworld.data.session.SessionManager;

import java.util.ArrayList;
import java.util.List;

public class PlaylistManagementActivity extends AppCompatActivity {

    public static final String EXTRA_PLAYLIST_ID = "extra_playlist_id";

    private SessionManager sessionManager;
    private PlaylistRepository playlistRepository;
    private PlaylistAdapter adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_playlist_management);
        setTitle(R.string.title_admin_manage_playlists);

        sessionManager = new SessionManager(this);
        if (!sessionManager.isAdmin()) {
            Toast.makeText(this, R.string.admin_access_denied, Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        playlistRepository = new PlaylistRepository(this);

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recyclerPlaylists);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new PlaylistAdapter(new ArrayList<Playlist>());
        recyclerView.setAdapter(adapter);

        adapter.setListener(new PlaylistAdapter.Listener() {
            @Override
            public void onManageSongs(Playlist playlist) {
                openPlaylistEditor(playlist.getId());
            }
        });

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fabAddPlaylist);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createNewPlaylist();
            }
        });

        loadPlaylists();
    }

    private void loadPlaylists() {
        adapter.submit(playlistRepository.getAll());
    }

    private void createNewPlaylist() {
        String newId = playlistRepository.generatePlaylistId();
        Playlist playlist = new Playlist(newId,
                getString(R.string.default_new_playlist_title),
                getString(R.string.default_new_playlist_description),
                "",
                null,
                null,
                new ArrayList<String>(),
                new ArrayList<Song>());
        playlistRepository.savePlaylist(playlist);
        loadPlaylists();
        openPlaylistEditor(newId);
    }

    private void openPlaylistEditor(String playlistId) {
        Intent intent = new Intent(this, PlaylistEditorActivity.class);
        intent.putExtra(EXTRA_PLAYLIST_ID, playlistId);
        startActivity(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (sessionManager.isAdmin()) {
            loadPlaylists();
        }
    }

    private static class PlaylistAdapter extends RecyclerView.Adapter<PlaylistAdapter.ViewHolder> {

        interface Listener {
            void onManageSongs(Playlist playlist);
        }

        private final List<Playlist> playlists;
        private Listener listener;

        PlaylistAdapter(List<Playlist> playlists) {
            this.playlists = playlists;
        }

        void setListener(Listener listener) {
            this.listener = listener;
        }

        void submit(List<Playlist> list) {
            playlists.clear();
            if (list != null) {
                playlists.addAll(list);
            }
            notifyDataSetChanged();
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_admin_playlist, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            final Playlist playlist = playlists.get(position);
            holder.titleView.setText(playlist.getTitle());
            holder.descriptionView.setText(playlist.getDescription());
            holder.countView.setText(holder.itemView.getContext().getString(
                    R.string.playlist_track_count_format, playlist.getSongs().size()));
            holder.manageButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        listener.onManageSongs(playlist);
                    }
                }
            });
        }

        @Override
        public int getItemCount() {
            return playlists.size();
        }

        static class ViewHolder extends RecyclerView.ViewHolder {

            final TextView titleView;
            final TextView descriptionView;
            final TextView countView;
            final Button manageButton;

            ViewHolder(View itemView) {
                super(itemView);
                titleView = (TextView) itemView.findViewById(R.id.textPlaylistTitle);
                descriptionView = (TextView) itemView.findViewById(R.id.textPlaylistDescription);
                countView = (TextView) itemView.findViewById(R.id.textPlaylistCount);
                manageButton = (Button) itemView.findViewById(R.id.buttonManagePlaylist);
            }
        }
    }
}
