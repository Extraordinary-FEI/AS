package com.example.cn.helloworld.ui.main;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.cn.helloworld.R;
import com.example.cn.helloworld.data.repository.FavoriteRepository;
import com.example.cn.helloworld.data.repository.SupportTaskRepository;
import com.example.cn.helloworld.data.repository.SupportTaskEnrollmentRepository;
import com.example.cn.helloworld.data.session.SessionManager;
import com.example.cn.helloworld.data.model.SupportTask;

public class SupportTaskDetailActivity extends AppCompatActivity {

    public static final String EXTRA_SUPPORT_TASK = "extra_support_task";

    private HomeModels.SupportTask supportTask;
    private SupportTaskEnrollmentRepository enrollmentRepository;
    private SessionManager sessionManager;
    private SupportTaskEnrollmentRepository.EnrollmentStatus enrollmentStatus;

    // ⭐ 新增：收藏功能变量
    private FavoriteRepository favoriteRepository;
    private boolean isTaskFavored = false;
    private String taskId = "";

    private ImageView btnFavorite;
    private TextView textFavorite;
    private TextView enrollmentStatusView;
    private TextView enrollmentHintView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_support_task_detail);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Object extra = getIntent().getSerializableExtra(EXTRA_SUPPORT_TASK);
        if (!(extra instanceof HomeModels.SupportTask)) {
            Toast.makeText(this, R.string.task_detail_invalid, Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        supportTask = (HomeModels.SupportTask) extra;
        sessionManager = new SessionManager(this);
        enrollmentRepository = new SupportTaskEnrollmentRepository(this);

        if (getSupportActionBar() != null)
            getSupportActionBar().setTitle(supportTask.getName());

        // ⭐ 初始化收藏
        initializeFavoriteFeature();

        refreshEnrollmentStatus();

        bindTask();
    }

    /** --------------------------------------
     * ⭐ 收藏功能初始化
     * -------------------------------------- */
    private void initializeFavoriteFeature() {
        favoriteRepository = new FavoriteRepository(this);

        taskId = supportTask.getId(); // 任务唯一 ID
        btnFavorite = (ImageView) findViewById(R.id.btn_favorite_task);
        textFavorite = (TextView) findViewById(R.id.text_favorite_task);

        isTaskFavored = favoriteRepository.isTaskFavorite(taskId);

        updateFavoriteUI();

        findViewById(R.id.layout_favorite_task).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleFavorite();
            }
        });
    }

    private void refreshEnrollmentStatus() {
        enrollmentStatus = enrollmentRepository.getEnrollmentStatus(getCurrentUserId(), supportTask.getId());
    }

    private String getCurrentUserId() {
        return sessionManager == null ? "" : sessionManager.getUserId();
    }

    /** 点击收藏 / 取消收藏 */
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    private void toggleFavorite() {
        boolean newState = !favoriteRepository.isTaskFavorite(taskId);
        favoriteRepository.setTaskFavorite(taskId, newState);
        isTaskFavored = newState;

        updateFavoriteUI();
        playFavoriteAnimation(btnFavorite);
    }

    /** 收藏 UI 切换 */
    private void updateFavoriteUI() {
        if (isTaskFavored) {
            btnFavorite.setImageResource(R.drawable.ic_heart_filled_red);
            textFavorite.setText("已收藏");
        } else {
            btnFavorite.setImageResource(R.drawable.ic_heart_outline_gray);
            textFavorite.setText("收藏任务");
        }
    }

    /** 收藏动画效果 */
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    private void playFavoriteAnimation(final ImageView view) {
        view.setScaleX(0.85f);
        view.setScaleY(0.85f);

        view.animate()
                .scaleX(1.2f)
                .scaleY(1.2f)
                .setDuration(150)
                .withEndAction(new Runnable() {
                    @Override
                    public void run() {
                        view.animate()
                                .scaleX(1f)
                                .scaleY(1f)
                                .setDuration(120)
                                .start();
                    }
                })
                .start();
    }

    // ----------------------------------------
    // 下面是你原有的任务绑定逻辑（保持不动）
    // ----------------------------------------

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
        enrollmentStatusView = (TextView) findViewById(R.id.task_user_enrollment_status);
        enrollmentHintView = (TextView) findViewById(R.id.task_user_enrollment_hint);
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
        if (note == null || note.trim().isEmpty())
            progressNoteView.setVisibility(View.GONE);
        else {
            progressNoteView.setVisibility(View.VISIBLE);
            progressNoteView.setText(note);
        }

        updateEnrollmentViews();
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
                case FULL:
                    actionText = getString(R.string.task_detail_full);
                    enabled = false;
                    break;
                case CLOSED:
                    actionText = getString(R.string.task_detail_completed);
                    enabled = false;
                    break;
                case NOT_OPEN:
                    actionText = getString(R.string.task_detail_not_open);
                    enabled = false;
                    break;
                default:
                    refreshEnrollmentStatus();
                    switch (enrollmentStatus) {
                        case APPROVED:
                            actionText = getString(R.string.task_enrollment_status_approved);
                            enabled = false;
                            break;
                        case REJECTED:
                            actionText = getString(R.string.task_enrollment_action_reapply);
                            enabled = true;
                            break;
                        case PENDING:
                            actionText = getString(R.string.task_enrollment_status_pending);
                            enabled = false;
                            break;
                        case NOT_APPLIED:
                        default:
                            if (supportTask.getRegistrationStatus() == HomeModels.SupportTask.RegistrationStatus.CHECK_IN) {
                                actionText = getString(R.string.task_detail_check_in);
                            } else {
                                actionText = getString(R.string.task_detail_enroll);
                            }
                            break;
                    }
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
                        enrollmentRepository.markApplied(getCurrentUserId(), supportTask.getId());
                        pushToAdminApprovalQueue();
                        refreshEnrollmentStatus();
                        updateEnrollmentViews();
                        Toast.makeText(SupportTaskDetailActivity.this, feedback, Toast.LENGTH_SHORT)
                                .show();
                        configureActionButton((Button) v);
                }
            });
        } else {
            actionButton.setOnClickListener(null);
        }
    }

    private void updateEnrollmentViews() {
        if (enrollmentStatusView == null) {
            return;
        }
        refreshEnrollmentStatus();
        String statusText;
        int backgroundRes;
        switch (enrollmentStatus) {
            case APPROVED:
                statusText = getString(R.string.task_enrollment_status_approved);
                backgroundRes = R.drawable.bg_task_status_ongoing;
                break;
            case REJECTED:
                statusText = getString(R.string.task_enrollment_status_rejected);
                backgroundRes = R.drawable.bg_task_status_completed;
                break;
            case PENDING:
                statusText = getString(R.string.task_enrollment_status_pending);
                backgroundRes = R.drawable.bg_task_status_upcoming;
                break;
            case NOT_APPLIED:
            default:
                statusText = getString(R.string.task_enrollment_status_not_applied);
                backgroundRes = R.drawable.bg_task_status_upcoming;
                break;
        }
        enrollmentStatusView.setText(statusText);
        enrollmentStatusView.setBackgroundResource(backgroundRes);
        if (enrollmentHintView != null) {
            enrollmentHintView.setText(R.string.task_enrollment_hint);
        }
    }

    /**
     * 将粉丝报名同步到管理员审批列表，便于后台看到新的待审核任务。
     */
    private void pushToAdminApprovalQueue() {
        SupportTaskRepository adminRepository = new SupportTaskRepository(this);
        String applicantId = getCurrentUserId();
        String applicantName = sessionManager.getUsername();
        String adminTaskId = supportTask.getId() + "_" + applicantId;

        String description = getString(R.string.task_enrollment_admin_description,
                applicantName, supportTask.getName(), supportTask.getTaskType());

        SupportTask existing = adminRepository.getTaskById(adminTaskId);
        if (existing == null) {
            SupportTask newTask = new SupportTask(adminTaskId, supportTask.getName(), description,
                    SupportTaskRepository.STATUS_PENDING, null, System.currentTimeMillis(),
                    System.currentTimeMillis(), 1);
            adminRepository.createTask(newTask);
        } else {
            existing.setTitle(supportTask.getName());
            existing.setDescription(description);
            existing.setStatus(SupportTaskRepository.STATUS_PENDING);
            existing.setAssignedAdmin(null);
            existing.setUpdatedAt(System.currentTimeMillis());
            adminRepository.updateTask(existing);
        }
    }

    private String getStatusText(HomeModels.SupportTask.TaskStatus status) {
        switch (status) {
            case ONGOING:
                return getString(R.string.task_status_ongoing);
            case COMPLETED:
                return getString(R.string.task_status_completed);
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
            default:
                return getString(R.string.task_registration_not_open);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
