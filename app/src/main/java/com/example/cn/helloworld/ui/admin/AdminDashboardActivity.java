package com.example.cn.helloworld.ui.admin;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.cn.helloworld.R;
import com.example.cn.helloworld.data.model.AdminMetrics;
import com.example.cn.helloworld.data.model.Order;
import com.example.cn.helloworld.data.model.Permission;
import com.example.cn.helloworld.data.repository.AdminOrderRepository;
import com.example.cn.helloworld.data.repository.AdminMetricsRepository;
import com.example.cn.helloworld.data.repository.SupportTaskRepository;
import com.example.cn.helloworld.data.session.SessionManager;
import com.example.cn.helloworld.ui.auth.LoginActivity;

import java.util.ArrayList;
import java.util.List;


/**
 * 管理员控制台：融合版本
 * - 保留登录验证与退出功能
 * - 增加统计仪表盘展示
 */
public class AdminDashboardActivity extends AppCompatActivity {

    private SessionManager sessionManager;
    private AdminMetricsRepository metricsRepository;
    private SupportTaskRepository supportTaskRepository;
    private AdminOrderRepository orderRepository;
    private TextView tvWelcome;
    private TextView tvToken;
    private TextView summaryText;
    private Button btnProducts;
    private Button btnPlaylists;
    private Button btnViewOrders;
    private Button btnManageSupportTasks;
    private Button btnTaskApproval;
    private Button btnLogout;
    private StatsBarView statsBarView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_dashboard);
        setTitle(R.string.title_admin_dashboard);

        sessionManager = new SessionManager(this);
        if (!sessionManager.isAdmin()) {
            Toast.makeText(this, R.string.admin_access_denied, Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        supportTaskRepository = new SupportTaskRepository(this);
        orderRepository = new AdminOrderRepository(this);
        metricsRepository = new AdminMetricsRepository(supportTaskRepository, orderRepository);

        bindViews();
        populateInfo();
        setupDashboardStats();
        initEvents();
    }

    private void bindViews() {
        tvWelcome = (TextView) findViewById(R.id.tvAdminWelcome);
        tvToken = (TextView) findViewById(R.id.tvAdminToken);
        summaryText = (TextView) findViewById(R.id.text_dashboard_summary);
        btnProducts = (Button) findViewById(R.id.btnManageProducts);
        btnPlaylists = (Button) findViewById(R.id.btnManagePlaylists);
        btnViewOrders = (Button) findViewById(R.id.btnViewOrders);
        btnManageSupportTasks = (Button) findViewById(R.id.btnManageSupportTasks);
        btnTaskApproval = (Button) findViewById(R.id.btnTaskApproval);
        btnLogout = (Button) findViewById(R.id.btnLogout);
        statsBarView = (StatsBarView) findViewById(R.id.stats_bar_view);
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
        String tokenInfo = "Token: " + token;
        String permissionsText = buildPermissionSummary();
        if (!permissionsText.isEmpty()) {
            tokenInfo += "\n权限: " + permissionsText;
        }
        tvToken.setText(tokenInfo);
    }

    /** 初始化仪表盘数据 */
    private void setupDashboardStats() {
        if (statsBarView == null) return;
        AdminMetrics metrics = metricsRepository.loadMetrics();
        float[] values = new float[]{
                metrics.getOrderCount(),
                metrics.getPendingTasks(),
                metrics.getNewRegistrations(),
                metrics.getActiveUsers()
        };
        String[] labels = new String[]{
                getString(R.string.stat_label_order),
                getString(R.string.stat_label_support),
                getString(R.string.stat_label_new_user),
                getString(R.string.stat_label_active)
        };
        statsBarView.setData(values, labels);

        if (summaryText != null) {
            summaryText.setText(getString(R.string.dashboard_summary,
                    metrics.getOrderCount(),
                    metrics.getPendingTasks(),
                    metrics.getNewRegistrations()));
        }
    }

    /** 初始化按钮事件 */
    private void initEvents() {
        btnProducts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(AdminDashboardActivity.this, ProductManagementActivity.class));
            }
        });
        btnPlaylists.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(AdminDashboardActivity.this, PlaylistManagementActivity.class));
            }
        });
        btnViewOrders.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openOrderList();
            }
        });

        btnManageSupportTasks.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(AdminDashboardActivity.this, SupportTaskManagementActivity.class));
            }
        });

        btnTaskApproval.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(AdminDashboardActivity.this, SupportTaskApprovalActivity.class));
            }
        });

        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confirmLogout();
            }
        });
    }

    private String buildPermissionSummary() {
        StringBuilder builder = new StringBuilder();

        for (String permission : sessionManager.getPermissions()) {
            if (builder.length() > 0) {
                builder.append(',');
            }
            builder.append(permission);   // permission 是 String，而不是 Permission.name()
        }

        return builder.toString();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (sessionManager != null && sessionManager.isAdmin()) {
            setupDashboardStats();
        }
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

    private void openOrderList() {
        List<Order> orders = metricsRepository.getOrders();
        ArrayList<Order> extras = new ArrayList<Order>(orders);
        Intent intent = AdminOrderListActivity.createIntent(this, extras);
        startActivity(intent);
    }
}
