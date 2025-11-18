package com.example.cn.helloworld.ui.playlist;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
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
import com.example.cn.helloworld.data.playlist.PlaylistRepository;
import com.example.cn.helloworld.data.model.Song;

import java.util.List;

/**
 * 歌单详情 Fragment（封面 + 歌曲列表）
 */
public class PlaylistFragment extends Fragment {

    private static final String ARG_PLAYLIST_ID = "playlist_id";

    private String playlistId;
    private PlaylistRepository playlistRepository;

    public static PlaylistFragment newInstance(String playlistId) {
        PlaylistFragment fragment = new PlaylistFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PLAYLIST_ID, playlistId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        playlistRepository = PlaylistRepository.getInstance();

        if (getArguments() != null) {
            playlistId = getArguments().getString(ARG_PLAYLIST_ID);
        }
    }

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState
    ) {
        View root = inflater.inflate(R.layout.fragment_playlist, container, false);

        Playlist playlist = playlistRepository.getById(playlistId);
        if (playlist != null) {
            bindHeader(root, playlist);
            bindSongList(root, playlist.getSongs());
        }

        return root;
    }

    /**
     * 绑定顶部封面信息区域
     */
    private void bindHeader(View root, Playlist playlist) {
        ImageView cover = (ImageView) root.findViewById(R.id.image_playlist_cover);
        TextView title = (TextView) root.findViewById(R.id.text_playlist_title);
        TextView description = (TextView) root.findViewById(R.id.text_playlist_description);
        TextView tags = (TextView) root.findViewById(R.id.text_playlist_tags);
        TextView meta = (TextView) root.findViewById(R.id.text_playlist_meta);

        title.setText(playlist.getTitle());
        description.setText(playlist.getDescription());

        // 标签
        if (playlist.getTags() != null && !playlist.getTags().isEmpty()) {
            tags.setText(TextUtils.join(" / ", playlist.getTags()));
        } else {
            tags.setText("无标签");
        }

        // 播放统计信息
        meta.setText(String.format(
                "共 %d 首 · %d 次播放 · %d 人收藏",
                playlist.getSongs().size(),
                playlist.getPlayCount(),
                playlist.getFavoriteCount()
        ));

        // 封面（本地 resId）
        if (playlist.getCoverResId() != null) {
            cover.setImageResource(playlist.getCoverResId());
        } else {
            cover.setImageResource(R.drawable.cover_playlist_placeholder);
        }
    }

    /**
     * 绑定歌曲列表
     */
    @RequiresApi(api = Build.VERSION_CODES.GINGERBREAD)
    private void bindSongList(View root, final List<Song> songs) {
        RecyclerView recyclerView = (RecyclerView) root.findViewById(R.id.recycler_playlist_songs);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(new SongsAdapter(songs, new SongsAdapter.OnSongClickListener() {
            @Override
            public void onSongClick(Song song, int position) {
                Context context = getContext();
                if (context != null) {
                    Intent intent = new Intent(context, MusicActivity.class);
                    intent.putExtra(MusicActivity.EXTRA_PLAYLIST_ID, playlistId);
                    intent.putExtra(MusicActivity.EXTRA_SONG_ID, song.getId());
                    context.startActivity(intent);
                }
            }
        }));
    }

    /**
     * 歌曲列表 Adapter
     */
    private static class SongsAdapter extends RecyclerView.Adapter<SongsAdapter.ViewHolder> {

        interface OnSongClickListener {
            void onSongClick(Song song, int position);
        }

        private final List<Song> songs;
        private final OnSongClickListener clickListener;

        SongsAdapter(List<Song> songs, OnSongClickListener listener) {
            this.songs = songs;
            this.clickListener = listener;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_song, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            final Song song = songs.get(position);
            final ViewHolder songHolder = holder;

            holder.title.setText(song.getTitle());
            holder.artist.setText(song.getArtist());
            holder.duration.setText(formatDuration(song.getDurationMs()));

            // 描述
            if (TextUtils.isEmpty(song.getDescription())) {
                holder.description.setVisibility(View.GONE);
            } else {
                holder.description.setVisibility(View.VISIBLE);
                holder.description.setText(song.getDescription());
            }

            // 封面
            holder.cover.setImageResource(song.getCoverResId());

            if (clickListener != null) {
                songHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int adapterPosition = songHolder.getAdapterPosition();
                        if (adapterPosition != RecyclerView.NO_POSITION) {
                            clickListener.onSongClick(song, adapterPosition);
                        }

                    }
                });
            }
        }

        @Override
        public int getItemCount() {
            return songs == null ? 0 : songs.size();
        }

        static class ViewHolder extends RecyclerView.ViewHolder {
            ImageView cover;
            TextView title;
            TextView artist;
            TextView duration;
            TextView description;

            ViewHolder(View itemView) {
                super(itemView);
                cover = (ImageView) itemView.findViewById(R.id.image_song_cover);
                title = (TextView) itemView.findViewById(R.id.text_song_title);
                artist = (TextView) itemView.findViewById(R.id.text_song_artist);
                duration = (TextView) itemView.findViewById(R.id.text_song_duration);
                description = (TextView) itemView.findViewById(R.id.text_song_description);
            }
        }

        private static String formatDuration(long ms) {
            int seconds = (int) (ms / 1000);
            int minutes = seconds / 60;
            seconds = seconds % 60;
            return String.format("%02d:%02d", minutes, seconds);
        }
    }
}
