package com.example.cn.helloworld.data.repository;

import android.text.TextUtils;

import com.example.cn.helloworld.data.model.LoginResult;

import java.util.Locale;
import java.util.UUID;

public class AuthRepository {

    public static final String ROLE_ADMIN = "admin";
    public static final String ROLE_USER = "user";
    private static final String ADMIN_VERIFICATION_CODE = "246810";

    public LoginResult login(String username, String password, String requestedRole, String verificationCode) {
        if (TextUtils.isEmpty(username) || TextUtils.isEmpty(password)) {
            return new LoginResult(false, null, null, null, null, "用户名和密码不能为空");
        }

        boolean adminRequested = ROLE_ADMIN.equals(requestedRole);
        boolean isAdminAccount = isAdminAccount(username, password);
        boolean isUserAccount = isUserAccount(username, password);

        if (adminRequested) {
            if (!isAdminAccount) {
                if (isUserAccount) {
                    return new LoginResult(false, username, null, null, null, "该账号为粉丝账号，请选择粉丝入口登录");
                }
                return new LoginResult(false, username, null, null, null, "管理员账号或密码错误");
            }

            if (TextUtils.isEmpty(verificationCode)) {
                return new LoginResult(false, username, null, null, null, "请输入管理员短信验证码");
            }

            if (!ADMIN_VERIFICATION_CODE.equals(verificationCode)) {
                return new LoginResult(false, username, null, null, null, "短信验证码错误，请重新输入");
            }

            String token = generateToken(username);
            String permissions = "manage_products,manage_playlists,view_users";
            return new LoginResult(true, username, token, ROLE_ADMIN, permissions, "管理员登录成功");
        }

        if (!isUserAccount) {
            if (isAdminAccount) {
                return new LoginResult(false, username, null, null, null, "该账号为管理员账号，请选择管理员入口登录");
            }
            return new LoginResult(false, username, null, null, null, "用户名或密码错误");
        }

        String token = generateToken(username);
        return new LoginResult(true, username, token, ROLE_USER, null, "用户登录成功");
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
