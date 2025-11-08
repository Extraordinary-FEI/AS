package com.example.cn.helloworld.ui.playlist;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.cn.helloworld.R;
import com.example.cn.helloworld.data.model.Playlist;
import com.example.cn.helloworld.data.model.Song;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * 歌单详情页，展示歌单基础信息与歌曲列表。
 */
public class PlaylistFragment extends Fragment {

    private ImageView playlistCover;
    private TextView playlistTitle;
    private TextView playlistDescription;
    private TextView playlistMeta;
    private RecyclerView songsRecyclerView;
    private SongsAdapter songsAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_playlist, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        playlistCover = (ImageView) view.findViewById(R.id.image_playlist_cover);
        playlistTitle = (TextView) view.findViewById(R.id.text_playlist_title);
        playlistDescription = (TextView) view.findViewById(R.id.text_playlist_description);
        playlistMeta = (TextView) view.findViewById(R.id.text_playlist_meta);
        songsRecyclerView = (RecyclerView) view.findViewById(R.id.recycler_playlist_songs);

        songsAdapter = new SongsAdapter(new SongsAdapter.OnSongClickListener() {
            @Override
            public void onSongClick(Song song) {
                Toast.makeText(getContext(), "准备播放：" + song.getTitle(), Toast.LENGTH_SHORT).show();
            }
        });
        songsRecyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
        songsRecyclerView.setHasFixedSize(true);
        songsRecyclerView.setAdapter(songsAdapter);

        Playlist playlist = buildMockPlaylist();
        bindPlaylist(playlist);
        songsAdapter.setSongs(playlist.getSongs());
    }

    private void bindPlaylist(Playlist playlist) {
        playlistTitle.setText(playlist.getTitle());
        playlistDescription.setText(playlist.getDescription());
        playlistMeta.setText(buildMeta(playlist));

        Integer coverResId = playlist.getCoverResId();
        if (coverResId != null) {
            playlistCover.setImageResource(coverResId.intValue());
        } else {
            playlistCover.setImageResource(R.drawable.cover_lisao);
        }
    }

    private String buildMeta(Playlist playlist) {
        List<Song> songs = playlist.getSongs();
        int count = songs.size();
        long totalDurationMs = 0L;
        for (int i = 0; i < songs.size(); i++) {
            totalDurationMs += songs.get(i).getDurationMs();
        }
        int totalMinutes = (int) (totalDurationMs / 1000 / 60);
        List<String> tags = playlist.getTags();
        if (!tags.isEmpty()) {
            return String.format(Locale.getDefault(), "共 %d 首 · %d 分钟 · %s",
                    count, totalMinutes, joinTags(tags));
        }
        return String.format(Locale.getDefault(), "共 %d 首 · %d 分钟", count, totalMinutes);
    }

    private String joinTags(List<String> tags) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < tags.size(); i++) {
            if (builder.length() > 0) {
                builder.append(" / ");
            }
            builder.append(tags.get(i));
        }
        return builder.toString();
    }

    private Playlist buildMockPlaylist() {
        List<Song> songs = new ArrayList<Song>();
        songs.add(new Song(
                "song-lisao",
                "离骚",
                "许嵩",
                245000L,
                "https://example.com/audio/li_sao.mp3",
                "沉静悠扬的钢琴旋律，适合夜晚放松",
                null,
                Integer.valueOf(R.drawable.cover_lisao)
        ));
        songs.add(new Song(
                "song-nishuo",
                "你说",
                "林俊杰",
                214000L,
                "https://example.com/audio/ni_shuo.mp3",
                "温柔治愈系，诉说心底的故事",
                null,
                Integer.valueOf(R.drawable.cover_nishuo)
        ));
        songs.add(new Song(
                "song-baobei",
                "宝贝",
                "张悬",
                198000L,
                "https://example.com/audio/bao_bei.mp3",
                "轻快民谣，伴你醒来迎接阳光",
                null,
                Integer.valueOf(R.drawable.cover_baobei)
        ));

        List<String> tags = new ArrayList<String>();
        tags.add("华语");
        tags.add("治愈");
        tags.add("安静");

        return new Playlist(
                "playlist-classic",
                "轻听华语 · 治愈精选",
                "精选 2000 年后治愈系华语歌曲，适合午后阅读或夜晚放松聆听。",
                "https://example.com/playlist/classic-heal",
                null,
                Integer.valueOf(R.drawable.cover_lisao),
                tags,
                songs
        );
    }

    public static PlaylistFragment newInstance() {
        return new PlaylistFragment();
    }

    private static class SongsAdapter extends RecyclerView.Adapter<SongsAdapter.SongViewHolder> {

        interface OnSongClickListener {
            void onSongClick(Song song);
        }

        private final List<Song> songs = new ArrayList<Song>();
        private final OnSongClickListener clickListener;

        SongsAdapter(OnSongClickListener clickListener) {
            this.clickListener = clickListener;
        }

        void setSongs(List<Song> newSongs) {
            songs.clear();
            if (newSongs != null) {
                songs.addAll(newSongs);
            }
            notifyDataSetChanged();
        }

        @NonNull
        @Override
        public SongViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_song, parent, false);
            return new SongViewHolder(itemView, clickListener);
        }

        @Override
        public void onBindViewHolder(@NonNull SongViewHolder holder, int position) {
            holder.bind(songs.get(position));
        }

        @Override
        public int getItemCount() {
            return songs.size();
        }

        private static class SongViewHolder extends RecyclerView.ViewHolder {

            private final ImageView coverView;
            private final TextView titleView;
            private final TextView artistView;
            private final TextView durationView;
            private final TextView descriptionView;
            private final OnSongClickListener clickListener;

            SongViewHolder(View itemView, OnSongClickListener clickListener) {
                super(itemView);
                this.clickListener = clickListener;
                coverView = (ImageView) itemView.findViewById(R.id.image_song_cover);
                titleView = (TextView) itemView.findViewById(R.id.text_song_title);
                artistView = (TextView) itemView.findViewById(R.id.text_song_artist);
                durationView = (TextView) itemView.findViewById(R.id.text_song_duration);
                descriptionView = (TextView) itemView.findViewById(R.id.text_song_description);
            }

            void bind(final Song song) {
                titleView.setText(song.getTitle());
                artistView.setText(song.getArtist());
                durationView.setText(formatDuration(song.getDurationMs()));

                String description = song.getDescription();
                if (description == null || description.length() == 0) {
                    descriptionView.setVisibility(View.GONE);
                } else {
                    descriptionView.setVisibility(View.VISIBLE);
                    descriptionView.setText(description);
                }

                Integer coverRes = song.getCoverResId();
                if (coverRes != null) {
                    coverView.setImageResource(coverRes.intValue());
                } else {
                    coverView.setImageResource(R.drawable.cover_baobei);
                }

                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (clickListener != null) {
                            clickListener.onSongClick(song);
                        }
                    }
                });
            }

            private String formatDuration(long durationMs) {
                long totalSeconds = durationMs / 1000L;
                long minutes = totalSeconds / 60L;
                long seconds = totalSeconds % 60L;
                return String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds);
            }
        }
    }
}
