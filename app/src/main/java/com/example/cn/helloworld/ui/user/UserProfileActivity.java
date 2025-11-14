package com.example.cn.helloworld.ui.user;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.cn.helloworld.R;
import com.example.cn.helloworld.data.session.SessionManager;
import com.example.cn.helloworld.ui.auth.LoginActivity;

public class UserProfileActivity extends AppCompatActivity {

    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        sessionManager = new SessionManager(this);

        // 使用 layout 中真实存在的 ID
        ImageView avatar  = (ImageView) findViewById(R.id.avatarImage);
        TextView username = (TextView) findViewById(R.id.tvUsername);
        TextView userId   = (TextView) findViewById(R.id.tvUserId);
        Button  logout    = (Button) findViewById(R.id.btnLogout);

        // 默认头像
        avatar.setImageResource(R.drawable.ic_user_default);

        // 显示用户信息
        username.setText(sessionManager.getUsername());
        userId.setText("UID: " + sessionManager.getUserId());

        // 退出登录
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sessionManager.logout();
                Intent intent = new Intent(UserProfileActivity.this, LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            }
        });
    }
}
