package com.example.cn.helloworld.ui.auth;

import android.content.Intent;
import android.os.Bundle;

import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.cn.helloworld.MusicActivity;
import com.example.cn.helloworld.R;
import com.example.cn.helloworld.RegisterActivity;
import com.example.cn.helloworld.data.model.LoginResult;
import com.example.cn.helloworld.data.repository.AuthRepository;
import com.example.cn.helloworld.data.session.SessionManager;
import com.example.cn.helloworld.ui.admin.AdminDashboardActivity;

public class LoginActivity extends AppCompatActivity {

    private EditText etUsername;
    private EditText etPassword;
    private RadioGroup rgRole;
    private TextView tvStatus;
    private Button btnLogin;
    private Button btnGoRegister;

    private AuthRepository authRepository;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        authRepository = new AuthRepository();
        sessionManager = new SessionManager(this);

        if (sessionManager.isLoggedIn()) {
            navigateByRole(sessionManager.getRole());
            finish();
            return;
        }

        bindViews();
        initEvents();
    }

    private void bindViews() {
        etUsername = (EditText) findViewById(R.id.etUsername);
        etPassword = (EditText) findViewById(R.id.etPassword);
        rgRole = (RadioGroup) findViewById(R.id.rgRole);
        tvStatus = (TextView) findViewById(R.id.tvLoginStatus);
        btnLogin = (Button) findViewById(R.id.btnLogin);
        btnGoRegister = (Button) findViewById(R.id.btnGoRegister);
    }

    private void initEvents() {
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                performLogin();
            }
        });

        btnGoRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });
    }

    private void performLogin() {
        String username = etUsername.getText() == null ? "" : etUsername.getText().toString().trim();
        String password = etPassword.getText() == null ? "" : etPassword.getText().toString().trim();
        boolean adminEntrance = rgRole.getCheckedRadioButtonId() == R.id.rbAdmin;

        LoginResult result = authRepository.login(username, password, adminEntrance);
        if (result.getMessage() != null) {
            tvStatus.setText(result.getMessage());
        }

        if (result.isSuccess()) {
            tvStatus.setTextColor(ContextCompat.getColor(this, android.R.color.holo_green_dark));
            sessionManager.saveSession(result);
            Toast.makeText(this, result.getMessage(), Toast.LENGTH_SHORT).show();
            navigateByRole(result.getRole());
            finish();
        } else {
            tvStatus.setTextColor(ContextCompat.getColor(this, android.R.color.holo_red_dark));
            Toast.makeText(this, result.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void navigateByRole(String role) {
        if (AuthRepository.ROLE_ADMIN.equals(role)) {
            Intent adminIntent = new Intent(this, AdminDashboardActivity.class);
            startActivity(adminIntent);
        } else {
            Intent userIntent = new Intent(this, MusicActivity.class);
            startActivity(userIntent);
        }
    }
}
