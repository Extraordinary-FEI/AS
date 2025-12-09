package com.example.cn.helloworld.ui.main;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.cn.helloworld.R;
import com.example.cn.helloworld.data.model.Playlist;

import java.util.List;
import java.util.Locale;

/**
 * 横向歌单适配器（使用 data.model.Playlist）
 */
public class PlaylistAdapter extends RecyclerView.Adapter<PlaylistAdapter.PlaylistViewHolder> {

    private final List<Playlist> playlists;
    private final OnPlaylistClickListener clickListener;

    public interface OnPlaylistClickListener {
        void onPlaylistClick(Playlist playlist);

        void onPlaylistClick(HomeModels.Playlist playlist);
    }

    public PlaylistAdapter(List<Playlist> playlists,
                           OnPlaylistClickListener clickListener) {
        this.playlists = playlists;
        this.clickListener = clickListener;
    }

    @Override
    public PlaylistViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_playlist, parent, false);
        return new PlaylistViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(PlaylistViewHolder holder, int position) {
        Playlist playlist = playlists.get(position);
        holder.bind(playlist, clickListener);
    }

    @Override
    public int getItemCount() {
        return playlists == null ? 0 : playlists.size();
    }

    static class PlaylistViewHolder extends RecyclerView.ViewHolder {
        final ImageView coverView;
        final TextView nameView;
        final TextView descriptionView;
        final TextView tagsView;
        final TextView metaView;

        PlaylistViewHolder(View itemView) {
            super(itemView);
            coverView = (ImageView) itemView.findViewById(R.id.playlist_cover);
            nameView = (TextView) itemView.findViewById(R.id.playlist_name);
            descriptionView = (TextView) itemView.findViewById(R.id.playlist_description);
            tagsView = (TextView) itemView.findViewById(R.id.playlist_tags);
            metaView = (TextView) itemView.findViewById(R.id.playlist_meta);
        }

        void bind(final Playlist playlist,
                  final OnPlaylistClickListener clickListener) {

            // 标题 & 描述
            nameView.setText(playlist.getTitle());
            descriptionView.setText(playlist.getDescription());

            // 标签
            String tagText = joinTags(playlist.getTags());
            if (tagText.length() == 0) {
                tagsView.setVisibility(View.GONE);
            } else {
                tagsView.setVisibility(View.VISIBLE);
                tagsView.setText(tagText);
            }

            // 统计信息
            metaView.setText(formatMeta(playlist));

            // 封面：优先 Uri，再次使用本地 resId
            String coverUrl = playlist.getCoverUrl();
            Integer coverResId = playlist.getCoverResId();
            Glide.clear(coverView);
            if (coverUrl != null && coverUrl.length() > 0) {
                Glide.with(coverView.getContext())
                        .load(coverUrl)
                        .centerCrop()
                        .placeholder(R.drawable.cover_playlist_placeholder)
                        .error(R.drawable.cover_playlist_placeholder)
                        .into(coverView);
            } else if (coverResId != null) {
                Glide.with(coverView.getContext())
                        .load(coverResId)
                        .centerCrop()
                        .placeholder(R.drawable.cover_playlist_placeholder)
                        .into(coverView);
            } else {
                coverView.setImageResource(R.drawable.cover_playlist_placeholder);
            }

            // 点击事件
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (clickListener != null) {
                        clickListener.onPlaylistClick(playlist);
                    }
                }
            });
        }

        private static String joinTags(List<String> tags) {
            if (tags == null || tags.isEmpty()) {
                return "";
            }
            StringBuilder builder = new StringBuilder();
            for (int i = 0; i < tags.size(); i++) {
                if (i > 0) {
                    builder.append(" · ");
                }
                builder.append(tags.get(i));
            }
            return builder.toString();
        }

        private static String formatMeta(Playlist playlist) {
            int trackCount =
                    playlist.getSongs() == null ? 0 : playlist.getSongs().size();
            return String.format(Locale.getDefault(),
                    "%d 首 · %d 次播放 · %d 人收藏",
                    trackCount,
                    playlist.getPlayCount(),
                    playlist.getFavoriteCount());
        }
    }
}
