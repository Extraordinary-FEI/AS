package com.example.cn.helloworld.ui.main;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.cn.helloworld.R;

import java.util.List;
import java.util.Locale;

/**
 * 横向歌单适配器。
 */
public class PlaylistAdapter extends RecyclerView.Adapter<PlaylistAdapter.PlaylistViewHolder> {

    private final List<HomeModels.Playlist> playlists;
    private final OnPlaylistClickListener clickListener;

    public interface OnPlaylistClickListener {
        void onPlaylistClick(HomeModels.Playlist playlist);
    }

    public PlaylistAdapter(List<HomeModels.Playlist> playlists, OnPlaylistClickListener clickListener) {
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
        HomeModels.Playlist playlist = playlists.get(position);
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

        void bind(final HomeModels.Playlist playlist, final OnPlaylistClickListener clickListener) {
            nameView.setText(playlist.getName());
            descriptionView.setText(playlist.getDescription());
            String tagText = joinTags(playlist.getTags());
            if (tagText.length() == 0) {
                tagsView.setVisibility(View.GONE);
            } else {
                tagsView.setVisibility(View.VISIBLE);
                tagsView.setText(tagText);
            }
            metaView.setText(formatMeta(playlist));
            tintCoverBackground(coverView.getContext(), coverView, playlist.getCoverColorResId());
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (clickListener != null) {
                        clickListener.onPlaylistClick(playlist);
                    }
                }
            });
        }

        private void tintCoverBackground(Context context, ImageView imageView, int colorResId) {
            Drawable background = imageView.getBackground();
            if (background instanceof GradientDrawable) {
                ((GradientDrawable) background.mutate()).setColor(ContextCompat.getColor(context, colorResId));
            }
        }

        private String formatMeta(HomeModels.Playlist playlist) {
            return String.format(Locale.getDefault(), "%d 首 · %s 次播放",
                    playlist.getTrackCount(),
                    formatCount(playlist.getPlayCount()));
        }

        private String joinTags(List<String> tags) {
            if (tags == null || tags.isEmpty()) {
                return "";
            }
            StringBuilder builder = new StringBuilder();
            for (int i = 0; i < tags.size(); i++) {
                if (builder.length() > 0) {
                    builder.append(" / ");
                }
                builder.append(tags.get(i));
            }
            return builder.toString();
        }

        private String formatCount(long value) {
            return String.format(Locale.getDefault(), "%,d", value);
        }
    }
}
