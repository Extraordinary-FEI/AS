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

    private static final String TAG = "AuthRepository";

    private final Context context;
    private DBHelper dbHelper;

    public AuthRepository(Context context) {
        this.context = context == null ? null : context.getApplicationContext();
    }

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
            return new LoginResult(false, null, username, null, null, null, "用户名和密码不能为空");
        }

        String role = TextUtils.isEmpty(requestedRole) ? "" : requestedRole.toUpperCase(Locale.US);

        if (ROLE_ADMIN.equals(role)) {
            if (TextUtils.isEmpty(verificationCode)) {
                return new LoginResult(false, null, username, null, null, null, "请输入管理员验证码");
            }
            if (!isValidAdminVerification(verificationCode)) {
                return new LoginResult(false, null, username, null, null, null, "管理员验证码错误");
            }
            if (isAdminAccount(username, password)) {
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
            return new LoginResult(false, null, username, null, null, null, "管理员账号或密码错误");
        }

        if (ROLE_USER.equals(role)) {
            UserRecord record = findUserAccount(username, password);
            if (record != null) {
                String token = generateToken(username);
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
            return new LoginResult(false, null, username, null, null, null, "用户名或密码错误");
        }

        return new LoginResult(false, null, username, null, null, null, "不支持的账号角色");
    }

    /** 判断管理员账号 */
    private boolean isAdminAccount(String username, String password) {
        return ADMIN_USERNAME.equalsIgnoreCase(username) && ADMIN_PASSWORD.equals(password);
    }

    /** 判断普通用户账号 */
    private UserRecord findUserAccount(String username, String password) {
        if (TextUtils.isEmpty(username) || TextUtils.isEmpty(password)) {
            return null;
        }

        if (USER_USERNAME.equalsIgnoreCase(username) && USER_PASSWORD.equals(password)) {
            return new UserRecord(USER_USERNAME, USER_USERNAME);
        }

        if (context == null) {
            return null;
        }

        if (dbHelper == null) {
            dbHelper = new DBHelper(context);
        }

        SQLiteDatabase db = null;
        Cursor cursor = null;
        try {
            db = dbHelper.getReadableDatabase();
            cursor = db.query(
                    DBHelper.T_USER,
                    new String[]{DBHelper.C_ID, DBHelper.C_NAME},
                    DBHelper.C_NAME + "=? AND " + DBHelper.C_PWD + "=?",
                    new String[]{username, password},
                    null,
                    null,
                    null,
                    "1"
            );

            if (cursor != null && cursor.moveToFirst()) {
                long id = cursor.getLong(cursor.getColumnIndex(DBHelper.C_ID));
                String name = cursor.getString(cursor.getColumnIndex(DBHelper.C_NAME));
                return new UserRecord(String.valueOf(id), name);
            }
        } catch (Exception e) {
            Log.e(TAG, "query user failed", e);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            if (db != null && db.isOpen()) {
                db.close();
            }
        }
        return null;
    }

    /** 管理员验证码 */
    private boolean isValidAdminVerification(String code) {
        return ADMIN_VERIFICATION_CODE.equalsIgnoreCase(code);
    }

    /** 生成唯一 token */
    private String generateToken(String username) {
        return String.format(Locale.US, "%s-%s", username, UUID.randomUUID().toString());
    }

    private static class UserRecord {
        final String id;
        final String name;

        private UserRecord(String id, String name) {
            this.id = id;
            this.name = name;
        }
    }
}
