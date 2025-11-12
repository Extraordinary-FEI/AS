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

    public static final String ROLE_ADMIN = UserRole.ADMIN.name();
    public static final String ROLE_USER = UserRole.USER.name();

    private static final String ADMIN_USERNAME = "admin";
    private static final String ADMIN_PASSWORD = "admin123";
    private static final String ADMIN_VERIFICATION_CODE = "YYQX2020";

    private static final String USER_USERNAME = "user";
    private static final String USER_PASSWORD = "user123";

    /**
     * 兼容旧调用：根据入口判断角色。
     */
    public LoginResult login(String username, String password, boolean useAdminEntrance) {
        String role = useAdminEntrance ? ROLE_ADMIN : ROLE_USER;
        return login(username, password, role, "");
    }

    /**
     * 登录逻辑：管理员入口与普通粉丝入口分开。
     */
    public LoginResult login(String username,
                             String password,
                             String requestedRole,
                             String verificationCode) {
        if (TextUtils.isEmpty(username) || TextUtils.isEmpty(password)) {
            return new LoginResult(false, null, null, null, null, "用户名和密码不能为空");
        }

        String role = TextUtils.isEmpty(requestedRole) ? "" : requestedRole.toUpperCase(Locale.US);

        if (ROLE_ADMIN.equals(role)) {
            if (TextUtils.isEmpty(verificationCode)) {
                return new LoginResult(false, username, null, null, null, "请输入管理员验证码");
            }
            if (!isValidAdminVerification(verificationCode)) {
                return new LoginResult(false, username, null, null, null, "管理员验证码错误");
            }
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
            }
            return new LoginResult(false, username, null, null, null, "管理员账号或密码错误");
        }

        if (ROLE_USER.equals(role)) {
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
            return new LoginResult(false, username, null, null, null, "用户名或密码错误");
        }

        return new LoginResult(false, username, null, null, null, "不支持的账号角色");
    }

    /** 判断管理员账号 */
    private boolean isAdminAccount(String username, String password) {
        return ADMIN_USERNAME.equalsIgnoreCase(username) && ADMIN_PASSWORD.equals(password);
    }

    /** 判断普通用户账号 */
    private boolean isUserAccount(String username, String password) {
        return USER_USERNAME.equalsIgnoreCase(username) && USER_PASSWORD.equals(password);
    }

    /** 管理员验证码 */
    private boolean isValidAdminVerification(String code) {
        return ADMIN_VERIFICATION_CODE.equalsIgnoreCase(code);
    }

    /** 生成唯一 token */
    private String generateToken(String username) {
        return String.format(Locale.US, "%s-%s", username, UUID.randomUUID().toString());
    }
}
