package com.example.cn.helloworld.ui.admin;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.cn.helloworld.R;
import com.example.cn.helloworld.data.model.SupportTask;
import com.example.cn.helloworld.data.repository.SupportTaskRepository;
import com.example.cn.helloworld.data.session.SessionManager;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class SupportTaskApprovalActivity extends AppCompatActivity {

    private SessionManager sessionManager;
    private SupportTaskRepository repository;
    private TaskAdapter adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_support_tasks);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(R.string.title_admin_support_tasks);
        }

        sessionManager = new SessionManager(this);
        if (!sessionManager.isAdmin()) {
            Toast.makeText(this, R.string.admin_access_denied, Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        repository = new SupportTaskRepository();

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recyclerSupportTasks);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new TaskAdapter(new ArrayList<SupportTask>(), new TaskAdapter.Listener() {
            @Override
            public void onApprove(SupportTask task) {
                repository.approveTask(task.getTaskId(), sessionManager.getUsername());
                loadTasks();
            }

            @Override
            public void onReject(SupportTask task) {
                repository.rejectTask(task.getTaskId(), sessionManager.getUsername());
                loadTasks();
            }
        });
        recyclerView.setAdapter(adapter);

        loadTasks();
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    private void loadTasks() {
        adapter.submit(repository.getAll());
    }

    /** 让 TaskAdapter 能正常调用的格式化函数 —— 设为 static */
    public static String formatStatus(Context ctx, String status) {
        if (SupportTaskRepository.STATUS_APPROVED.equals(status)) {
            return ctx.getString(R.string.support_task_status_approved);
        } else if (SupportTaskRepository.STATUS_REJECTED.equals(status)) {
            return ctx.getString(R.string.support_task_status_rejected);
        }
        return ctx.getString(R.string.support_task_status_pending);
    }

    /** 完全修复后的 Adapter */
    private static class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.ViewHolder> {

        /** 把 Listener 放在这里不会报错 —— 因为不再是 static interface */
        interface Listener {
            void onApprove(SupportTask task);
            void onReject(SupportTask task);
        }

        private final List<SupportTask> tasks;
        private final Listener listener;

        TaskAdapter(List<SupportTask> tasks, Listener listener) {
            this.tasks = tasks;
            this.listener = listener;
        }

        void submit(List<SupportTask> list) {
            tasks.clear();
            if (list != null) tasks.addAll(list);
            notifyDataSetChanged();
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_admin_support_task, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
            final SupportTask task = tasks.get(position);

            holder.titleView.setText(task.getTitle());
            holder.descriptionView.setText(task.getDescription());

            // ★ 使用新的 static 方法格式化状态
            holder.statusView.setText(
                    holder.itemView.getContext().getString(
                            R.string.support_task_status_format,
                            SupportTaskApprovalActivity.formatStatus(holder.itemView.getContext(), task.getStatus())
                    )
            );

            holder.timeView.setText(DateFormat.format("MM-dd HH:mm", new Date(task.getUpdatedAt())));

            boolean isPending = SupportTaskRepository.STATUS_PENDING.equals(task.getStatus());
            holder.approveButton.setEnabled(isPending);
            holder.rejectButton.setEnabled(isPending);

            holder.assigneeView.setText(task.getAssignedAdmin() == null
                    ? holder.itemView.getContext().getString(R.string.support_task_unassigned)
                    : holder.itemView.getContext().getString(R.string.support_task_assigned_format, task.getAssignedAdmin())
            );

            holder.approveButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onApprove(task);
                }
            });

            holder.rejectButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onReject(task);
                }
            });
        }

        @Override
        public int getItemCount() {
            return tasks.size();
        }

        static class ViewHolder extends RecyclerView.ViewHolder {

            final TextView titleView;
            final TextView descriptionView;
            final TextView statusView;
            final TextView assigneeView;
            final TextView timeView;
            final Button approveButton;
            final Button rejectButton;

            ViewHolder(View itemView) {
                super(itemView);
                titleView = (TextView) itemView.findViewById(R.id.textTaskTitle);
                descriptionView = (TextView) itemView.findViewById(R.id.textTaskDescription);
                statusView = (TextView) itemView.findViewById(R.id.textTaskStatus);
                assigneeView = (TextView) itemView.findViewById(R.id.textTaskAssignee);
                timeView = (TextView) itemView.findViewById(R.id.textTaskTime);
                approveButton = (Button) itemView.findViewById(R.id.buttonApproveTask);
                rejectButton = (Button) itemView.findViewById(R.id.buttonRejectTask);
            }
        }
    }
}
