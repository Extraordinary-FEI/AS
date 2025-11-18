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
import android.view.LayoutInflater;
import android.view.MenuItem;
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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class SupportTaskManagementActivity extends AppCompatActivity {

    private SessionManager sessionManager;
    private SupportTaskRepository repository;
    private TaskAdapter adapter;

    private static final String[] STATUS_VALUES = new String[]{
            SupportTaskRepository.STATUS_PENDING,
            SupportTaskRepository.STATUS_APPROVED,
            SupportTaskRepository.STATUS_REJECTED
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_support_task_management);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(R.string.title_admin_manage_support_tasks);
        }

        sessionManager = new SessionManager(this);
        if (!sessionManager.isAdmin()) {
            Toast.makeText(this, R.string.admin_access_denied, Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        repository = new SupportTaskRepository(this);

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recyclerSupportTasks);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new TaskAdapter(new ArrayList<SupportTask>());
        adapter.setListener(new TaskAdapter.Listener() {
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

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fabAddSupportTask);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTaskEditor(null);
            }
        });

        loadTasks();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
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

        ArrayAdapter<String> statusAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item,
                getStatusLabels());
        statusAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        statusSpinner.setAdapter(statusAdapter);

        if (task != null) {
            titleInput.setText(task.getTitle());
            descInput.setText(task.getDescription());
            priorityInput.setText(String.valueOf(task.getPriority()));
            statusSpinner.setSelection(findStatusIndex(task.getStatus()));
        } else {
            statusSpinner.setSelection(0);
        }

        final AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle(task == null ? R.string.dialog_title_add_support_task : R.string.dialog_title_edit_support_task)
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
                        String title = titleInput.getText().toString().trim();
                        String description = descInput.getText().toString().trim();
                        String priorityText = priorityInput.getText().toString().trim();

                        if (TextUtils.isEmpty(title)) {
                            titleInput.setError(getString(R.string.error_support_task_title_required));
                            return;
                        }
                        if (TextUtils.isEmpty(description)) {
                            descInput.setError(getString(R.string.error_support_task_description_required));
                            return;
                        }

                        int priority;
                        try {
                            priority = TextUtils.isEmpty(priorityText) ? 0 : Integer.parseInt(priorityText);
                        } catch (NumberFormatException e) {
                            priorityInput.setError(getString(R.string.error_support_task_priority_invalid));
                            return;
                        }

                        String status = STATUS_VALUES[statusSpinner.getSelectedItemPosition()];
                        String admin = sessionManager.getUsername();
                        if (TextUtils.isEmpty(admin)) {
                            admin = task != null ? task.getAssignedAdmin() : null;
                        }

                        long createdAt = task == null ? System.currentTimeMillis() : task.getCreatedAt();
                        SupportTask newTask = new SupportTask(
                                task == null ? repository.generateTaskId() : task.getTaskId(),
                                title,
                                description,
                                status,
                                admin,
                                createdAt,
                                System.currentTimeMillis(),
                                priority
                        );
                        if (task == null) {
                            repository.createTask(newTask);
                        } else {
                            repository.updateTask(newTask);
                        }
                        loadTasks();
                        dialogInterface.dismiss();
                    }
                });
            }
        });

        dialog.show();
    }

    private void confirmDelete(final SupportTask task) {
        new AlertDialog.Builder(this)
                .setTitle(R.string.dialog_title_delete_support_task)
                .setMessage(getString(R.string.dialog_message_delete_support_task, task.getTitle()))
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        repository.deleteTask(task.getTaskId());
                        loadTasks();
                    }
                })
                .setNegativeButton(android.R.string.cancel, null)
                .show();
    }


    private String[] getStatusLabels() {
        return new String[]{

                getString(R.string.support_task_status_pending),
                getString(R.string.support_task_status_approved),
                getString(R.string.support_task_status_rejected)
        };
    }

    private int findStatusIndex(String status) {
        for (int i = 0; i < STATUS_VALUES.length; i++) {
            if (STATUS_VALUES[i].equals(status)) {
                return i;
            }
        }
        return 0;
    }

    private static class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.ViewHolder> {

        interface Listener {
            void onEdit(SupportTask task);
            void onDelete(SupportTask task);
        }

        private final List<SupportTask> tasks;
        private Listener listener;
        private final SimpleDateFormat format = new SimpleDateFormat("MM-dd HH:mm", Locale.getDefault());

        TaskAdapter(List<SupportTask> tasks) {
            this.tasks = tasks;
        }

        void setListener(Listener listener) {
            this.listener = listener;
        }

        void submit(List<SupportTask> list) {
            tasks.clear();
            if (list != null) {
                tasks.addAll(list);
            }
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
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            final SupportTask task = tasks.get(position);
            holder.bind(task, format, listener);
        }

        @Override
        public int getItemCount() {
            return tasks.size();
        }

        static class ViewHolder extends RecyclerView.ViewHolder {

            private final TextView titleView;
            private final TextView descriptionView;
            private final TextView statusView;
            private final TextView assigneeView;
            private final TextView priorityView;
            private final TextView timeView;
            private final Button editButton;
            private final Button deleteButton;
            private final View manageButtons;
            private final View approvalButtons;

            ViewHolder(View itemView) {
                super(itemView);
                titleView = (TextView) itemView.findViewById(R.id.textTaskTitle);
                descriptionView = (TextView) itemView.findViewById(R.id.textTaskDescription);
                statusView = (TextView) itemView.findViewById(R.id.textTaskStatus);
                assigneeView = (TextView) itemView.findViewById(R.id.textTaskAssignee);
                priorityView = (TextView) itemView.findViewById(R.id.textTaskPriority);
                timeView = (TextView) itemView.findViewById(R.id.textTaskTime);
                editButton = (Button) itemView.findViewById(R.id.buttonEditTask);
                deleteButton = (Button) itemView.findViewById(R.id.buttonDeleteTask);
                manageButtons = itemView.findViewById(R.id.groupManageButtons);
                approvalButtons = itemView.findViewById(R.id.groupApprovalButtons);
            }

            void bind(final SupportTask task,
                      SimpleDateFormat format,
                      final Listener listener) {
                Context context = itemView.getContext();
                titleView.setText(task.getTitle());
                descriptionView.setText(task.getDescription());
                statusView.setText(context.getString(R.string.support_task_status_format,
                        SupportTaskApprovalActivity.formatStatus(context, task.getStatus())));
                if (TextUtils.isEmpty(task.getAssignedAdmin())) {
                    assigneeView.setText(R.string.support_task_unassigned);
                } else {
                    assigneeView.setText(context.getString(R.string.support_task_assigned_format, task.getAssignedAdmin()));
                }
                priorityView.setText(context.getString(R.string.support_task_priority_format, task.getPriority()));
                timeView.setText(context.getString(R.string.support_task_updated_time_format,
                        format.format(new Date(task.getUpdatedAt()))));

                if (manageButtons != null) {
                    manageButtons.setVisibility(View.VISIBLE);
                }
                if (approvalButtons != null) {
                    approvalButtons.setVisibility(View.GONE);
                }

                editButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (listener != null) {
                            listener.onEdit(task);
                        }
                    }
                });

                deleteButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (listener != null) {
                            listener.onDelete(task);
                        }
                    }
                });
            }
        }
    }
}
