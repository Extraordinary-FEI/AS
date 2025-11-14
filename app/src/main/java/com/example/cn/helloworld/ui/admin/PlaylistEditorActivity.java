package com.example.cn.helloworld.ui.admin;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.cn.helloworld.R;
import com.example.cn.helloworld.data.model.Playlist;
import com.example.cn.helloworld.data.model.Song;
import com.example.cn.helloworld.data.session.SessionManager;
import com.example.cn.helloworld.data.playlist.PlaylistRepository;

import java.util.ArrayList;
import java.util.List;

public class PlaylistEditorActivity extends AppCompatActivity {

    private static final int REQUEST_CODE_PICK_COVER = 1011;

    private SessionManager sessionManager;
    private PlaylistRepository playlistRepository;
    private Playlist playlist;
    private List<Song> songs = new ArrayList<>();

    private EditText titleInput;
    private EditText descriptionInput;
    private EditText playUrlInput;
    private TextView coverHint;
    private ImageView coverPreview;
    private SongAdapter songAdapter;

    private String coverUri;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_playlist_editor);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        sessionManager = new SessionManager(this);
        if (!sessionManager.isAdmin()) {
            Toast.makeText(this, R.string.admin_access_denied, Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        playlistRepository = new PlaylistRepository(this);

        String playlistId = getIntent().getStringExtra(PlaylistManagementActivity.EXTRA_PLAYLIST_ID);
        if (TextUtils.isEmpty(playlistId)) {
            finish();
            return;
        }
        playlist = playlistRepository.getById(playlistId);
        if (playlist == null) {
            finish();
            return;
        }

        setTitle(playlist.getTitle());

        titleInput = (EditText) findViewById(R.id.editPlaylistTitle);
        descriptionInput = (EditText) findViewById(R.id.editPlaylistDescription);
        playUrlInput = (EditText) findViewById(R.id.editPlaylistPlayUrl);
        coverHint = (TextView) findViewById(R.id.textPlaylistCoverHint);
        coverPreview = (ImageView) findViewById(R.id.imagePlaylistCoverPreview);

        Button changeCoverButton = (Button) findViewById(R.id.buttonChangeCover);
        changeCoverButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openCoverPicker();
            }
        });

        RecyclerView songsRecycler = (RecyclerView) findViewById(R.id.recyclerPlaylistSongs);
        songsRecycler.setLayoutManager(new LinearLayoutManager(this));
        songAdapter = new SongAdapter(new ArrayList<Song>());
        songAdapter.setListener(new SongAdapter.Listener() {
            @Override
            public void onEdit(Song song) {
                showSongEditor(song);
            }

            @Override
            public void onDelete(Song song) {
                deleteSong(song);
            }
        });
        songsRecycler.setAdapter(songAdapter);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fabAddSong);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSongEditor(null);
            }
        });

        Button saveButton = (Button) findViewById(R.id.buttonSavePlaylist);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                savePlaylist();
            }
        });

        populateData();
    }

    private void populateData() {
        titleInput.setText(playlist.getTitle());
        descriptionInput.setText(playlist.getDescription());
        playUrlInput.setText(playlist.getPlayUrl());
        coverUri = playlist.getCoverUrl();
        if (!TextUtils.isEmpty(coverUri)) {
            coverHint.setText(coverUri);
            coverPreview.setImageURI(Uri.parse(coverUri));
        } else if (playlist.getCoverResId() != null) {
            coverPreview.setImageResource(playlist.getCoverResId());
        }
        songs = playlist.getSongs();
        songAdapter.submit(songs);
    }

    private void openCoverPicker() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("image/*");
        startActivityForResult(Intent.createChooser(intent, getString(R.string.playlist_pick_cover)), REQUEST_CODE_PICK_COVER);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_PICK_COVER && resultCode == RESULT_OK && data != null) {
            Uri uri = data.getData();
            if (uri != null) {
                coverUri = uri.toString();
                coverPreview.setImageURI(uri);
                coverHint.setText(coverUri);
            }
        }
    }

    private void showSongEditor(@Nullable final Song song) {
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_song_editor, null, false);
        final EditText titleInput = (EditText) dialogView.findViewById(R.id.editSongTitle);
        final EditText artistInput = (EditText) dialogView.findViewById(R.id.editSongArtist);
        final EditText durationInput = (EditText) dialogView.findViewById(R.id.editSongDuration);
        final EditText streamUrlInput = (EditText) dialogView.findViewById(R.id.editSongStreamUrl);

        if (song != null) {
            titleInput.setText(song.getTitle());
            artistInput.setText(song.getArtist());
            durationInput.setText(String.valueOf(song.getDurationMs() / 1000));
            streamUrlInput.setText(song.getStreamUrl());
        }

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle(song == null ? R.string.dialog_title_add_song : R.string.dialog_title_edit_song)
                .setView(dialogView)
                .setPositiveButton(android.R.string.ok, null)
                .setNegativeButton(android.R.string.cancel, null)
                .create();

        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(final DialogInterface dialogInterface) {
                AlertDialog alertDialog = (AlertDialog) dialogInterface;
                Button button = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE);
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String title = titleInput.getText() == null ? "" : titleInput.getText().toString().trim();
                        String artist = artistInput.getText() == null ? "" : artistInput.getText().toString().trim();
                        String durationText = durationInput.getText() == null ? "" : durationInput.getText().toString().trim();
                        String streamUrl = streamUrlInput.getText() == null ? "" : streamUrlInput.getText().toString().trim();

                        if (TextUtils.isEmpty(title)) {
                            titleInput.setError(getString(R.string.error_song_title_required));
                            return;
                        }
                        long durationMs;
                        try {
                            durationMs = Long.parseLong(durationText) * 1000L;
                        } catch (NumberFormatException exception) {
                            durationInput.setError(getString(R.string.error_song_duration_invalid));
                            return;
                        }
                        if (TextUtils.isEmpty(streamUrl)) {
                            streamUrlInput.setError(getString(R.string.error_song_stream_required));
                            return;
                        }

                        Song updated;
                        if (song == null) {
                            String newId = playlistRepository.generateSongId(playlist.getId());
                            updated = new Song(newId, title, artist, durationMs, streamUrl,
                                    "",
                                    null,
                                    null);
                            songs.add(updated);
                        } else {
                            updated = new Song(song.getId(), title, artist, durationMs, streamUrl,
                                    song.getDescription(), song.getCoverUrl(), song.getCoverResId());
                            for (int i = 0; i < songs.size(); i++) {
                                if (songs.get(i).getId().equals(updated.getId())) {
                                    songs.set(i, updated);
                                    break;
                                }
                            }
                        }

                        songAdapter.submit(songs);
                        dialogInterface.dismiss();
                    }
                });
            }
        });

        dialog.show();
    }

    private void deleteSong(final Song song) {
        new AlertDialog.Builder(this)
                .setTitle(R.string.dialog_title_delete_song)
                .setMessage(getString(R.string.dialog_message_delete_song, song.getTitle()))
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        for (int i = songs.size() - 1; i >= 0; i--) {
                            if (songs.get(i).getId().equals(song.getId())) {
                                songs.remove(i);
                            }
                        }
                        songAdapter.submit(songs);
                    }
                })
                .setNegativeButton(android.R.string.cancel, null)
                .show();
    }

    private void savePlaylist() {
        String title = titleInput.getText() == null ? "" : titleInput.getText().toString().trim();
        String description = descriptionInput.getText() == null ? "" : descriptionInput.getText().toString().trim();
        String playUrl = playUrlInput.getText() == null ? "" : playUrlInput.getText().toString().trim();

        if (TextUtils.isEmpty(title)) {
            titleInput.setError(getString(R.string.error_playlist_title_required));
            return;
        }

        playlistRepository.updatePlaylistDetails(playlist.getId(),
                title,
                description,
                playUrl,
                coverUri,
                coverUri == null ? playlist.getCoverResId() : null,
                playlist.getTags());
        playlistRepository.replaceSongs(playlist.getId(), songs);
        Toast.makeText(this, R.string.toast_playlist_saved, Toast.LENGTH_SHORT).show();
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private static class SongAdapter extends RecyclerView.Adapter<SongAdapter.ViewHolder> {

        interface Listener {
            void onEdit(Song song);

            void onDelete(Song song);
        }

        private final List<Song> songs;
        private Listener listener;

        SongAdapter(List<Song> songs) {
            this.songs = songs;
        }

        void setListener(Listener listener) {
            this.listener = listener;
        }

        void submit(List<Song> list) {
            songs.clear();
            if (list != null) {
                songs.addAll(list);
            }
            notifyDataSetChanged();
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_admin_song, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            final Song song = songs.get(position);
            holder.titleView.setText(song.getTitle());
            holder.subtitleView.setText(holder.itemView.getContext().getString(
                    R.string.playlist_song_duration_format,
                    song.getArtist(),
                    song.getDurationMs() / 1000));
            holder.editButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        listener.onEdit(song);
                    }
                }
            });
            holder.deleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        listener.onDelete(song);
                    }
                }
            });
        }

        @Override
        public int getItemCount() {
            return songs.size();
        }

        static class ViewHolder extends RecyclerView.ViewHolder {

            final TextView titleView;
            final TextView subtitleView;
            final Button editButton;
            final Button deleteButton;

            ViewHolder(View itemView) {
                super(itemView);
                titleView = (TextView) itemView.findViewById(R.id.textSongTitle);
                subtitleView = (TextView) itemView.findViewById(R.id.textSongSubtitle);
                editButton = (Button) itemView.findViewById(R.id.buttonEditSong);
                deleteButton = (Button) itemView.findViewById(R.id.buttonDeleteSong);
            }
        }
    }
}
