package com.example.cn.helloworld.data.repository;

import android.text.TextUtils;

import com.example.cn.helloworld.data.model.LoginResult;
import com.example.cn.helloworld.data.model.Permission;
import com.example.cn.helloworld.data.model.UserRole;

import java.util.EnumSet;
import java.util.Locale;
import java.util.UUID;

/**
 * AuthRepository
 * 用于处理管理员与普通用户登录验证。
 */
public class AuthRepository {

    /**
     * 登录逻辑：管理员入口与普通粉丝入口分开。
     */
    public LoginResult login(String username, String password, boolean useAdminEntrance) {
        if (TextUtils.isEmpty(username) || TextUtils.isEmpty(password)) {
            return new LoginResult(false, null, null, null, null, "用户名和密码不能为空");
        }

        // 管理员入口登录
        if (useAdminEntrance) {
            if (isAdminAccount(username, password)) {
                String token = generateToken(username);
                return new LoginResult(
                        true,
                        username,
                        token,
                        UserRole.ADMIN,
                        EnumSet.of(
                                Permission.MANAGE_PRODUCTS,
                                Permission.MANAGE_PLAYLISTS,
                                Permission.APPROVE_SUPPORT_TASKS,
                                Permission.VIEW_ANALYTICS
                        ),
                        "管理员登录成功"
                );
            } else {
                return new LoginResult(false, username, null, null, null, "管理员账号或密码错误");
            }
        }

        // 普通用户入口登录
        if (isUserAccount(username, password)) {
            String token = generateToken(username);
            return new LoginResult(
                    true,
                    username,
                    token,
                    UserRole.USER,
                    EnumSet.of(Permission.VIEW_ANALYTICS),
                    "用户登录成功"
            );
        }

        // 登录失败
        return new LoginResult(false, username, null, null, null, "用户名或密码错误");
    }

    /** 判断管理员账号 */
    private boolean isAdminAccount(String username, String password) {
        return "admin".equalsIgnoreCase(username) && "admin123".equals(password);
    }

    /** 判断普通用户账号 */
    private boolean isUserAccount(String username, String password) {
        return "user".equalsIgnoreCase(username) && "user123".equals(password);
    }

    /** 生成唯一 token */
    private String generateToken(String username) {
        return String.format(Locale.US, "%s-%s", username, UUID.randomUUID().toString());
    }
}
