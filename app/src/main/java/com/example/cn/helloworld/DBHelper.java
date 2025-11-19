package com.example.cn.helloworld;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {

    public static final String DB_NAME = "fans.db";
    private static final int DB_VERSION = 1;

    // 表 & 字段
    public static final String T_USER       = "t_user";
    public static final String C_ID         = "_id";
    public static final String C_NAME       = "name";
    public static final String C_PWD        = "pwd";
    public static final String C_EMAIL      = "email";
    public static final String C_PHONE      = "phone";
    public static final String C_GENDER     = "gender";
    public static final String C_MAJOR      = "major";
    public static final String C_CLAZZ      = "clazz";//班级
    public static final String C_DATE       = "date";
    public static final String C_HOBBIES    = "hobbies";
    public static final String C_BIO        = "bio";
    public static final String C_CREATED_AT = "created_at";


    public DBHelper(Context ctx) {
        super(ctx, DB_NAME, null, DB_VERSION);
    }

    @Override public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS " + T_USER + " (" +
                C_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                C_NAME + " TEXT," +
                C_PWD + " TEXT," +
                C_EMAIL + " TEXT," +
                C_PHONE + " TEXT," +
                C_GENDER + " TEXT," +
                C_MAJOR + " TEXT," +
                C_CLAZZ + " TEXT," +
                C_DATE + " TEXT," +
                C_HOBBIES + " TEXT," +
                C_BIO + " TEXT," +
                "role TEXT," +                   // ⭐ 新增角色字段
                C_CREATED_AT + " TIMESTAMP DEFAULT CURRENT_TIMESTAMP" +
                ")");

    }

    @Override public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + T_USER);
        onCreate(db);
    }
}
