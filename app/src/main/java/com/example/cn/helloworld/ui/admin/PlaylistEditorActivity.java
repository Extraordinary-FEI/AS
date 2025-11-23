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
import android.support.v7.widget.Toolbar;
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

/**
 * 后台编辑歌单（本地版）
 */
public class PlaylistEditorActivity extends AppCompatActivity {

    private SessionManager sessionManager;
    private PlaylistRepository playlistRepository;
    private Playlist playlist;

    private EditText titleInput;
    private EditText descriptionInput;
    private TextView coverHint;
    private ImageView coverPreview;

    private List<Song> songs = new ArrayList<>();
    private SongAdapter songAdapter;

    // 封面 URI（可为空）
    private String coverUri = null;

    private static final int REQUEST_CODE_PICK_COVER = 2001;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_playlist_editor);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        sessionManager = new SessionManager(this);
        if (!sessionManager.isAdmin()) {
            Toast.makeText(this, "需要管理员权限", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        playlistRepository = PlaylistRepository.getInstance(this);

        // 读取要编辑的 playlistId
        String playlistId = getIntent().getStringExtra(PlaylistManagementActivity.EXTRA_PLAYLIST_ID);
        playlist = playlistRepository.getById(playlistId);
        if (playlist == null) {
            Toast.makeText(this, "歌单不存在", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        bindViews();
        populateData();
        setupSongList();
    }

    private void bindViews() {
        titleInput = (EditText) findViewById(R.id.editPlaylistTitle);
        descriptionInput = (EditText) findViewById(R.id.editPlaylistDescription);
        coverHint = (TextView) findViewById(R.id.textPlaylistCoverHint);
        coverPreview = (ImageView) findViewById(R.id.imagePlaylistCoverPreview);

        Button changeCoverButton = (Button) findViewById(R.id.buttonChangeCover);
        changeCoverButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pickCoverImage();
            }
        });

        Button saveButton = (Button) findViewById(R.id.buttonSavePlaylist);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                savePlaylist();
            }
        });

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fabAddSong);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSongEditor(null);
            }
        });
    }

    private void populateData() {
        setTitle("编辑歌单 - " + playlist.getTitle());

        titleInput.setText(playlist.getTitle());
        descriptionInput.setText(playlist.getDescription());

        coverUri = playlist.getCoverUrl();
        if (!TextUtils.isEmpty(coverUri)) {
            coverPreview.setImageURI(Uri.parse(coverUri));
            coverHint.setText(coverUri);
        } else if (playlist.getCoverResId() != null) {
            coverPreview.setImageResource(playlist.getCoverResId());
        }

        songs = new ArrayList<>(playlist.getSongs());
    }

    // ===================== 封面选择 =====================

    private void pickCoverImage() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(intent, REQUEST_CODE_PICK_COVER);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE_PICK_COVER && resultCode == RESULT_OK && data != null) {
            Uri uri = data.getData();
            if (uri != null) {
                coverUri = uri.toString();
                coverPreview.setImageURI(uri);
                coverHint.setText(coverUri);
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    // ===================== 歌曲管理 =====================

    private void setupSongList() {
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recyclerPlaylistSongs);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        songAdapter = new SongAdapter(songs);
        recyclerView.setAdapter(songAdapter);

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
    }

    private void showSongEditor(@Nullable final Song oldSong) {
        View view = LayoutInflater.from(this)
                .inflate(R.layout.dialog_song_editor, null);

        final EditText titleInput = (EditText) view.findViewById(R.id.editSongTitle);
        final EditText artistInput = (EditText) view.findViewById(R.id.editSongArtist);
        final EditText durationInput = (EditText) view.findViewById(R.id.editSongDuration);

        if (oldSong != null) {
            titleInput.setText(oldSong.getTitle());
            artistInput.setText(oldSong.getArtist());
            durationInput.setText(String.valueOf(oldSong.getDurationMs() / 1000));
        }

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle(oldSong == null ? "添加歌曲" : "编辑歌曲")
                .setView(view)
                .setPositiveButton("确定", null)
                .setNegativeButton("取消", null)
                .create();

        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface d) {
                final AlertDialog ad = (AlertDialog) d;
                Button btn = ad.getButton(AlertDialog.BUTTON_POSITIVE);
                btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String title = titleInput.getText().toString().trim();
                        String artist = artistInput.getText().toString().trim();
                        String durText = durationInput.getText().toString().trim();

                        if (TextUtils.isEmpty(title)) {
                            titleInput.setError("请输入歌曲名");
                            return;
                        }

                        long duration;
                        try {
                            duration = Long.parseLong(durText) * 1000;
                        } catch (Exception e) {
                            durationInput.setError("时长格式错误");
                            return;
                        }

                        Song newSong = new Song(
                                oldSong == null ? System.currentTimeMillis() + "" : oldSong.getId(),
                                title,
                                artist,
                                "",
                                duration,
                                0,      // audioResId 本地 mp3 你可以自己扩展
                                0       // coverResId 本地封面
                        );

                        if (oldSong == null) songs.add(newSong);
                        else {
                            for (int i = 0; i < songs.size(); i++) {
                                if (songs.get(i).getId().equals(oldSong.getId())) {
                                    songs.set(i, newSong);
                                    break;
                                }
                            }
                        }

                        songAdapter.submit(songs);
                        ad.dismiss();
                    }
                });
            }
        });

        dialog.show();
    }

    private void deleteSong(final Song song) {
        new AlertDialog.Builder(this)
                .setTitle("删除歌曲")
                .setMessage("确定删除 " + song.getTitle() + " 吗？")
                .setPositiveButton("删除", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        songs.remove(song);
                        songAdapter.submit(songs);
                    }
                })
                .setNegativeButton("取消", null)
                .show();
    }

    // ===================== 保存歌单 =====================

    private void savePlaylist() {
        String title = titleInput.getText().toString().trim();
        if (TextUtils.isEmpty(title)) {
            titleInput.setError("标题不能为空");
            return;
        }

        Playlist updated = playlist.copyWith(
                title,
                descriptionInput.getText().toString().trim(),
                null,                // playUrl（本地版不用）
                coverUri,
                coverUri == null ? playlist.getCoverResId() : null,
                playlist.getTags(),
                songs,
                playlist.getPlayCount(),
                playlist.getFavoriteCount()
        );

        playlistRepository.updatePlaylist(updated);

        Toast.makeText(this, "已保存歌单", Toast.LENGTH_SHORT).show();
        finish();
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    // ===================== SongAdapter =====================

    private static class SongAdapter extends RecyclerView.Adapter<SongAdapter.ViewHolder> {

        interface Listener {
            void onEdit(Song song);
            void onDelete(Song song);
        }

        private List<Song> songs;
        private Listener listener;

        SongAdapter(List<Song> songs) {
            this.songs = songs;
        }

        void setListener(Listener l) { this.listener = l; }

        void submit(List<Song> list) {
            songs = new ArrayList<>(list);
            notifyDataSetChanged();
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_admin_song, parent, false);
            return new ViewHolder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder h, int pos) {
            final Song s = songs.get(pos);
            h.title.setText(s.getTitle());
            h.subtitle.setText(s.getArtist() + " · " + (s.getDurationMs() / 1000) + "秒");

            h.edit.setOnClickListener(new View.OnClickListener() {
                @Override public void onClick(View v) {
                    if (listener != null) listener.onEdit(s);
                }
            });

            h.delete.setOnClickListener(new View.OnClickListener() {
                @Override public void onClick(View v) {
                    if (listener != null) listener.onDelete(s);
                }
            });
        }

        @Override
        public int getItemCount() { return songs.size(); }

        static class ViewHolder extends RecyclerView.ViewHolder {
            TextView title;
            TextView subtitle;
            Button edit;
            Button delete;

            ViewHolder(View itemView) {
                super(itemView);
                title = (TextView) itemView.findViewById(R.id.textSongTitle);
                subtitle = (TextView) itemView.findViewById(R.id.textSongSubtitle);
                edit = (Button) itemView.findViewById(R.id.buttonEditSong);
                delete = (Button) itemView.findViewById(R.id.buttonDeleteSong);
            }
        }
    }
}
