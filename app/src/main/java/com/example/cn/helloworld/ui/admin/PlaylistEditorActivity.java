package com.example.cn.helloworld.ui.admin;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
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

public class PlaylistEditorActivity extends AppCompatActivity {

    private static final int REQUEST_CODE_PICK_COVER = 2001;
    private static final int REQUEST_CODE_PICK_AUDIO = 2002;
    private static final int REQUEST_CODE_PICK_SONG_COVER = 2003;

    private static final int REQUEST_PERMISSION_READ_STORAGE = 1001;

    private SessionManager sessionManager;
    private PlaylistRepository playlistRepository;
    private Playlist playlist;

    private EditText titleInput;
    private EditText descriptionInput;
    private TextView coverHint;
    private ImageView coverPreview;

    private List<Song> songs = new ArrayList<>();
    private SongAdapter songAdapter;

    private TextView activeAudioPathView;
    private String pendingAudioPath;

    // 歌单封面
    private String coverUri = null;

    // ---- 新增：歌曲封面上传 ----
    private ImageView dialogSongCoverPreview;
    private TextView dialogSongCoverPath;
    private String pendingSongCoverPath = null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_playlist_editor);

        // ==================== 动态权限检测（解决崩溃） ====================
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            if (checkSelfPermission(android.Manifest.permission.READ_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {

                requestPermissions(new String[]{
                        android.Manifest.permission.READ_EXTERNAL_STORAGE
                }, REQUEST_PERMISSION_READ_STORAGE);
            }
        }
        // ===============================================================

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        sessionManager = new SessionManager(this);
        if (!sessionManager.isAdmin()) {
            Toast.makeText(this, "需要管理员权限", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        playlistRepository = PlaylistRepository.getInstance(this);

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
            @Override public void onClick(View view) { pickCoverImage(); }
        });

        Button saveButton = (Button) findViewById(R.id.buttonSavePlaylist);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) { savePlaylist(); }
        });

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fabAddSong);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) { showSongEditor(null); }
        });
    }

    private void populateData() {
        setTitle("编辑歌单 - " + playlist.getTitle());

        titleInput.setText(playlist.getTitle());
        descriptionInput.setText(playlist.getDescription());

        coverUri = playlist.getCoverUrl();
        if (!TextUtils.isEmpty(coverUri)) {
            try {
                coverPreview.setImageURI(Uri.parse(coverUri));
                coverHint.setText(coverUri);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (playlist.getCoverResId() != null) {
            coverPreview.setImageResource(playlist.getCoverResId());
        }

        songs = new ArrayList<>(playlist.getSongs());
    }

    // =================== 选取歌单封面 ====================
    private void pickCoverImage() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(intent, REQUEST_CODE_PICK_COVER);
    }

    // =================== 选取歌曲音频 ====================
    private void pickAudioFile() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("audio/*");
        startActivityForResult(intent, REQUEST_CODE_PICK_AUDIO);
    }

    // =================== 选取歌曲封面（新增） ====================
    private void pickSongCover() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(intent, REQUEST_CODE_PICK_SONG_COVER);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != RESULT_OK || data == null) {
            super.onActivityResult(requestCode, resultCode, data);
            return;
        }

        Uri uri = data.getData();
        if (uri == null) {
            super.onActivityResult(requestCode, resultCode, data);
            return;
        }

        try {
            if (requestCode == REQUEST_CODE_PICK_COVER) {
                coverUri = uri.toString();
                coverPreview.setImageURI(uri);
                coverHint.setText(coverUri);

            } else if (requestCode == REQUEST_CODE_PICK_AUDIO) {
                pendingAudioPath = uri.toString();
                updateAudioLabel(activeAudioPathView, pendingAudioPath);

            } else if (requestCode == REQUEST_CODE_PICK_SONG_COVER) {
                pendingSongCoverPath = uri.toString();
                if (dialogSongCoverPreview != null)
                    dialogSongCoverPreview.setImageURI(uri);

                if (dialogSongCoverPath != null)
                    dialogSongCoverPath.setText(pendingSongCoverPath);
            }

        } catch (Exception e) {
            Toast.makeText(this, "无法读取文件，请检查权限", Toast.LENGTH_SHORT).show();
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    private void updateAudioLabel(@Nullable TextView textView, @Nullable String path) {
        if (textView == null) return;

        if (TextUtils.isEmpty(path)) {
            textView.setText(R.string.song_hint_no_file);
            return;
        }

        String name = Uri.parse(path).getLastPathSegment();
        if (TextUtils.isEmpty(name)) name = path;

        textView.setText(getString(R.string.song_selected_file, name));
    }

    private void setupSongList() {
        RecyclerView rv = (RecyclerView) findViewById(R.id.recyclerPlaylistSongs);
        rv.setLayoutManager(new LinearLayoutManager(this));

        songAdapter = new SongAdapter(songs);
        rv.setAdapter(songAdapter);

        songAdapter.setListener(new SongAdapter.Listener() {
            @Override public void onEdit(Song song) { showSongEditor(song); }
            @Override public void onDelete(Song song) { deleteSong(song); }
        });
    }

    private void showSongEditor(@Nullable final Song oldSong) {
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_song_editor, null);

        final EditText titleInput = (EditText) view.findViewById(R.id.editSongTitle);
        final EditText artistInput = (EditText) view.findViewById(R.id.editSongArtist);
        final EditText durationInput = (EditText) view.findViewById(R.id.editSongDuration);
        final EditText streamInput = (EditText) view.findViewById(R.id.editSongStreamUrl);
        final TextView localFileText = (TextView) view.findViewById(R.id.textSongLocalFile);

        dialogSongCoverPreview = (ImageView) view.findViewById(R.id.imgSongCoverPreview);
        dialogSongCoverPath = (TextView) view.findViewById(R.id.textSongCoverPath);

        pendingAudioPath = oldSong != null ? oldSong.getLocalFilePath() : null;
        pendingSongCoverPath = oldSong != null ? oldSong.getCoverImagePath() : null;

        activeAudioPathView = localFileText;
        updateAudioLabel(localFileText, pendingAudioPath);

        if (oldSong != null) {
            titleInput.setText(oldSong.getTitle());
            artistInput.setText(oldSong.getArtist());
            durationInput.setText(String.valueOf(oldSong.getDurationMs() / 1000));

            if (!TextUtils.isEmpty(oldSong.getStreamUrl()))
                streamInput.setText(oldSong.getStreamUrl());

            if (!TextUtils.isEmpty(oldSong.getCoverImagePath())) {
                dialogSongCoverPreview.setImageURI(Uri.parse(oldSong.getCoverImagePath()));
                dialogSongCoverPath.setText(oldSong.getCoverImagePath());
            } else if (oldSong.getCoverResId() != 0) {
                dialogSongCoverPreview.setImageResource(oldSong.getCoverResId());
            }
        }

        view.findViewById(R.id.buttonPickSongCover).setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) { pickSongCover(); }
        });

        view.findViewById(R.id.buttonPickLocalAudio).setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) { pickAudioFile(); }
        });

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle(oldSong == null ? "添加歌曲" : "编辑歌曲")
                .setView(view)
                .setPositiveButton("确定", null)
                .setNegativeButton("取消", null)
                .create();

        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override public void onDismiss(DialogInterface dialogInterface) {
                activeAudioPathView = null;
                pendingAudioPath = null;
                pendingSongCoverPath = null;
            }
        });

        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override public void onShow(DialogInterface d) {
                final AlertDialog ad = (AlertDialog) d;
                Button btn = ad.getButton(AlertDialog.BUTTON_POSITIVE);

                btn.setOnClickListener(new View.OnClickListener() {
                    @Override public void onClick(View v) {

                        String title = titleInput.getText().toString().trim();
                        String artist = artistInput.getText().toString().trim();
                        String durText = durationInput.getText().toString().trim();
                        String streamUrl = streamInput.getText().toString().trim();

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

                        boolean hasBuiltInAudio = oldSong != null && oldSong.getAudioResId() != 0;
                        if (TextUtils.isEmpty(pendingAudioPath) &&
                                TextUtils.isEmpty(streamUrl) &&
                                !hasBuiltInAudio) {
                            streamInput.setError(getString(R.string.error_song_source_required));
                            return;
                        }

                        String id = oldSong == null ? System.currentTimeMillis() + "" : oldSong.getId();

                        Song newSong;

                        if (!TextUtils.isEmpty(pendingAudioPath)) {
                            newSong = new Song(
                                    id, title, artist, "",
                                    duration,
                                    pendingAudioPath,
                                    oldSong != null ? oldSong.getCoverResId() : null
                            );

                        } else if (!TextUtils.isEmpty(streamUrl)) {
                            newSong = new Song(
                                    id, title, artist, "",
                                    duration,
                                    streamUrl,
                                    oldSong != null ? oldSong.getCoverUrl() : null,
                                    oldSong != null ? oldSong.getCoverResId() : null
                            );

                        } else {
                            newSong = new Song(
                                    id, title, artist, "",
                                    duration,
                                    oldSong != null ? oldSong.getAudioResId() : 0,
                                    oldSong != null ? oldSong.getCoverResId() : 0
                            );
                        }

                        if (!TextUtils.isEmpty(pendingSongCoverPath)) {
                            newSong.setCoverImagePath(pendingSongCoverPath);
                        }

                        if (oldSong == null)
                            songs.add(newSong);
                        else {
                            for (int i = 0; i < songs.size(); i++) {
                                if (songs.get(i).getId().equals(oldSong.getId())) {
                                    songs.set(i, newSong);
                                    break;
                                }
                            }
                        }

                        songAdapter.submit(songs);
                        playlist.setSongs(songs);

// ⭐ 如果歌单需要使用第一首歌封面作为封面，自动更新歌单封面
                        if (!songs.isEmpty()) {
                            Song first = songs.get(0);
                            if (first.getCoverImagePath() != null) {
                                playlist.setCoverUrl(first.getCoverImagePath());
                            }
                        }

// ⭐ 写入数据库
                        playlistRepository.updatePlaylist(playlist);
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
                    @Override public void onClick(DialogInterface dialog, int which) {
                        songs.remove(song);
                        songAdapter.submit(songs);
                    }
                })
                .setNegativeButton("取消", null)
                .show();
    }

    private void savePlaylist() {
        String title = titleInput.getText().toString().trim();
        if (TextUtils.isEmpty(title)) {
            titleInput.setError("标题不能为空");
            return;
        }

        Playlist updated = playlist.copyWith(
                title,
                descriptionInput.getText().toString().trim(),
                null,
                coverUri,
                coverUri == null ? playlist.getCoverResId() : null,
                playlist.getTags(),
                songs,
                playlist.getPlayCount(),
                playlist.getFavoriteCount()
        );

        playlistRepository.updatePlaylist(updated);

        Toast.makeText(this, "已保存歌单", Toast.LENGTH_SHORT).show();

        // ⭐ 通知上级页面需要刷新
        setResult(RESULT_OK);
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

    // ==================== 动态权限回调 ====================
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_PERMISSION_READ_STORAGE) {
            if (grantResults.length > 0 &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                Toast.makeText(this, "已授予读取图片权限", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this,
                        "没有读取权限，无法选择封面图片",
                        Toast.LENGTH_LONG).show();
            }
        }
    }

    // =================== SongAdapter ====================
    private static class SongAdapter extends RecyclerView.Adapter<SongAdapter.ViewHolder> {

        interface Listener {
            void onEdit(Song song);
            void onDelete(Song song);
        }

        private List<Song> songs;
        private Listener listener;

        SongAdapter(List<Song> songs) { this.songs = songs; }

        void setListener(Listener l) { listener = l; }

        void submit(List<Song> list) {
            songs = new ArrayList<>(list);
            notifyDataSetChanged();
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup p, int v) {
            View view = LayoutInflater.from(p.getContext())
                    .inflate(R.layout.item_admin_song, p, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder h, int pos) {
            final Song s = songs.get(pos);

            h.title.setText(s.getTitle());
            h.subtitle.setText(buildSubtitle(s));

            // 封面显示：自定义 > URL > resId
            if (!TextUtils.isEmpty(s.getCoverImagePath())) {
                try { h.cover.setImageURI(Uri.parse(s.getCoverImagePath())); }
                catch (Exception e) { h.cover.setImageResource(R.drawable.cover_playlist_placeholder); }

            } else if (s.getCoverResId() != 0) {
                h.cover.setImageResource(s.getCoverResId());
            } else {
                h.cover.setImageResource(R.drawable.cover_playlist_placeholder);
            }

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

        private String buildSubtitle(Song s) {
            StringBuilder sb = new StringBuilder()
                    .append(s.getArtist())
                    .append(" · ")
                    .append(s.getDurationMs() / 1000).append("秒");

            if (!TextUtils.isEmpty(s.getLocalFilePath())) sb.append(" · 本地音频");
            else if (!TextUtils.isEmpty(s.getStreamUrl())) sb.append(" · 流媒体");
            else if (s.getAudioResId() != 0) sb.append(" · 内置音频");

            if (!TextUtils.isEmpty(s.getCoverImagePath())) sb.append(" · 自定义封面");

            return sb.toString();
        }

        static class ViewHolder extends RecyclerView.ViewHolder {

            ImageView cover;
            TextView title, subtitle;
            Button edit, delete;

            ViewHolder(View v) {
                super(v);
                cover = (ImageView) v.findViewById(R.id.imgAdminSongCover);
                title = (TextView) v.findViewById(R.id.textSongTitle);
                subtitle = (TextView) v.findViewById(R.id.textSongSubtitle);
                edit = (Button) v.findViewById(R.id.buttonEditSong);
                delete = (Button) v.findViewById(R.id.buttonDeleteSong);
            }
        }

    }
}
