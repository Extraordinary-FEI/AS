package com.example.cn.helloworld.ui.main;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
<<<<<<< HEAD

=======
>>>>>>> 54c08ab2b5a048159174a0026e1d331be437f64c
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.cn.helloworld.R;

import java.util.List;

/**
 * 横向歌单适配器。
 */
public class PlaylistAdapter extends RecyclerView.Adapter<PlaylistAdapter.PlaylistViewHolder> {

    private final List<HomeModels.Playlist> playlists;

    public PlaylistAdapter(List<HomeModels.Playlist> playlists) {
        this.playlists = playlists;
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
        holder.nameView.setText(playlist.getName());
        holder.descriptionView.setText(playlist.getDescription());
        tintCoverBackground(holder.coverView.getContext(), holder.coverView, playlist.getCoverColorResId());
    }

    private void tintCoverBackground(Context context, ImageView imageView, int colorResId) {
        Drawable background = imageView.getBackground();
        if (background instanceof GradientDrawable) {
            ((GradientDrawable) background.mutate()).setColor(ContextCompat.getColor(context, colorResId));
        }
    }

    @Override
    public int getItemCount() {
        return playlists == null ? 0 : playlists.size();
    }

    static class PlaylistViewHolder extends RecyclerView.ViewHolder {
        final ImageView coverView;
        final TextView nameView;
        final TextView descriptionView;

        PlaylistViewHolder(View itemView) {
            super(itemView);

            coverView = (ImageView) itemView.findViewById(R.id.playlist_cover);
            nameView = (TextView) itemView.findViewById(R.id.playlist_name);
            descriptionView = (TextView) itemView.findViewById(R.id.playlist_description);

        }
    }
}
