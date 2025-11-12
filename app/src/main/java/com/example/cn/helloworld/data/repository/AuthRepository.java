package com.example.cn.helloworld.data.repository;

import android.text.TextUtils;

import com.example.cn.helloworld.data.model.LoginResult;
import com.example.cn.helloworld.data.model.Permission;
import com.example.cn.helloworld.data.model.UserRole;

import java.util.EnumSet;
import java.util.Locale;
import java.util.UUID;

public class AuthRepository {

    public LoginResult login(String username, String password, boolean useAdminEntrance) {
        if (TextUtils.isEmpty(username) || TextUtils.isEmpty(password)) {
            return new LoginResult(false, null, null, null, null, "用户名和密码不能为空");
        }

        if (useAdminEntrance) {
            if (isAdminAccount(username, password)) {
                String token = generateToken(username);
                return new LoginResult(true,
                        username,
                        token,
                        UserRole.ADMIN,
                        EnumSet.of(Permission.MANAGE_PRODUCTS,
                                Permission.MANAGE_PLAYLISTS,
                                Permission.APPROVE_SUPPORT_TASKS,
                                Permission.VIEW_ANALYTICS),
                        "管理员登录成功");
            } else {
                return new LoginResult(false, username, null, null, null, "管理员账号或密码错误");
            }
        }

        if (isUserAccount(username, password)) {
            String token = generateToken(username);
            return new LoginResult(true,
                    username,
                    token,
                    UserRole.USER,
                    EnumSet.of(Permission.VIEW_ANALYTICS),
                    "用户登录成功");
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
