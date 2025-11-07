package com.example.cn.helloworld.ui.main;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.cn.helloworld.R;

import java.util.List;

/**
 * 应援任务列表适配器。
 */
public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.TaskViewHolder> {

    private final List<HomeModels.SupportTask> tasks;

    public TaskAdapter(List<HomeModels.SupportTask> tasks) {
        this.tasks = tasks;
    }

    @Override
    public TaskViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_task, parent, false);
        return new TaskViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(TaskViewHolder holder, int position) {
        HomeModels.SupportTask task = tasks.get(position);
        holder.nameView.setText(task.getName());
        holder.deadlineView.setText(task.getDeadline());
        holder.descriptionView.setText(task.getDescription());
    }

    @Override
    public int getItemCount() {
        return tasks == null ? 0 : tasks.size();
    }

    static class TaskViewHolder extends RecyclerView.ViewHolder {
        final TextView nameView;
        final TextView deadlineView;
        final TextView descriptionView;

        TaskViewHolder(View itemView) {
            super(itemView);
            nameView = (TextView) itemView.findViewById(R.id.task_name);
            deadlineView = (TextView) itemView.findViewById(R.id.task_deadline);
            descriptionView = (TextView) itemView.findViewById(R.id.task_description);
        }
    }
}
