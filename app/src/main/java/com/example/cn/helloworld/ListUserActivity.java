package com.example.cn.helloworld;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ListUserActivity extends AppCompatActivity {

    private ListView lvUsers;

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_user);

        // 顶部栏
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            if (getSupportActionBar() != null) {
                getSupportActionBar().setTitle("用户列表");
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            }
            toolbar.setNavigationOnClickListener(new android.view.View.OnClickListener() {
                @Override public void onClick(android.view.View v) { finish(); }
            });
        }

        lvUsers = (ListView) findViewById(R.id.lvUsers);

        DBHelper helper = new DBHelper(this);
        SQLiteDatabase db = null;
        Cursor c = null;
        try {
            db = helper.getReadableDatabase();
            c = db.query(DBHelper.T_USER,
                    new String[]{DBHelper.C_ID, DBHelper.C_NAME, DBHelper.C_MAJOR},
                    null, null, null, null,
                    DBHelper.C_CREATED_AT + " DESC");

            List<Map<String, String>> data = new ArrayList<Map<String, String>>();
            while (c.moveToNext()) {
                Map<String, String> row = new HashMap<String, String>();
                row.put("name",  c.getString(c.getColumnIndex(DBHelper.C_NAME)));
                row.put("major", c.getString(c.getColumnIndex(DBHelper.C_MAJOR)));
                data.add(row);
            }

            SimpleAdapter adapter = new SimpleAdapter(
                    this,
                    data,
                    R.layout.useritem,
                    new String[]{"name", "major"},
                    new int[]{R.id.tvItemName, R.id.tvItemMajor}
            );
            lvUsers.setAdapter(adapter);

        } finally {
            if (c  != null) c.close();
            if (db != null) db.close();
        }
    }
}
