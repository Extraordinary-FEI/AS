package com.example.cn.helloworld.ui.admin;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.widget.TextView;

import com.example.cn.helloworld.R;

public class AdminDashboardActivity extends AppCompatActivity {

    private StatsBarView statsBarView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_dashboard);
        setTitle(R.string.title_admin_dashboard);

        statsBarView = (StatsBarView) findViewById(R.id.stats_bar_view);
        CardView statsCard = (CardView) findViewById(R.id.card_stats);
        TextView summaryText = (TextView) findViewById(R.id.text_dashboard_summary);

        float[] values = new float[]{120f, 80f, 45f, 30f};
        String[] labels = new String[]{getString(R.string.stat_label_order),
                getString(R.string.stat_label_support),
                getString(R.string.stat_label_new_user),
                getString(R.string.stat_label_review)};
        statsBarView.setData(values, labels);

        summaryText.setText(getString(R.string.dashboard_summary,
                (int) values[0], (int) values[1], (int) values[2]));
        statsCard.setContentDescription(getString(R.string.dashboard_chart_description));
    }
}
