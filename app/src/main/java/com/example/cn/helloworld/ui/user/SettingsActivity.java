package com.example.cn.helloworld.ui.user;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.widget.CompoundButton;
import android.widget.Toast;

import com.example.cn.helloworld.R;

public class SettingsActivity extends AppCompatActivity {

    private static final String PREFS_NAME = "user_settings";
    private static final String KEY_NOTIFICATION = "notification_enabled";
    private static final String KEY_WIFI_ONLY = "wifi_only";
    private static final String KEY_PRIVACY_MODE = "privacy_mode";

    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(R.string.user_action_settings);
        }

        sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);

        SwitchCompat switchNotification = (SwitchCompat) findViewById(R.id.switch_notification);
        SwitchCompat switchWifiOnly = (SwitchCompat) findViewById(R.id.switch_wifi_only);
        SwitchCompat switchPrivacy = (SwitchCompat) findViewById(R.id.switch_privacy);

        bindSwitch(switchNotification, KEY_NOTIFICATION, true);
        bindSwitch(switchWifiOnly, KEY_WIFI_ONLY, true);
        bindSwitch(switchPrivacy, KEY_PRIVACY_MODE, false);
    }

    private void bindSwitch(SwitchCompat switchCompat, final String key, boolean defaultValue) {
        if (switchCompat == null) {
            return;
        }
        boolean current = sharedPreferences.getBoolean(key, defaultValue);
        switchCompat.setChecked(current);
        switchCompat.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                sharedPreferences.edit().putBoolean(key, isChecked).apply();
                Toast.makeText(SettingsActivity.this, R.string.settings_saved, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}

