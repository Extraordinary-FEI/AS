package com.example.cn.helloworld.ui.playlist;

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

import com.example.cn.helloworld.R;
import com.example.cn.helloworld.data.model.Playlist;
import com.example.cn.helloworld.data.model.Song;
import com.example.cn.helloworld.data.repository.PlaylistRepository;

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
        playlistRepository = new PlaylistRepository(getContext());

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

        // 播放统计信息（如果你的 Playlist 内没有统计字段，可手动写）
        meta.setText(String.format(
                "共 %d 首 · %d 次播放 · %d 人收藏",
                playlist.getSongs().size(),
                playlist.getPlayCount(),
                playlist.getFavoriteCount()
        ));

        // 加载封面：支持本地 resId 或 URL
        Integer resId = playlist.getCoverResId();
        String url = playlist.getCoverUrl();

        if (resId != null) {
            cover.setImageResource(resId);
        } else if (!TextUtils.isEmpty(url)) {
            Glide.with(getContext()).load(url).into(cover);
        } else {
            cover.setImageResource(R.drawable.cover_playlist_placeholder);
        }
    }

    /**
     * 绑定歌曲列表
     */
    @RequiresApi(api = Build.VERSION_CODES.GINGERBREAD)
    private void bindSongList(View root, List<Song> songs) {
        RecyclerView recyclerView = (RecyclerView) root.findViewById(R.id.recycler_playlist_songs);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(new SongsAdapter(songs));
    }

    /**
     * 歌曲列表 Adapter
     */
    private static class SongsAdapter extends RecyclerView.Adapter<SongsAdapter.ViewHolder> {

        private final List<Song> songs;

        SongsAdapter(List<Song> songs) {
            this.songs = songs;
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
            Song song = songs.get(position);

            holder.title.setText(song.getTitle());
            holder.artist.setText(song.getArtist());
            holder.duration.setText(formatDuration(song.getDurationMs()));

            // 描述（允许为空）
            if (TextUtils.isEmpty(song.getDescription())) {
                holder.description.setVisibility(View.GONE);
            } else {
                holder.description.setVisibility(View.VISIBLE);
                holder.description.setText(song.getDescription());
            }

            // 封面
            Integer coverRes = song.getCoverResId();
            String coverUrl = song.getCoverUrl();

            if (coverRes != null) {
                holder.cover.setImageResource(coverRes);
            } else if (!TextUtils.isEmpty(coverUrl)) {
                Glide.with(holder.itemView.getContext())
                        .load(coverUrl)
                        .into(holder.cover);
            } else {
                holder.cover.setImageResource(R.drawable.cover_playlist_placeholder);
            }
        }

        @Override
        public int getItemCount() {
            return songs == null ? 0 : songs.size();
        }

        /**
         * ViewHolder：对应 item_song.xml
         */
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

        /** 毫秒 → MM:SS */
        private static String formatDuration(long ms) {
            int seconds = (int) (ms / 1000);
            int minutes = seconds / 60;
            seconds = seconds % 60;
            return String.format("%02d:%02d", minutes, seconds);
        }
    }
}
