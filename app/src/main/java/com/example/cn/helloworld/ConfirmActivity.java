package com.example.cn.helloworld;

import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class ConfirmActivity extends AppCompatActivity {

    private TextView tvName, tvPwd, tvEmail, tvPhone, tvGender, tvMajor,
            tvClass, tvDate, tvHobbies, tvBio;
    private Button btnBack, btnOk;

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm);

        // 1) Toolbar（注意：布局里必须有 id=toolbar 的 Toolbar）
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            if (getSupportActionBar() != null) {
                getSupportActionBar().setTitle("注册信息确认");
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            }
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override public void onClick(View v) { finish(); }
            });
        }

        // 2) 绑定视图（id 要和 activity_confirm.xml 完全一致）
        tvName    = (TextView) findViewById(R.id.tvName);
        tvPwd     = (TextView) findViewById(R.id.tvPwd);
        tvEmail   = (TextView) findViewById(R.id.tvEmail);
        tvPhone   = (TextView) findViewById(R.id.tvPhone);
        tvGender  = (TextView) findViewById(R.id.tvGender);
        tvMajor   = (TextView) findViewById(R.id.tvMajor);
        tvClass   = (TextView) findViewById(R.id.tvClass);
        tvDate    = (TextView) findViewById(R.id.tvDate);
        tvHobbies = (TextView) findViewById(R.id.tvHobbies);
        tvBio     = (TextView) findViewById(R.id.tvBio);

        btnBack   = (Button) findViewById(R.id.btnBack);
        btnOk     = (Button) findViewById(R.id.btnOk);

        // 3) 取参（即使某些 key 为空也不会崩）
        Intent it = getIntent();
        final String name    = it.getStringExtra("name");
        final String pwd     = it.getStringExtra("pwd");
        final String email   = it.getStringExtra("email");
        final String phone   = it.getStringExtra("phone");
        final String gender  = it.getStringExtra("gender");
        final String major   = it.getStringExtra("major");
        final String clazz   = it.getStringExtra("clazz");
        final String date    = it.getStringExtra("date");
        final String hobbies = it.getStringExtra("hobbies");
        final String bio     = it.getStringExtra("bio");

        // 4) 显示
        tvName.setText(name);
        tvPwd.setText(pwd);
        tvEmail.setText(email);
        tvPhone.setText(phone);
        tvGender.setText(gender);
        tvMajor.setText(major);
        tvClass.setText(clazz);
        tvDate.setText(date);
        tvHobbies.setText(hobbies);
        tvBio.setText(bio);

        // 5) 返回
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) { finish(); }
        });

        // 6) 确认：入库 → 跳列表
//        btnOk.setOnClickListener(new View.OnClickListener() {
//            @Override public void onClick(View v) {
//                DBHelper helper = new DBHelper(ConfirmActivity.this);
//                SQLiteDatabase db = null;
//                try {
//                    db = helper.getWritableDatabase();
//                    ContentValues cv = new ContentValues();
//                    cv.put(DBHelper.C_NAME,  name);
//                    cv.put(DBHelper.C_PWD,   pwd);
//                    cv.put(DBHelper.C_EMAIL, email);
//                    cv.put(DBHelper.C_PHONE, phone);
//                    cv.put(DBHelper.C_GENDER,gender);
//                    cv.put(DBHelper.C_MAJOR, major);
//                    cv.put(DBHelper.C_CLAZZ, clazz);
//                    cv.put(DBHelper.C_DATE,  date);
//                    cv.put(DBHelper.C_HOBBIES, hobbies);
//                    cv.put(DBHelper.C_BIO,   bio);
//                    db.insert(DBHelper.T_USER, null, cv);
//
//                    Toast.makeText(getApplicationContext(), "已保存到数据库", Toast.LENGTH_SHORT).show();
//                    startActivity(new Intent(ConfirmActivity.this, ListUserActivity.class));
//                    finish(); // 关闭确认页，回退栈更干净
//                } catch (Exception e) {
//                    Toast.makeText(getApplicationContext(), "数据库异常：" + e.getMessage(), Toast.LENGTH_LONG).show();
//                } finally {
//                    if (db != null) db.close();
//                }
//            }
//        });
        //跳转到歌曲列表
        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // （1）保存数据逻辑（保持不变）
                SQLiteDatabase db = new DBHelper(ConfirmActivity.this).getWritableDatabase();
                ContentValues values = new ContentValues();
                values.put("name", tvName.getText().toString());
                values.put("pwd", tvPwd.getText().toString());
                values.put("email", tvEmail.getText().toString());
                values.put("phone", tvPhone.getText().toString());
                db.insert("tb_user", null, values);
                db.close();

                Toast.makeText(ConfirmActivity.this, "注册成功！", Toast.LENGTH_SHORT).show();

                // （2）修改跳转目标为 MusicActivity
                Intent intent = new Intent(ConfirmActivity.this, MusicActivity.class);
                startActivity(intent);

                // （3）关闭当前页面，防止回退又回到确认页
                finish();
            }
        });




    }
}
