package com.example.cn.helloworld.ui.admin;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.cn.helloworld.R;
import com.example.cn.helloworld.data.repository.AuthRepository;
import com.example.cn.helloworld.data.session.SessionManager;
import com.example.cn.helloworld.ui.auth.LoginActivity;

/**
 * 管理员控制台：融合版本
 * - 保留登录验证与退出功能
 * - 增加统计仪表盘展示
 */
public class AdminDashboardActivity extends AppCompatActivity {

    private SessionManager sessionManager;
    private TextView tvWelcome;
    private TextView tvToken;
    private TextView summaryText;
    private Button btnProducts;
    private Button btnPlaylists;
    private Button btnUsers;
    private Button btnLogout;
    private StatsBarView statsBarView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_dashboard);
        setTitle(R.string.title_admin_dashboard);

        sessionManager = new SessionManager(this);
        // 非管理员直接退出
        if (!AuthRepository.ROLE_ADMIN.equals(sessionManager.getRole())) {
            finish();
            return;
        }

        bindViews();
        populateInfo();
        setupDashboardStats();
        initEvents();
    }

    private void bindViews() {
        tvWelcome = findViewById(R.id.tvAdminWelcome);
        tvToken = findViewById(R.id.tvAdminToken);
        summaryText = findViewById(R.id.text_dashboard_summary);
        btnProducts = findViewById(R.id.btnManageProducts);
        btnPlaylists = findViewById(R.id.btnManagePlaylists);
        btnUsers = findViewById(R.id.btnViewUsers);
        btnLogout = findViewById(R.id.btnLogout);
        statsBarView = findViewById(R.id.stats_bar_view);
    }

    /** 显示当前登录信息 */
    private void populateInfo() {
        String username = sessionManager.getUsername();
        if (username == null || username.isEmpty()) {
            username = "管理员";
        }
        tvWelcome.setText("管理员控制台 - " + username);

        String token = sessionManager.getToken();
        if (token == null || token.isEmpty()) token = "未获取";
        String permissions = sessionManager.getPermissions();
        String tokenInfo = "Token: " + token;
        if (permissions != null && !permissions.isEmpty()) {
            tokenInfo += "\n权限: " + permissions;
        }
        tvToken.setText(tokenInfo);
    }

    /** 初始化仪表盘数据 */
    private void setupDashboardStats() {
        if (statsBarView == null) return;

        float[] values = new float[]{120f, 80f, 45f, 30f};
        String[] labels = new String[]{
                getString(R.string.stat_label_order),
                getString(R.string.stat_label_support),
                getString(R.string.stat_label_new_user),
                getString(R.string.stat_label_review)
        };
        statsBarView.setData(values, labels);

        if (summaryText != null) {
            summaryText.setText(getString(R.string.dashboard_summary,
                    (int) values[0], (int) values[1], (int) values[2]));
        }
    }

    /** 初始化按钮事件 */
    private void initEvents() {
        View.OnClickListener featureClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String message;
                if (v == btnProducts) {
                    message = "进入商品管理（演示）";
                } else if (v == btnPlaylists) {
                    message = "进入歌单管理（演示）";
                } else {
                    message = "进入用户管理（演示）";
                }
                Toast.makeText(AdminDashboardActivity.this, message, Toast.LENGTH_SHORT).show();
            }
        };

        btnProducts.setOnClickListener(featureClickListener);
        btnPlaylists.setOnClickListener(featureClickListener);
        btnUsers.setOnClickListener(featureClickListener);

        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confirmLogout();
            }
        });
    }

    /** 退出登录确认框 */
    private void confirmLogout() {
        new AlertDialog.Builder(this)
                .setTitle("退出登录")
                .setMessage("确定要退出管理员账号吗？")
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        sessionManager.clearSession();
                        Intent intent = new Intent(AdminDashboardActivity.this, LoginActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        finish();
                    }
                })
                .setNegativeButton(android.R.string.cancel, null)
                .show();
    }
}
