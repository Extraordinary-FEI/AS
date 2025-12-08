package com.example.cn.helloworld.ui.user;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.cn.helloworld.R;

import java.util.List;

public class CheckinLocationAdapter extends RecyclerView.Adapter<CheckinLocationAdapter.ViewHolder> {

    public interface Listener {
        void onCheckinClick(CheckinLocation location);

        void onNavigateClick(CheckinLocation location);
    }

    private final List<CheckinLocation> locations;
    private final CheckinManager checkinManager;
    private final Listener listener;

    public CheckinLocationAdapter(List<CheckinLocation> locations, CheckinManager checkinManager, Listener listener) {
        this.locations = locations;
        this.checkinManager = checkinManager;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_checkin_location, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final CheckinLocation location = locations.get(position);
        final boolean completed = checkinManager.isCompleted(location.getId());

        holder.name.setText(location.getName());
        holder.description.setText(location.getDescription());
        holder.tips.setText(location.getTips());
        holder.checkinButton.setText(completed
                ? holder.itemView.getContext().getString(R.string.checkin_completed)
                : holder.itemView.getContext().getString(R.string.checkin_go));
        holder.checkinButton.setSelected(completed);
        holder.checkinButton.setEnabled(true);

        holder.checkinButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onCheckinClick(location);
            }
        });
        holder.navigateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onNavigateClick(location);
            }
        });
    }

    @Override
    public int getItemCount() {
        return locations.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        final TextView name;
        final TextView description;
        final TextView tips;
        final TextView checkinButton;
        final TextView navigateButton;

        ViewHolder(View itemView) {
            super(itemView);
            name = (TextView) itemView.findViewById(R.id.textLocationName);
            description = (TextView) itemView.findViewById(R.id.textLocationDescription);
            tips = (TextView) itemView.findViewById(R.id.textLocationTips);
            checkinButton = (TextView) itemView.findViewById(R.id.buttonCheckin);
            navigateButton = (TextView) itemView.findViewById(R.id.buttonNavigate);
        }
    }
}
