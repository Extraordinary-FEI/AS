package com.example.cn.helloworld.ui.main;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.cn.helloworld.R;

/**
 * 任务详情页，可查看任务信息并执行报名/签到操作。
 */
public class SupportTaskDetailActivity extends AppCompatActivity {

    public static final String EXTRA_SUPPORT_TASK = "extra_support_task";

    private HomeModels.SupportTask supportTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_support_task_detail);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        Object extra = getIntent().getSerializableExtra(EXTRA_SUPPORT_TASK);
        if (!(extra instanceof HomeModels.SupportTask)) {
            Toast.makeText(this, R.string.task_detail_invalid, Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        supportTask = (HomeModels.SupportTask) extra;

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(supportTask.getName());
        }

        bindTask();
    }

    private void bindTask() {
        TextView statusView = (TextView) findViewById(R.id.task_status);
        TextView timeView = (TextView) findViewById(R.id.task_time);
        TextView locationView = (TextView) findViewById(R.id.task_location);
        TextView typeView = (TextView) findViewById(R.id.task_type);
        TextView statusLabel = (TextView) findViewById(R.id.task_status_label);
        TextView descriptionView = (TextView) findViewById(R.id.task_description);
        TextView guideView = (TextView) findViewById(R.id.task_guide);
        TextView contactView = (TextView) findViewById(R.id.task_contact);
        TextView progressView = (TextView) findViewById(R.id.task_progress);
        TextView progressNoteView = (TextView) findViewById(R.id.task_progress_note);
        ProgressBar progressBar = (ProgressBar) findViewById(R.id.task_progress_bar);
        Button actionButton = (Button) findViewById(R.id.task_action);

        statusView.setText(getStatusText(supportTask.getStatus()));
        statusView.setBackgroundResource(getStatusBackground(supportTask.getStatus()));

        timeView.setText(getString(R.string.task_detail_time_format, supportTask.getTimeRange()));
        locationView.setText(getString(R.string.task_detail_location_format, supportTask.getLocation()));
        typeView.setText(getString(R.string.task_detail_type_format, supportTask.getTaskType()));
        statusLabel.setText(getString(R.string.task_detail_status_format,
                getRegistrationText(supportTask.getRegistrationStatus())));

        descriptionView.setText(supportTask.getDescription());
        guideView.setText(supportTask.getGuide());
        contactView.setText(supportTask.getContact());

        progressBar.setMax(supportTask.getMaxParticipants());
        progressBar.setProgress(Math.min(supportTask.getEnrolledCount(), supportTask.getMaxParticipants()));
        progressView.setText(getString(R.string.task_capacity_format,
                supportTask.getEnrolledCount(), supportTask.getMaxParticipants()));

        String note = supportTask.getProgressNote();
        if (note == null || note.trim().isEmpty()) {
            progressNoteView.setVisibility(View.GONE);
        } else {
            progressNoteView.setVisibility(View.VISIBLE);
            progressNoteView.setText(note);
        }

        configureActionButton(actionButton);
    }

    private void configureActionButton(Button actionButton) {
        String actionText;
        boolean enabled = true;

        if (supportTask.getStatus() == HomeModels.SupportTask.TaskStatus.COMPLETED) {
            actionText = getString(R.string.task_detail_completed);
            enabled = false;
        } else {
            switch (supportTask.getRegistrationStatus()) {
                case OPEN:
                    actionText = getString(R.string.task_detail_enroll);
                    break;
                case CHECK_IN:
                    actionText = getString(R.string.task_detail_check_in);
                    break;
                case FULL:
                    actionText = getString(R.string.task_detail_full);
                    enabled = false;
                    break;
                case CLOSED:
                    actionText = getString(R.string.task_detail_completed);
                    enabled = false;
                    break;
                case NOT_OPEN:
                default:
                    actionText = getString(R.string.task_detail_not_open);
                    enabled = false;
                    break;
            }
        }

        actionButton.setText(actionText);
        actionButton.setEnabled(enabled);

        if (enabled) {
            final String feedback = getString(R.string.task_detail_action_feedback, actionText);
            actionButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(SupportTaskDetailActivity.this, feedback, Toast.LENGTH_SHORT)
                            .show();
                }
            });
        } else {
            actionButton.setOnClickListener(null);
        }
    }

    private String getStatusText(HomeModels.SupportTask.TaskStatus status) {
        switch (status) {
            case ONGOING:
                return getString(R.string.task_status_ongoing);
            case COMPLETED:
                return getString(R.string.task_status_completed);
            case UPCOMING:
            default:
                return getString(R.string.task_status_upcoming);
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

    private String getRegistrationText(HomeModels.SupportTask.RegistrationStatus status) {
        switch (status) {
            case OPEN:
                return getString(R.string.task_registration_open);
            case FULL:
                return getString(R.string.task_registration_full);
            case CHECK_IN:
                return getString(R.string.task_registration_check_in);
            case CLOSED:
                return getString(R.string.task_registration_closed);
            case NOT_OPEN:
            default:
                return getString(R.string.task_registration_not_open);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
