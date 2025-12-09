package com.example.cn.helloworld;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * 负责创建与升级本地 SQLite 数据库的帮助类，覆盖了表格中的“数据存储（数据库）”要求。
 */
public class DBHelper extends SQLiteOpenHelper {

    public static final String DB_NAME = "fans.db";
    private static final int DB_VERSION = 2;

    public static final String T_USER       = "t_user";
    public static final String C_ID         = "_id";
    public static final String C_NAME       = "name";
    public static final String C_PWD        = "pwd";
    public static final String C_EMAIL      = "email";
    public static final String C_PHONE      = "phone";
    public static final String C_GENDER     = "gender";
    public static final String C_MAJOR      = "major";
    public static final String C_CLAZZ      = "clazz";
    public static final String C_DATE       = "date";
    public static final String C_HOBBIES    = "hobbies";
    public static final String C_BIO        = "bio";
    public static final String C_ROLE       = "role";
    public static final String C_CREATED_AT = "created_at";

    // 评论表
    public static final String T_REVIEW        = "t_review";
    public static final String C_PRODUCT_ID    = "product_id";
    public static final String C_USER_NAME     = "user_name";
    public static final String C_CONTENT       = "content";
    public static final String C_RATING        = "rating";
    public static final String C_REVIEW_TIME   = "review_created_at";

    /**
     * Create a helper for managing the local SQLite database.
     */
    public DBHelper(Context ctx) {
        super(ctx, DB_NAME, null, DB_VERSION);
    }

    /**
     * Build the initial user and review tables when the database is first created.
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
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
                C_ROLE + " TEXT," +
                C_CREATED_AT + " TIMESTAMP DEFAULT CURRENT_TIMESTAMP" +
                ")");

        db.execSQL("CREATE TABLE IF NOT EXISTS " + T_REVIEW + " (" +
                C_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                C_PRODUCT_ID + " TEXT," +
                C_USER_NAME + " TEXT," +
                C_CONTENT + " TEXT," +
                C_RATING + " REAL," +
                C_REVIEW_TIME + " INTEGER" +
                ")");
    }

    /**
     * Drop existing tables and recreate schema when a version upgrade occurs.
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + T_USER);
        db.execSQL("DROP TABLE IF EXISTS " + T_REVIEW);
        onCreate(db);
    }
}
