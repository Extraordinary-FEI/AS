package com.example.cn.helloworld.ui.user;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.cn.helloworld.R;
import com.example.cn.helloworld.ui.main.HomeModels;

import java.util.List;

class FavoriteTaskAdapter extends RecyclerView.Adapter<FavoriteTaskAdapter.ViewHolder> {

    private final List<HomeModels.SupportTask> tasks;
    private final FavoriteItemRemover<HomeModels.SupportTask> remover;

    FavoriteTaskAdapter(List<HomeModels.SupportTask> tasks, FavoriteItemRemover<HomeModels.SupportTask> remover) {
        this.tasks = tasks;
        this.remover = remover;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_favorite_simple, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        final HomeModels.SupportTask task = tasks.get(position);
        holder.title.setText(task.getName());
        holder.subtitle.setText(task.getLocation());
        holder.tag.setText(R.string.favorite_section_tasks);
        holder.icon.setImageResource(R.drawable.ic_category_task);
        holder.removeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int adapterPosition = holder.getAdapterPosition();
                if (adapterPosition != RecyclerView.NO_POSITION && remover != null) {
                    remover.onRemove(task, adapterPosition);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return tasks == null ? 0 : tasks.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        final TextView title;
        final TextView subtitle;
        final TextView tag;
        final ImageView icon;
        final ImageButton removeButton;

        ViewHolder(View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.text_fav_title);
            subtitle = (TextView) itemView.findViewById(R.id.text_fav_subtitle);
            tag = (TextView) itemView.findViewById(R.id.text_fav_tag);
            icon = (ImageView) itemView.findViewById(R.id.image_fav_icon);
            removeButton = (ImageButton) itemView.findViewById(R.id.button_remove);
        }
    }
}

