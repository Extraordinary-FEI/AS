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

public class AuthRepository {

    public static final String ROLE_ADMIN = UserRole.ADMIN.name();
    public static final String ROLE_USER = UserRole.USER.name();

    private static final String ADMIN_USERNAME = "admin";
    private static final String ADMIN_PASSWORD = "admin123";
    private static final String ADMIN_CODE = "YYQX2020";

    private static final String TAG = "AuthRepository";

    private DBHelper helper;
    private Context context;

    public AuthRepository(Context context) {
        this.context = context.getApplicationContext();
        helper = new DBHelper(this.context);
    }

    public LoginResult login(String username, String password, boolean adminEntry) {
        return login(username, password,
                adminEntry ? ROLE_ADMIN : ROLE_USER,
                "");
    }

    public LoginResult login(String username, String password, String role, String code) {

        if (TextUtils.isEmpty(username) || TextUtils.isEmpty(password)) {
            return new LoginResult(false, null, username, null, null, null,
                    "用户名或密码不能为空");
        }

        role = role == null ? ROLE_USER : role.toUpperCase(Locale.US);

        // -------------------------
        // 管理员登录
        // -------------------------
        if (ROLE_ADMIN.equals(role)) {

            if (TextUtils.isEmpty(code)) {
                return new LoginResult(false, null, username, null, null, null,
                        "请输入管理员验证码");
            }

            if (!ADMIN_CODE.equalsIgnoreCase(code)) {
                return new LoginResult(false, null, username, null, null, null,
                        "管理员验证码错误");
            }

            if (ADMIN_USERNAME.equalsIgnoreCase(username)
                    && ADMIN_PASSWORD.equals(password)) {

                String token = token(username);

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
                                Permission.VIEW_ANALYTICS),
                        "管理员登录成功");
            }

            return new LoginResult(false, null, username, null, null, null,
                    "管理员账号或密码错误");
        }

        // -------------------------
        // 普通用户 SQLite 查询
        // -------------------------

        UserRecord record = queryUser(username, password);
        if (record != null) {

            String token = token(record.name);

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

    private UserRecord queryUser(String username, String pwd) {
        SQLiteDatabase db = null;
        Cursor cursor = null;

        try {
            db = helper.getReadableDatabase();

            cursor = db.query(
                    DBHelper.T_USER,
                    new String[]{
                            DBHelper.C_ID,
                            DBHelper.C_NAME,
                            DBHelper.C_ROLE
                    },
                    DBHelper.C_NAME + "=? AND " + DBHelper.C_PWD + "=?",
                    new String[]{username, pwd},
                    null, null, null, "1"
            );

            if (cursor != null && cursor.moveToFirst()) {
                String role = cursor.getString(cursor.getColumnIndex(DBHelper.C_ROLE));

                if (ROLE_USER.equalsIgnoreCase(role)) {
                    return new UserRecord(
                            String.valueOf(cursor.getLong(cursor.getColumnIndex(DBHelper.C_ID))),
                            cursor.getString(cursor.getColumnIndex(DBHelper.C_NAME))
                    );
                }
            }

        } catch (Exception e) {
            Log.e(TAG, "queryUser error:", e);
        } finally {
            if (cursor != null)
                cursor.close();
            if (db != null)
                db.close();
        }

        return null;
    }

    private String token(String name) {
        return name + "-" + UUID.randomUUID().toString();
    }

    private static class UserRecord {
        String id;
        String name;

        UserRecord(String id, String name) {
            this.id = id;
            this.name = name;
        }
    }
}
