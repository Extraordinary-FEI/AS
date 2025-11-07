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
import com.example.cn.helloworld.data.repository.AuthRepository;
import com.example.cn.helloworld.data.session.SessionManager;
import com.example.cn.helloworld.ui.auth.LoginActivity;

public class AdminDashboardActivity extends AppCompatActivity {

    private SessionManager sessionManager;
    private TextView tvWelcome;
    private TextView tvToken;
    private Button btnProducts;
    private Button btnPlaylists;
    private Button btnUsers;
    private Button btnLogout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_dashboard);

        sessionManager = new SessionManager(this);
        if (!AuthRepository.ROLE_ADMIN.equals(sessionManager.getRole())) {
            finish();
            return;
        }
        bindViews();
        populateInfo();
        initEvents();
    }

    private void bindViews() {
        tvWelcome = (TextView) findViewById(R.id.tvAdminWelcome);
        tvToken = (TextView) findViewById(R.id.tvAdminToken);
        btnProducts = (Button) findViewById(R.id.btnManageProducts);
        btnPlaylists = (Button) findViewById(R.id.btnManagePlaylists);
        btnUsers = (Button) findViewById(R.id.btnViewUsers);
        btnLogout = (Button) findViewById(R.id.btnLogout);
    }

    private void populateInfo() {
        String username = sessionManager.getUsername();
        if (username == null || username.length() == 0) {
            username = "管理员";
        }
        String permissions = sessionManager.getPermissions();
        tvWelcome.setText("管理员控制台 - " + username);
        String token = sessionManager.getToken();
        if (token == null || token.length() == 0) {
            token = "未获取";
        }
        String tokenInfo = "Token: " + token;
        if (permissions != null && permissions.length() > 0) {
            tokenInfo += "\n权限: " + permissions;
        }
        tvToken.setText(tokenInfo);
    }

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
