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

import com.example.cn.helloworld.data.repository.AuthRepository;
import com.example.cn.helloworld.data.session.SessionManager;
import com.example.cn.helloworld.data.model.UserRole;
import com.example.cn.helloworld.ui.main.MainActivity;

import java.util.UUID;

public class ConfirmActivity extends AppCompatActivity {

    private TextView tvName, tvRole, tvPwd, tvEmail, tvPhone, tvGender,
            tvMajor, tvClass, tvDate, tvHobbies, tvBio;

    private Button btnBack, btnOk;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            if (getSupportActionBar() != null) {
                getSupportActionBar().setTitle("注册信息确认");
            }
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    finish();
                }
            });
        }

        tvName = (TextView) findViewById(R.id.tvName);
        tvRole = (TextView) findViewById(R.id.tvRole);
        tvPwd = (TextView) findViewById(R.id.tvPwd);
        tvEmail = (TextView) findViewById(R.id.tvEmail);
        tvPhone = (TextView) findViewById(R.id.tvPhone);
        tvGender = (TextView) findViewById(R.id.tvGender);
        tvMajor = (TextView) findViewById(R.id.tvMajor);
        tvClass = (TextView) findViewById(R.id.tvClass);
        tvDate = (TextView) findViewById(R.id.tvDate);
        tvHobbies = (TextView) findViewById(R.id.tvHobbies);
        tvBio = (TextView) findViewById(R.id.tvBio);

        btnBack = (Button) findViewById(R.id.btnBack);
        btnOk = (Button) findViewById(R.id.btnOk);

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

        tvName.setText(name);
        tvRole.setText("普通用户");
        tvPwd.setText(pwd);
        tvEmail.setText(email);
        tvPhone.setText(phone);
        tvGender.setText(gender);
        tvMajor.setText(major);
        tvClass.setText(clazz);
        tvDate.setText(date);
        tvHobbies.setText(hobbies);
        tvBio.setText(bio);

        btnBack.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                finish();
            }
        });

        btnOk.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                SQLiteDatabase db = null;

                try {
                    db = new DBHelper(ConfirmActivity.this).getWritableDatabase();

                    ContentValues cv = new ContentValues();
                    cv.put(DBHelper.C_NAME, name);
                    cv.put(DBHelper.C_PWD, pwd);
                    cv.put(DBHelper.C_EMAIL, email);
                    cv.put(DBHelper.C_PHONE, phone);
                    cv.put(DBHelper.C_GENDER, gender);
                    cv.put(DBHelper.C_MAJOR, major);
                    cv.put(DBHelper.C_CLAZZ, clazz);
                    cv.put(DBHelper.C_DATE, date);
                    cv.put(DBHelper.C_HOBBIES, hobbies);
                    cv.put(DBHelper.C_BIO, bio);

                    cv.put(DBHelper.C_ROLE, AuthRepository.ROLE_USER);

                    long id = db.insert(DBHelper.T_USER, null, cv);

                    if (id == -1) {
                        Toast.makeText(ConfirmActivity.this,
                                "数据库写入失败",
                                Toast.LENGTH_LONG).show();
                        return;
                    }

                    SessionManager sm = new SessionManager(ConfirmActivity.this);
                    sm.login(String.valueOf(id), name);
                    sm.saveSession(UUID.randomUUID().toString(), UserRole.USER, false);

                    Toast.makeText(ConfirmActivity.this, "注册成功！", Toast.LENGTH_SHORT).show();

                    Intent i = new Intent(ConfirmActivity.this, MainActivity.class);
                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
                            Intent.FLAG_ACTIVITY_NEW_TASK |
                            Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(i);

                    finish();

                } catch (Exception e) {
                    Toast.makeText(ConfirmActivity.this,
                            "错误：" + e.getMessage(),
                            Toast.LENGTH_LONG).show();

                } finally {
                    if (db != null) db.close();
                }
            }
        });
    }
}
