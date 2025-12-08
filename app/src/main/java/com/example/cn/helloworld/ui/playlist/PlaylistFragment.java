package com.example.cn.helloworld.ui.playlist;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.cn.helloworld.MusicActivity;
import com.example.cn.helloworld.R;
import com.example.cn.helloworld.data.model.Playlist;
import com.example.cn.helloworld.data.model.Song;
import com.example.cn.helloworld.data.playlist.PlaylistRepository;

import java.util.ArrayList;
import java.util.List;

public class PlaylistFragment extends Fragment {

    public static final String ARG_PLAYLIST_ID = "playlist_id";

    public static PlaylistFragment newInstance(String playlistId) {
        PlaylistFragment f = new PlaylistFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PLAYLIST_ID, playlistId);
        f.setArguments(args);
        return f;
    }

    private PlaylistRepository playlistRepository;

    private ImageView coverImg;
    private TextView titleView;
    private TextView descView;
    private TextView tagView;
    private TextView metaView;
    private RecyclerView songList;

    private String playlistId;
    private SongAdapter songAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_playlist, container, false);

        playlistRepository = PlaylistRepository.getInstance(getContext());

        coverImg = (ImageView) view.findViewById(R.id.image_playlist_cover);
        titleView = (TextView) view.findViewById(R.id.text_playlist_title);
        descView = (TextView) view.findViewById(R.id.text_playlist_description);
        tagView = (TextView) view.findViewById(R.id.text_playlist_tags);
        metaView = (TextView) view.findViewById(R.id.text_playlist_meta);
        songList = (RecyclerView) view.findViewById(R.id.recycler_playlist_songs);   // ✔ 正确 ID

        songList.setLayoutManager(new LinearLayoutManager(getContext()));
        songAdapter = new SongAdapter(new ArrayList<Song>(), new SongAdapter.OnSongClickListener() {
            @Override
            public void onSongClick(Song song) {
                Context context = getContext();
                if (context == null || song == null || playlistId == null) {
                    return;
                }
                Intent intent = MusicActivity.createIntent(context, playlistId, song.getId());
                context.startActivity(intent);
            }
        });
        songList.setAdapter(songAdapter);

        loadPlaylist();

        return view;
    }

    private void loadPlaylist() {
        if (getArguments() == null) return;

        playlistId = getArguments().getString(ARG_PLAYLIST_ID);
        Playlist playlist = playlistRepository.getById(playlistId);
        if (playlist == null) return;

        // 封面
        if (playlist.getCoverUrl() != null && !playlist.getCoverUrl().isEmpty()) {
            coverImg.setImageURI(Uri.parse(playlist.getCoverUrl()));
        } else if (playlist.getCoverResId() != null) {
            coverImg.setImageResource(playlist.getCoverResId());
        }

        titleView.setText(playlist.getTitle());
        descView.setText(playlist.getDescription());

        // 标签
        if (playlist.getTags() != null && !playlist.getTags().isEmpty()) {
            tagView.setText(android.text.TextUtils.join(" / ", playlist.getTags()));
        }

        // 元信息：首数 / 播放量
        metaView.setText(
                playlist.getSongs().size() + " 首 · " + playlist.getPlayCount() + " 次播放"
        );

        songAdapter.setPlaylistCover(playlist.getCoverUrl(), playlist.getCoverResId());
        songAdapter.submit(playlist.getSongs());
    }


    // ================== Adapter ==================

    private static class SongAdapter extends RecyclerView.Adapter<SongAdapter.Holder> {

        interface OnSongClickListener {
            void onSongClick(Song song);
        }

        private List<Song> songs;
        private String playlistCoverUrl;
        private Integer playlistCoverResId;
        private final OnSongClickListener listener;

        SongAdapter(List<Song> s, OnSongClickListener listener) {
            songs = s;
            this.listener = listener;
        }

        void submit(List<Song> list) {
            songs = new ArrayList<>(list);
            notifyDataSetChanged();
        }

        void setPlaylistCover(String coverUrl, Integer coverResId) {
            this.playlistCoverUrl = coverUrl;
            this.playlistCoverResId = coverResId;
        }

        @NonNull
        @Override
        public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_song, parent, false);
            return new Holder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull Holder h, int pos) {
            final Song s = songs.get(pos);

            h.title.setText(s.getTitle());
            h.artist.setText(s.getArtist());
            h.duration.setText(formatDuration(s.getDurationMs()));
            h.description.setText(s.getDescription());

            bindCover(h.cover, s);
            h.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        listener.onSongClick(s);
                    }
                }
            });
        }

        @Override
        public int getItemCount() {
            return songs.size();
        }

        static class Holder extends RecyclerView.ViewHolder {

            TextView title, artist, duration, description;
            ImageView cover;

            Holder(View v) {
                super(v);
                title = (TextView) v.findViewById(R.id.text_song_title);
                artist = (TextView) v.findViewById(R.id.text_song_artist);
                duration = (TextView) v.findViewById(R.id.text_song_duration);
                description = (TextView) v.findViewById(R.id.text_song_description);
                cover = (ImageView) v.findViewById(R.id.image_song_cover);
            }
        }

        private void bindCover(ImageView coverView, Song song) {
            if (coverView == null || song == null) {
                return;
            }

            if (!TextUtils.isEmpty(song.getCoverImagePath())) {
                coverView.setImageURI(Uri.parse(song.getCoverImagePath()));
            } else if (!TextUtils.isEmpty(song.getCoverUrl())) {
                coverView.setImageURI(Uri.parse(song.getCoverUrl()));
            } else if (song.getCoverResId() != 0) {
                coverView.setImageResource(song.getCoverResId());
            } else if (!TextUtils.isEmpty(playlistCoverUrl)) {
                coverView.setImageURI(Uri.parse(playlistCoverUrl));
            } else if (playlistCoverResId != null) {
                coverView.setImageResource(playlistCoverResId);
            } else {
                coverView.setImageResource(R.drawable.cover_playlist_placeholder);
            }
        }

        private static String formatDuration(long durationMs) {
            long totalSeconds = durationMs / 1000;
            long minutes = totalSeconds / 60;
            long seconds = totalSeconds % 60;
            return String.format(java.util.Locale.getDefault(), "%02d:%02d", minutes, seconds);
        }
    }
}

