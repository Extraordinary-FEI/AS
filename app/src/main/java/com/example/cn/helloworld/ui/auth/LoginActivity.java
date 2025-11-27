package com.example.cn.helloworld.ui.auth;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.cn.helloworld.R;
import com.example.cn.helloworld.RegisterActivity;
import com.example.cn.helloworld.data.model.LoginResult;
import com.example.cn.helloworld.data.model.UserRole;
import com.example.cn.helloworld.data.repository.AuthRepository;
import com.example.cn.helloworld.data.session.SessionManager;
import com.example.cn.helloworld.ui.admin.AdminDashboardActivity;
import com.example.cn.helloworld.ui.main.MainActivity;

public class LoginActivity extends AppCompatActivity {

    private EditText etUsername;
    private EditText etPassword;
    private EditText etVerificationCode;
    private RadioGroup rgRole;
    private RadioButton rbUser;
    private RadioButton rbAdmin;
    private TextView tvStatus;
    private TextView tvRoleHint;
    private TextView tvSecurityHint;
    private Button btnLogin;
    private Button btnGoRegister;
    private CheckBox cbRememberLogin;
    private View verificationLayout;

    private AuthRepository authRepository;
    private SessionManager sessionManager;

    @RequiresApi(api = Build.VERSION_CODES.GINGERBREAD)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        authRepository = new AuthRepository(getApplicationContext());
        sessionManager = new SessionManager(this);

        // 自动登录
        if (sessionManager.isLoggedIn() && sessionManager.shouldRemember()) {
            navigateByRole(sessionManager.getRole());
            finish();
            return;
        }

        bindViews();
        prefillLoginForm();
        initEvents();
        updateRoleUi(rgRole.getCheckedRadioButtonId() == R.id.rbAdmin);
    }

    private void bindViews() {
        etUsername = (EditText) findViewById(R.id.etUsername);
        etPassword = (EditText) findViewById(R.id.etPassword);
        etVerificationCode = (EditText) findViewById(R.id.etVerificationCode);
        rgRole = (RadioGroup) findViewById(R.id.rgRole);
        rbUser = (RadioButton) findViewById(R.id.rbUser);
        rbAdmin = (RadioButton) findViewById(R.id.rbAdmin);
        tvStatus = (TextView) findViewById(R.id.tvLoginStatus);
        tvRoleHint = (TextView) findViewById(R.id.tvRoleHint);
        tvSecurityHint = (TextView) findViewById(R.id.tvSecurityHint);
        btnLogin = (Button) findViewById(R.id.btnLogin);
        btnGoRegister = (Button) findViewById(R.id.btnGoRegister);
        cbRememberLogin = (CheckBox) findViewById(R.id.cbRememberLogin);
        verificationLayout = findViewById(R.id.layoutVerificationCode);
    }

    private void initEvents() {

        rgRole.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                updateRoleUi(checkedId == R.id.rbAdmin);
            }
        });

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                performLogin();
            }
        });

        btnGoRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (rgRole.getCheckedRadioButtonId() == R.id.rbAdmin) {
                    Toast.makeText(LoginActivity.this,
                            R.string.error_admin_register_hint,
                            Toast.LENGTH_SHORT).show();
                    return;
                }
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
            }
        });
    }


    private void performLogin() {
        String username = etUsername.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        String verification = etVerificationCode.getText().toString().trim();

        boolean adminEntrance = rgRole.getCheckedRadioButtonId() == R.id.rbAdmin;

        String requestedRole = adminEntrance ?
                AuthRepository.ROLE_ADMIN : AuthRepository.ROLE_USER;

        // 保存记住我设置
        sessionManager.updateLoginPreference(username, requestedRole, cbRememberLogin.isChecked());

        LoginResult result = authRepository.login(username, password, requestedRole, verification);

        if (result.getMessage() != null) {
            tvStatus.setText(result.getMessage());
        }

        if (result.isSuccess()) {
            tvStatus.setTextColor(ContextCompat.getColor(this, android.R.color.holo_green_dark));

            // ★ 必须保存 userId，不然自动登录不会生效
            String userId = result.getUserId() == null
                    ? result.getUsername()
                    : result.getUserId();

            if (result.getRole() == UserRole.ADMIN) {
                sessionManager.loginAdmin(userId, result.getUsername());
            } else {
                sessionManager.login(userId, result.getUsername());
            }

            // 保存 token + role
            sessionManager.saveSession(
                    result.getToken(),
                    result.getRole(),
                    cbRememberLogin.isChecked()
            );

            Toast.makeText(this, result.getMessage(), Toast.LENGTH_SHORT).show();
            navigateByRole(result.getRole());
            finish();

        } else {
            tvStatus.setTextColor(ContextCompat.getColor(this, android.R.color.holo_red_dark));
            Toast.makeText(this, result.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void navigateByRole(UserRole role) {
        startActivity(new Intent(this, MainActivity.class));
    }

    @RequiresApi(api = Build.VERSION_CODES.GINGERBREAD)
    private void prefillLoginForm() {
        String lastUsername = sessionManager.getLastUsername();
        if (!lastUsername.equals("")) {
            etUsername.setText(lastUsername);
        }

        String lastRole = sessionManager.getLastSelectedRole();
        if (AuthRepository.ROLE_ADMIN.equals(lastRole)) {
            rgRole.check(R.id.rbAdmin);
        } else {
            rgRole.check(R.id.rbUser);
        }

        cbRememberLogin.setChecked(sessionManager.shouldRemember());
    }

    private void updateRoleUi(boolean adminSelected) {
        verificationLayout.setVisibility(adminSelected ? View.VISIBLE : View.GONE);

        if (!adminSelected) {
            etVerificationCode.setText("");
        }

        btnGoRegister.setVisibility(adminSelected ? View.GONE : View.VISIBLE);

        tvRoleHint.setText(adminSelected ?
                R.string.role_admin_login_hint :
                R.string.role_user_login_hint);

        tvSecurityHint.setText(adminSelected ?
                R.string.login_security_tip_admin :
                R.string.login_security_tip_user);

        tvStatus.setTextColor(ContextCompat.getColor(this, R.color.textSecondary));
        tvStatus.setText(R.string.login_status_default);
    }
}
