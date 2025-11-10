package com.example.cn.helloworld;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.cn.helloworld.R;

import java.util.Calendar;

public class RegisterActivity extends AppCompatActivity {

    private EditText etName, etPassword, etEmail, etPhone, etBio, etFansDate;
    private RadioButton rbMale, rbFemale;
    private CheckBox cbSing, cbDance, cbPaint;
    private Spinner spMajor, spClass;
    private Button btnRegister, btnCancel;

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // 设置标题栏
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("易烊千玺粉丝应援注册系统");
        }

        bindViews();
        initEvents();
    }

    private void bindViews() {
        etName = (EditText) findViewById(R.id.etName);
        etPassword = (EditText) findViewById(R.id.etPassword);
        etEmail = (EditText) findViewById(R.id.etEmail);
        etPhone = (EditText) findViewById(R.id.etPhone);
        etBio = (EditText) findViewById(R.id.etBio);
        etFansDate = (EditText) findViewById(R.id.etFansDate);

        rbMale = (RadioButton) findViewById(R.id.rbMale);
        rbFemale = (RadioButton) findViewById(R.id.rbFemale);

        cbSing = (CheckBox) findViewById(R.id.cbSing);
        cbDance = (CheckBox) findViewById(R.id.cbDance);
        cbPaint = (CheckBox) findViewById(R.id.cbPaint);

        spMajor = (Spinner) findViewById(R.id.spMajor);
        spClass = (Spinner) findViewById(R.id.spClass);

        btnRegister = (Button) findViewById(R.id.btnRegister);
        btnCancel = (Button) findViewById(R.id.btnCancel);
    }

    private void initEvents() {
        // “专业”用 entries="@array/majors"，无需代码绑定

        // “班级”用 ArrayAdapter 绑定（确保 import android.widget.ArrayAdapter;）
        String[] classes = new String[]{"一班", "二班", "三班"};
        ArrayAdapter<String> classAdapter = new ArrayAdapter<String>(
                this, android.R.layout.simple_spinner_dropdown_item, classes);
        spClass.setAdapter(classAdapter);

        // 日期选择器
        etFansDate.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                Calendar cal = Calendar.getInstance();
                DatePickerDialog dlg = new DatePickerDialog(
                        RegisterActivity.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int y, int m, int d) {
                                String mm = (m + 1) < 10 ? ("0" + (m + 1)) : String.valueOf(m + 1);
                                String dd = d < 10 ? ("0" + d) : String.valueOf(d);
                                etFansDate.setText(y + "-" + mm + "-" + dd);
                            }
                        },
                        cal.get(Calendar.YEAR),
                        cal.get(Calendar.MONTH),
                        cal.get(Calendar.DAY_OF_MONTH)
                );
                dlg.getDatePicker().setMaxDate(System.currentTimeMillis());
                dlg.show();
            }
        });

        // 注册
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                String name = textOf(etName);
                String pwd  = textOf(etPassword);
                if (name.length()==0 || pwd.length()==0) {
                    Toast.makeText(getApplicationContext(),"姓名和密码不能为空",Toast.LENGTH_SHORT).show();
                    return;
                }

                String email = textOf(etEmail);
                String phone = textOf(etPhone);
                String gender = rbMale.isChecked() ? "男" : "女";
                String major  = itemOf(spMajor);
                String clazz  = itemOf(spClass);
                String date   = textOf(etFansDate);
                String bio    = textOf(etBio);

                StringBuilder hobby = new StringBuilder();
                if (cbSing.isChecked())  hobby.append("唱歌 ");
                if (cbDance.isChecked()) hobby.append("跳舞 ");
                if (cbPaint.isChecked()) hobby.append("绘画 ");

                Intent it = new Intent(RegisterActivity.this, ConfirmActivity.class);
                it.putExtra("name", name);
                it.putExtra("pwd",  pwd);
                it.putExtra("email", email);
                it.putExtra("phone", phone);
                it.putExtra("gender", gender);
                it.putExtra("major",  major);
                it.putExtra("clazz",  clazz);
                it.putExtra("date",   date);
                it.putExtra("hobbies", hobby.toString().trim());
                it.putExtra("bio", bio);
                startActivity(it);
            }
        });

        // 取消
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                etName.setText(""); etPassword.setText(""); etEmail.setText("");
                etPhone.setText(""); etBio.setText(""); etFansDate.setText("");
                rbMale.setChecked(true);
                cbSing.setChecked(false); cbDance.setChecked(false); cbPaint.setChecked(false);
                Toast.makeText(getApplicationContext(),"已清空",Toast.LENGTH_SHORT).show();
            }
        });
    }

    private static String textOf(EditText e){
        return e.getText()==null? "" : e.getText().toString().trim();
    }
    private static String itemOf(Spinner s){
        return s.getSelectedItem()==null? "" : s.getSelectedItem().toString();
    }
}