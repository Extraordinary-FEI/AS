package com.example.cn.helloworld.data.repository;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;
import android.util.Log;

import com.example.cn.helloworld.DBHelper;
import com.example.cn.helloworld.data.model.LoginResult;
import com.example.cn.helloworld.data.model.Permission;
import com.example.cn.helloworld.data.model.UserRole;

import java.util.EnumSet;
import java.util.Locale;
import java.util.UUID;

/**
 * AuthRepository
 * 修复版：
 * - 支持 SQLite 注册用户（含 role 字段）
 * - 支持管理员验证
 * - 修复登录失败和找不到用户的问题
 * - 自动兼容老师要求的 SQLite 结构
 */
public class AuthRepository {

    public static final String ROLE_ADMIN = UserRole.ADMIN.name();
    public static final String ROLE_USER = UserRole.USER.name();

    private static final String ADMIN_USERNAME = "admin";
    private static final String ADMIN_PASSWORD = "admin123";
    private static final String ADMIN_VERIFICATION_CODE = "YYQX2020";

    private static final String TAG = "AuthRepository";
    private final Context context;

    private DBHelper dbHelper;

    public AuthRepository(Context context) {
        this.context = context.getApplicationContext();
        this.dbHelper = new DBHelper(this.context);
    }

    /**
     * 兼容旧用法
     */
    public LoginResult login(String username, String password, boolean useAdminEntrance) {
        String role = useAdminEntrance ? ROLE_ADMIN : ROLE_USER;
        return login(username, password, role, "");
    }

    /**
     * 登录入口（管理员 + 用户）
     */
    public LoginResult login(String username,
                             String password,
                             String requestedRole,
                             String verificationCode) {

        if (TextUtils.isEmpty(username) || TextUtils.isEmpty(password)) {
            return new LoginResult(false, null, username, null, null, null,
                    "用户名和密码不能为空");
        }

        String role = TextUtils.isEmpty(requestedRole)
                ? ROLE_USER
                : requestedRole.toUpperCase(Locale.US);

        // ========= 管理员登录 ==========
        if (ROLE_ADMIN.equals(role)) {

            // 管理员验证码必须输入
            if (TextUtils.isEmpty(verificationCode)) {
                return new LoginResult(false, null, username, null, null, null,
                        "请输入管理员验证码");
            }

            if (!ADMIN_VERIFICATION_CODE.equalsIgnoreCase(verificationCode)) {
                return new LoginResult(false, null, username, null, null, null,
                        "管理员验证码错误");
            }

            // 管理员账号判断（写死）
            if (ADMIN_USERNAME.equalsIgnoreCase(username)
                    && ADMIN_PASSWORD.equals(password)) {

                String token = generateToken(username);

                return new LoginResult(
                        true,
                        username,
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

            return new LoginResult(false, null, username, null, null, null,
                    "管理员账号或密码错误");
        }

        // ========== 普通用户登录 ==========
        if (ROLE_USER.equals(role)) {

            UserRecord record = queryUserFromSQLite(username, password);

            if (record != null) {
                String token = generateToken(record.name);

                return new LoginResult(
                        true,
                        record.id,
                        record.name,
                        token,
                        UserRole.USER,
                        EnumSet.of(Permission.VIEW_ANALYTICS),
                        "用户登录成功"
                );
            }

            return new LoginResult(false, null, username, null, null, null,
                    "用户名或密码错误");
        }

        return new LoginResult(false, null, username, null, null, null,
                "不支持的账号角色");
    }

    /**
     * 从 SQLite 查询用户
     * - 支持 ConfirmActivity 注册记录
     * - 必须带上 role 字段
     */
    private UserRecord queryUserFromSQLite(String username, String password) {
        SQLiteDatabase db = null;
        Cursor cursor = null;

        try {
            db = dbHelper.getReadableDatabase();

            cursor = db.query(
                    DBHelper.T_USER,
                    new String[]{
                            DBHelper.C_ID,
                            DBHelper.C_NAME,
                            DBHelper.C_PWD,
                            "role"        // ⭐ 必须有 role 字段
                    },
                    DBHelper.C_NAME + "=? AND " + DBHelper.C_PWD + "=?",
                    new String[]{username, password},
                    null,
                    null,
                    null
            );

            if (cursor != null && cursor.moveToFirst()) {

                long id = cursor.getLong(cursor.getColumnIndex(DBHelper.C_ID));
                String name = cursor.getString(cursor.getColumnIndex(DBHelper.C_NAME));
                String role = cursor.getString(cursor.getColumnIndex("role"));

                // 只允许普通用户走此流程
                if (ROLE_USER.equalsIgnoreCase(role)) {
                    return new UserRecord(String.valueOf(id), name);
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "SQLite 查询用户失败：", e);
        } finally {
            if (cursor != null) cursor.close();
            if (db != null && db.isOpen()) db.close();
        }

        return null;
    }

    /** 生成 token */
    private String generateToken(String username) {
        return username + "-" + UUID.randomUUID().toString();
    }

    /** SQLite 查询结果封装 */
    private static class UserRecord {
        final String id;
        final String name;

        UserRecord(String id, String name) {
            this.id = id;
            this.name = name;
        }
    }
}
