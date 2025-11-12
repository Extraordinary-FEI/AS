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
import com.example.cn.helloworld.data.playlist.PlaylistRepository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

/**
 * 歌单详情页，展示歌单基础信息与歌曲列表。
 */
public class PlaylistFragment extends Fragment {

    private static final String ARG_PLAYLIST_ID = "arg_playlist_id";

    private ImageView playlistCover;
    private TextView playlistTitle;
    private TextView playlistDescription;
    private TextView playlistTags;
    private TextView playlistMeta;
    private RecyclerView songsRecyclerView;
    private SongsAdapter songsAdapter;
    private PlaylistRepository playlistRepository;
    private String playlistId;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        playlistRepository = PlaylistRepository.getInstance();
        Bundle arguments = getArguments();
        if (arguments != null) {
            playlistId = arguments.getString(ARG_PLAYLIST_ID);
        }
    }

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
        playlistTags = (TextView) view.findViewById(R.id.text_playlist_tags);
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

        loadPlaylist();
    }

    private void loadPlaylist() {
        Playlist playlist = playlistRepository.findById(playlistId);
        if (playlist == null) {
            playlistTitle.setText(R.string.playlist_not_found);
            playlistDescription.setText(R.string.playlist_not_found_description);
            playlistTags.setText("");
            playlistTags.setVisibility(View.GONE);
            playlistMeta.setText("");
            songsAdapter.setSongs(Collections.<Song>emptyList());
            Toast.makeText(getContext(), R.string.playlist_not_found_toast, Toast.LENGTH_SHORT).show();
            playlistCover.setImageResource(R.drawable.cover_playlist_placeholder);
            return;
        }
        bindPlaylist(playlist);
        songsAdapter.setSongs(playlist.getSongs());
    }

    private void bindPlaylist(Playlist playlist) {
        playlistTitle.setText(playlist.getTitle());
        playlistDescription.setText(playlist.getDescription());
        bindTags(playlist.getTags());
        playlistMeta.setText(buildMeta(playlist));

        Integer coverResId = playlist.getCoverResId();
        if (coverResId != null) {
            playlistCover.setImageResource(coverResId.intValue());
        } else {
            playlistCover.setImageResource(R.drawable.cover_playlist_placeholder);
        }
    }

    private void bindTags(List<String> tags) {
        if (tags == null || tags.isEmpty()) {
            playlistTags.setVisibility(View.GONE);
            playlistTags.setText("");
        } else {
            playlistTags.setVisibility(View.VISIBLE);
            playlistTags.setText(joinTags(tags));
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
        String durationPart = String.format(Locale.getDefault(), "共 %d 首 · %d 分钟", count, totalMinutes);
        return String.format(Locale.getDefault(), "%s · %s 次播放 · %s 人收藏",
                durationPart,
                formatCount(playlist.getPlayCount()),
                formatCount(playlist.getFavoriteCount()));
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

    private String formatCount(long count) {
        return String.format(Locale.getDefault(), "%,d", count);
    }

    public static PlaylistFragment newInstance(String playlistId) {
        PlaylistFragment fragment = new PlaylistFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PLAYLIST_ID, playlistId);
        fragment.setArguments(args);
        return fragment;
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
                    coverView.setImageResource(R.drawable.cover_playlist_placeholder);
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
