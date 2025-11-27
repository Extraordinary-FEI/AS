package com.example.cn.helloworld.ui.user;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.cn.helloworld.R;
import com.example.cn.helloworld.ui.main.HomeModels;

import java.util.List;

class FavoriteTaskAdapter extends RecyclerView.Adapter<FavoriteTaskAdapter.ViewHolder> {

    private final List<HomeModels.SupportTask> tasks;

    FavoriteTaskAdapter(List<HomeModels.SupportTask> tasks) {
        this.tasks = tasks;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_favorite_simple, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        HomeModels.SupportTask task = tasks.get(position);
        holder.title.setText(task.getName());
        holder.subtitle.setText(task.getLocation());
    }

    @Override
    public int getItemCount() {
        return tasks == null ? 0 : tasks.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        final TextView title;
        final TextView subtitle;

        ViewHolder(View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.text_fav_title);
            subtitle = (TextView) itemView.findViewById(R.id.text_fav_subtitle);
        }
    }
}

