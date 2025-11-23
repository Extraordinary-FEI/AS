package com.example.cn.helloworld.ui.playlist;

import android.content.Context;
import android.content.Intent;
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

/**
 * 将所有歌曲平铺展示的 Fragment，满足歌单 Tab “所有歌曲排列” 的需求。
 */
public class PlaylistLibraryFragment extends Fragment {

    private PlaylistRepository playlistRepository;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        playlistRepository = PlaylistRepository.getInstance();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_all_songs, container, false);
        RecyclerView recyclerView = (RecyclerView) root.findViewById(R.id.recycler_all_songs);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(new SongsAdapter(collectAllSongs(), new SongsAdapter.OnSongClickListener() {
            @Override
            public void onSongClick(Song song) {
                Context context = getContext();
                if (context != null) {
                    Intent intent = new Intent(context, MusicActivity.class);
                    intent.putExtra(MusicActivity.EXTRA_SONG_ID, song.getId());
                    intent.putExtra(MusicActivity.EXTRA_PLAYLIST_ID, findPlaylistIdForSong(song.getId()));
                    context.startActivity(intent);
                }
            }
        }));
        return root;
    }

    private List<Song> collectAllSongs() {
        List<Song> songs = new ArrayList<Song>();
        List<Playlist> playlists = playlistRepository.getAllPlaylists();
        for (int i = 0; i < playlists.size(); i++) {
            Playlist playlist = playlists.get(i);
            if (playlist != null && playlist.getSongs() != null) {
                songs.addAll(playlist.getSongs());
            }
        }
        return songs;
    }

    private String findPlaylistIdForSong(String songId) {
        if (TextUtils.isEmpty(songId)) {
            return null;
        }
        List<Playlist> playlists = playlistRepository.getAllPlaylists();
        for (int i = 0; i < playlists.size(); i++) {
            Playlist playlist = playlists.get(i);
            if (playlist != null && playlist.getSongs() != null) {
                for (int j = 0; j < playlist.getSongs().size(); j++) {
                    Song song = playlist.getSongs().get(j);
                    if (song != null && songId.equals(song.getId())) {
                        return playlist.getId();
                    }
                }
            }
        }
        return null;
    }

    private static class SongsAdapter extends RecyclerView.Adapter<SongsAdapter.ViewHolder> {

        interface OnSongClickListener {
            void onSongClick(Song song);
        }

        private final List<Song> songs;
        private final OnSongClickListener listener;

        SongsAdapter(List<Song> songs, OnSongClickListener listener) {
            this.songs = songs;
            this.listener = listener;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_song, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
            final Song song = songs.get(position);
            holder.title.setText(song.getTitle());
            holder.artist.setText(song.getArtist());
            holder.duration.setText(formatDuration(song.getDurationMs()));
            if (TextUtils.isEmpty(song.getDescription())) {
                holder.description.setVisibility(View.GONE);
            } else {
                holder.description.setVisibility(View.VISIBLE);
                holder.description.setText(song.getDescription());
            }
            holder.cover.setImageResource(song.getCoverResId());
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        listener.onSongClick(song);
                    }
                }
            });
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

        private static String formatDuration(long durationMs) {
            long totalSeconds = durationMs / 1000;
            long minutes = totalSeconds / 60;
            long seconds = totalSeconds % 60;
            return String.format("%02d:%02d", minutes, seconds);
        }
    }
}
