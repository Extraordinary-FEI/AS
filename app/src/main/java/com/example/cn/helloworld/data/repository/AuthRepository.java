package com.example.cn.helloworld.data.repository;

import android.text.TextUtils;

import com.example.cn.helloworld.data.model.LoginResult;

import java.util.Locale;
import java.util.UUID;

public class AuthRepository {

    public static final String ROLE_ADMIN = "admin";
    public static final String ROLE_USER = "user";

    public LoginResult login(String username, String password, boolean useAdminEntrance) {
        if (TextUtils.isEmpty(username) || TextUtils.isEmpty(password)) {
            return new LoginResult(false, null, null, null, null, "用户名和密码不能为空");
        }

        if (useAdminEntrance) {
            if (isAdminAccount(username, password)) {
                String token = generateToken(username);
                String permissions = "manage_products,manage_playlists,view_users";
                return new LoginResult(true, username, token, ROLE_ADMIN, permissions, "管理员登录成功");
            } else {
                return new LoginResult(false, username, null, null, null, "管理员账号或密码错误");
            }
        }

        if (isUserAccount(username, password)) {
            String token = generateToken(username);
            return new LoginResult(true, username, token, ROLE_USER, null, "用户登录成功");
        }

        return new LoginResult(false, username, null, null, null, "用户名或密码错误");
    }

    private boolean isAdminAccount(String username, String password) {
        return "admin".equalsIgnoreCase(username) && "admin123".equals(password);
    }

    private boolean isUserAccount(String username, String password) {
        return "user".equalsIgnoreCase(username) && "user123".equals(password);
    }

    private String generateToken(String username) {
        return String.format(Locale.US, "%s-%s", username, UUID.randomUUID().toString());
    }
}
