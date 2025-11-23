package com.example.cn.helloworld.data.storage;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * 专用于管理员后台的 SQLite 数据库，负责持久化商品及操作日志。
 */
public class AdminProductDbHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "admin_products.db";
    private static final int DB_VERSION = 1;

    public interface Tables {
        String PRODUCTS = "admin_products";
        String OPERATIONS = "admin_product_operations";
    }

    public interface ProductColumns {
        String ID = "id";
        String NAME = "name";
        String DESCRIPTION = "description";
        String PRICE = "price";
        String INVENTORY = "inventory";
        String CATEGORY = "category";
        String RATING = "rating";
        String TAGS = "tags";
        String STAR_EVENTS = "star_events";
        String ACTIVE = "active";
        String COVER_URL = "cover_url";
        String RELEASE_TIME = "release_time";
        String ATTRIBUTES = "attributes";
        String IMAGE_RES_ID = "image_res_id";
        String LIMITED_QUANTITY = "limited_quantity";
        String CATEGORY_ATTRIBUTES = "category_attributes";
        String UPDATED_AT = "updated_at";
    }

    public interface OperationColumns {
        String ID = "_id";
        String PRODUCT_ID = "product_id";
        String OPERATION = "operation";
        String OPERATOR = "operator";
        String TIMESTAMP = "timestamp";
        String SUMMARY = "summary";
    }

    public AdminProductDbHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createProducts = "CREATE TABLE IF NOT EXISTS " + Tables.PRODUCTS + " ("
                + ProductColumns.ID + " TEXT PRIMARY KEY,"
                + ProductColumns.NAME + " TEXT,"
                + ProductColumns.DESCRIPTION + " TEXT,"
                + ProductColumns.PRICE + " REAL,"
                + ProductColumns.INVENTORY + " INTEGER,"
                + ProductColumns.CATEGORY + " TEXT,"
                + ProductColumns.RATING + " INTEGER,"
                + ProductColumns.TAGS + " TEXT,"
                + ProductColumns.STAR_EVENTS + " TEXT,"
                + ProductColumns.ACTIVE + " INTEGER,"
                + ProductColumns.COVER_URL + " TEXT,"
                + ProductColumns.RELEASE_TIME + " TEXT,"
                + ProductColumns.ATTRIBUTES + " TEXT,"
                + ProductColumns.IMAGE_RES_ID + " INTEGER,"
                + ProductColumns.LIMITED_QUANTITY + " TEXT,"
                + ProductColumns.CATEGORY_ATTRIBUTES + " TEXT,"
                + ProductColumns.UPDATED_AT + " INTEGER"
                + ")";

        String createOperations = "CREATE TABLE IF NOT EXISTS " + Tables.OPERATIONS + " ("
                + OperationColumns.ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + OperationColumns.PRODUCT_ID + " TEXT,"
                + OperationColumns.OPERATION + " TEXT,"
                + OperationColumns.OPERATOR + " TEXT,"
                + OperationColumns.SUMMARY + " TEXT,"
                + OperationColumns.TIMESTAMP + " INTEGER"
                + ")";

        db.execSQL(createProducts);
        db.execSQL(createOperations);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + Tables.PRODUCTS);
        db.execSQL("DROP TABLE IF EXISTS " + Tables.OPERATIONS);
        onCreate(db);
    }
}