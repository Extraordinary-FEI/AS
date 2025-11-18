package com.example.cn.helloworld;

import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.cn.helloworld.data.repository.AuthRepository;
import com.example.cn.helloworld.data.session.SessionManager;
import com.example.cn.helloworld.data.model.UserRole;
import com.example.cn.helloworld.ui.main.MainActivity;

import java.util.UUID;

public class ConfirmActivity extends AppCompatActivity {

    private TextView tvName, tvRole, tvPwd, tvEmail, tvPhone, tvGender, tvMajor,
            tvClass, tvDate, tvHobbies, tvBio;
    private Button btnBack, btnOk;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm);

        // Toolbar
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

        // Bind views
        tvName = findViewById(R.id.tvName);
        tvRole = findViewById(R.id.tvRole);
        tvPwd = findViewById(R.id.tvPwd);
        tvEmail = findViewById(R.id.tvEmail);
        tvPhone = findViewById(R.id.tvPhone);
        tvGender = findViewById(R.id.tvGender);
        tvMajor = findViewById(R.id.tvMajor);
        tvClass = findViewById(R.id.tvClass);
        tvDate = findViewById(R.id.tvDate);
        tvHobbies = findViewById(R.id.tvHobbies);
        tvBio = findViewById(R.id.tvBio);

        btnBack = findViewById(R.id.btnBack);
        btnOk = findViewById(R.id.btnOk);

        // Read data
        Intent it = getIntent();
        final String name = it.getStringExtra("name");
        final String pwd = it.getStringExtra("pwd");
        final String email = it.getStringExtra("email");
        final String phone = it.getStringExtra("phone");
        final String gender = it.getStringExtra("gender");
        final String major = it.getStringExtra("major");
        final String clazz = it.getStringExtra("clazz");
        final String date = it.getStringExtra("date");
        final String hobbies = it.getStringExtra("hobbies");
        final String bio = it.getStringExtra("bio");
        final String role = it.getStringExtra("role");

        // Fill UI
        tvName.setText(name);
        tvRole.setText(AuthRepository.ROLE_ADMIN.equals(role)
                ? getString(R.string.role_admin)
                : getString(R.string.role_fan)
        );
        tvPwd.setText(pwd);
        tvEmail.setText(email);
        tvPhone.setText(phone);
        tvGender.setText(gender);
        tvMajor.setText(major);
        tvClass.setText(clazz);
        tvDate.setText(date);
        tvHobbies.setText(hobbies);
        tvBio.setText(bio);

        // Back button
        btnBack.setOnClickListener(v -> finish());

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
        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SQLiteDatabase db = null;
                try {
                    db = new DBHelper(ConfirmActivity.this).getWritableDatabase();

                    ContentValues values = new ContentValues();
                    values.put(DBHelper.C_NAME, name);
                    values.put(DBHelper.C_PWD, pwd);
                    values.put(DBHelper.C_EMAIL, email);
                    values.put(DBHelper.C_PHONE, phone);
                    values.put(DBHelper.C_GENDER, gender);
                    values.put(DBHelper.C_MAJOR, major);
                    values.put(DBHelper.C_CLAZZ, clazz);
                    values.put(DBHelper.C_DATE, date);
                    values.put(DBHelper.C_HOBBIES, hobbies);
                    values.put(DBHelper.C_BIO, bio);
                  
                    long rowId = db.insert(DBHelper.T_USER, null, values);

                    if (rowId == -1L) {
                        Toast.makeText(
                                ConfirmActivity.this,
                                getString(R.string.error_db, getString(R.string.error_insert_failed)),
                                Toast.LENGTH_LONG
                        ).show();
                        return;
                    }

                    SessionManager sessionManager = new SessionManager(ConfirmActivity.this);
                    sessionManager.login(String.valueOf(rowId), name);
                    sessionManager.saveSession(UUID.randomUUID().toString(), UserRole.USER, false);

                    Toast.makeText(ConfirmActivity.this, R.string.msg_register_success, Toast.LENGTH_SHORT).show();
                    Intent mainIntent = new Intent(ConfirmActivity.this, MainActivity.class);
                    mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(mainIntent);

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                        finishAffinity();
                    } else {
                        finish();
                    }
                } catch (Exception e) {
                    Toast.makeText(ConfirmActivity.this, getString(R.string.error_db, e.getMessage()), Toast.LENGTH_LONG).show();
                } finally {
                    if (db != null) {
                        db.close();
                    }

                }
            }
        });
    }
}
