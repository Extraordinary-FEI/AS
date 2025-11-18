package com.example.cn.helloworld.ui.admin;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
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
    private FloatingActionButton fabAddTask;

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

        repository = SupportTaskRepository.getInstance(this);

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

            @Override
            public void onEdit(SupportTask task) {
                showTaskEditor(task);
            }

            @Override
            public void onDelete(SupportTask task) {
                confirmDelete(task);
            }
        });
        recyclerView.setAdapter(adapter);

        fabAddTask = (FloatingActionButton) findViewById(R.id.fabAddSupportTask);
        if (fabAddTask != null) {
            fabAddTask.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showTaskEditor(null);
                }
            });
        }

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

    private void showTaskEditor(@Nullable final SupportTask task) {
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_support_task_editor, null, false);
        final EditText titleInput = (EditText) view.findViewById(R.id.editSupportTaskTitle);
        final EditText descInput = (EditText) view.findViewById(R.id.editSupportTaskDescription);
        final EditText priorityInput = (EditText) view.findViewById(R.id.editSupportTaskPriority);
        final Spinner statusSpinner = (Spinner) view.findViewById(R.id.spinnerSupportTaskStatus);

        final String[] statusValues = new String[]{
                SupportTaskRepository.STATUS_PENDING,
                SupportTaskRepository.STATUS_APPROVED,
                SupportTaskRepository.STATUS_REJECTED
        };
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item,
                new String[]{
                        getString(R.string.support_task_status_pending),
                        getString(R.string.support_task_status_approved),
                        getString(R.string.support_task_status_rejected)
                });
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        statusSpinner.setAdapter(adapter);

        if (task != null) {
            titleInput.setText(task.getTitle());
            descInput.setText(task.getDescription());
            priorityInput.setText(String.valueOf(task.getPriority()));
            int index = indexOfStatus(task.getStatus(), statusValues);
            if (index >= 0) {
                statusSpinner.setSelection(index);
            }
        }

        final AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle(task == null ? R.string.support_task_dialog_title_create : R.string.support_task_dialog_title_edit)
                .setView(view)
                .setPositiveButton(android.R.string.ok, null)
                .setNegativeButton(android.R.string.cancel, null)
                .create();

        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(final DialogInterface dialogInterface) {
                Button button = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String title = titleInput.getText() == null ? "" : titleInput.getText().toString().trim();
                        String description = descInput.getText() == null ? "" : descInput.getText().toString().trim();
                        String priorityText = priorityInput.getText() == null ? "0" : priorityInput.getText().toString().trim();
                        if (TextUtils.isEmpty(title)) {
                            titleInput.setError(getString(R.string.support_task_error_title_required));
                            return;
                        }
                        if (TextUtils.isEmpty(description)) {
                            descInput.setError(getString(R.string.support_task_error_desc_required));
                            return;
                        }
                        int priority;
                        try {
                            priority = Integer.parseInt(priorityText);
                        } catch (NumberFormatException exception) {
                            priorityInput.setError(getString(R.string.support_task_error_priority));
                            return;
                        }

                        String status = statusValues[statusSpinner.getSelectedItemPosition()];
                        String admin = sessionManager.getUsername();
                        if (task == null) {
                            repository.createTask(title, description, priority, status, admin);
                        } else {
                            repository.updateTask(task.getTaskId(), title, description, status, priority, admin);
                        }
                        dialogInterface.dismiss();
                        loadTasks();
                    }
                });
            }
        });

        dialog.show();
    }

    private int indexOfStatus(String status, String[] statuses) {
        for (int i = 0; i < statuses.length; i++) {
            if (statuses[i].equals(status)) {
                return i;
            }
        }
        return 0;
    }

    private void confirmDelete(final SupportTask task) {
        if (task == null) {
            return;
        }
        new AlertDialog.Builder(this)
                .setTitle(R.string.support_task_dialog_title_delete)
                .setMessage(getString(R.string.support_task_dialog_message_delete, task.getTitle()))
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        repository.deleteTask(task.getTaskId());
                        loadTasks();
                    }
                })
                .setNegativeButton(android.R.string.cancel, null)
                .show();
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
            void onEdit(SupportTask task);
            void onDelete(SupportTask task);
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
            holder.priorityView.setText(holder.itemView.getContext()
                    .getString(R.string.support_task_priority_format, task.getPriority()));

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

            holder.editButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onEdit(task);
                }
            });

            holder.deleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onDelete(task);
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
            final TextView priorityView;
            final TextView assigneeView;
            final TextView timeView;
            final Button approveButton;
            final Button rejectButton;
            final Button editButton;
            final Button deleteButton;

            ViewHolder(View itemView) {
                super(itemView);
                titleView = (TextView) itemView.findViewById(R.id.textTaskTitle);
                descriptionView = (TextView) itemView.findViewById(R.id.textTaskDescription);
                statusView = (TextView) itemView.findViewById(R.id.textTaskStatus);
                priorityView = (TextView) itemView.findViewById(R.id.textTaskPriority);
                assigneeView = (TextView) itemView.findViewById(R.id.textTaskAssignee);
                timeView = (TextView) itemView.findViewById(R.id.textTaskTime);
                approveButton = (Button) itemView.findViewById(R.id.buttonApproveTask);
                rejectButton = (Button) itemView.findViewById(R.id.buttonRejectTask);
                editButton = (Button) itemView.findViewById(R.id.buttonEditTask);
                deleteButton = (Button) itemView.findViewById(R.id.buttonDeleteTask);
            }
        }
    }
}
