package com.example.cn.helloworld.ui.main;


import android.content.Context;
import android.content.Intent;
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
        final HomeModels.SupportTask task = tasks.get(position);
        Context context = holder.itemView.getContext();

        holder.nameView.setText(task.getName());
        holder.statusView.setText(getStatusText(context, task.getStatus()));
        holder.statusView.setBackgroundResource(getStatusBackground(task.getStatus()));
        holder.typeView.setText(context.getString(R.string.task_type_format, task.getTaskType()));
        holder.timeView.setText(context.getString(R.string.task_time_format, task.getTimeRange()));
        holder.capacityView.setText(context.getString(R.string.task_capacity_format,
                task.getEnrolledCount(), task.getMaxParticipants()));
        holder.registrationView.setText(buildRegistrationLine(context, task));
        holder.descriptionView.setText(task.getDescription());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), SupportTaskDetailActivity.class);
                intent.putExtra(SupportTaskDetailActivity.EXTRA_SUPPORT_TASK, task);
                v.getContext().startActivity(intent);
            }
        });
    }

    private String buildRegistrationLine(Context context, HomeModels.SupportTask task) {
        String base = getRegistrationText(context, task.getRegistrationStatus());
        HomeModels.SupportTask.EnrollmentState enrollment = task.getEnrollmentState();
        if (enrollment == HomeModels.SupportTask.EnrollmentState.NOT_APPLIED) {
            return base;
        }
        return base + " · " + getEnrollmentText(context, enrollment);
    }

    @Override
    public int getItemCount() {
        return tasks == null ? 0 : tasks.size();
    }

    static class TaskViewHolder extends RecyclerView.ViewHolder {
        final TextView nameView;
        final TextView statusView;
        final TextView typeView;
        final TextView timeView;
        final TextView capacityView;
        final TextView registrationView;
        final TextView descriptionView;

        TaskViewHolder(View itemView) {
            super(itemView);

            nameView = (TextView) itemView.findViewById(R.id.task_name);
            statusView = (TextView) itemView.findViewById(R.id.task_status);
            typeView = (TextView) itemView.findViewById(R.id.task_type);
            timeView = (TextView) itemView.findViewById(R.id.task_time);
            capacityView = (TextView) itemView.findViewById(R.id.task_capacity);
            registrationView = (TextView) itemView.findViewById(R.id.task_registration_status);
            descriptionView = (TextView) itemView.findViewById(R.id.task_description);
        }
    }

    private String getStatusText(Context context, HomeModels.SupportTask.TaskStatus status) {
        switch (status) {
            case ONGOING:
                return context.getString(R.string.task_status_ongoing);
            case COMPLETED:
                return context.getString(R.string.task_status_completed);
            case UPCOMING:
            default:
                return context.getString(R.string.task_status_upcoming);
        }
    }

    private int getStatusBackground(HomeModels.SupportTask.TaskStatus status) {
        switch (status) {
            case ONGOING:
                return R.drawable.bg_task_status_ongoing;
            case COMPLETED:
                return R.drawable.bg_task_status_completed;
            case UPCOMING:
            default:
                return R.drawable.bg_task_status_upcoming;
        }
    }

    private String getRegistrationText(Context context, HomeModels.SupportTask.RegistrationStatus status) {
        switch (status) {
            case OPEN:
                return context.getString(R.string.task_registration_open);
            case FULL:
                return context.getString(R.string.task_registration_full);
            case CHECK_IN:
                return context.getString(R.string.task_registration_check_in);
            case CLOSED:
                return context.getString(R.string.task_registration_closed);
            case NOT_OPEN:
            default:
                return context.getString(R.string.task_registration_not_open);
        }
    }

    private String getEnrollmentText(Context context, HomeModels.SupportTask.EnrollmentState state) {
        switch (state) {
            case APPROVED:
                return context.getString(R.string.task_enrollment_status_approved);
            case REJECTED:
                return context.getString(R.string.task_enrollment_status_rejected);
            case PENDING:
                return context.getString(R.string.task_enrollment_status_pending);
            case NOT_APPLIED:
            default:
                return context.getString(R.string.task_enrollment_status_not_applied);
        }
    }
}
