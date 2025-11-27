package com.example.cn.helloworld.ui.user;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.cn.helloworld.R;

import java.util.List;

class QuickActionAdapter extends RecyclerView.Adapter<QuickActionAdapter.ViewHolder> {

    interface Listener {
        void onActionClick(QuickAction action);
        void onActionEdit(QuickAction action);
    }

    private final List<QuickAction> actions;
    private final Listener listener;

    QuickActionAdapter(List<QuickAction> actions, Listener listener) {
        this.actions = actions;
        this.listener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_quick_action, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final QuickAction action = actions.get(position);
        holder.title.setText(action.getTitle());
        holder.description.setText(action.getDescription());
        holder.icon.setImageResource(action.getIconRes());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onActionClick(action);
                }
            }
        });

        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (listener != null) {
                    listener.onActionEdit(action);
                    return true;
                }
                return false;
            }
        });
    }

    @Override
    public int getItemCount() {
        return actions.size();
    }

    void updateAction(QuickAction updated) {
        for (int i = 0; i < actions.size(); i++) {
            QuickAction action = actions.get(i);
            if (action.getId().equals(updated.getId())) {
                actions.set(i, updated);
                notifyItemChanged(i);
                break;
            }
        }
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        final ImageView icon;
        final TextView title;
        final TextView description;

        ViewHolder(View itemView) {
            super(itemView);
            icon = (ImageView) itemView.findViewById(R.id.quickActionIcon);
            title = (TextView) itemView.findViewById(R.id.quickActionTitle);
            description = (TextView) itemView.findViewById(R.id.quickActionDescription);
        }
    }
}
