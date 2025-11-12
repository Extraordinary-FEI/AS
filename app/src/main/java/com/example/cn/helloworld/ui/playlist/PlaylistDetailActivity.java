package com.example.cn.helloworld.ui.playlist;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.example.cn.helloworld.R;

/**
 * 歌单详情页宿主 Activity，承载 PlaylistFragment。
 */
public class PlaylistDetailActivity extends AppCompatActivity {

    private static final String EXTRA_PLAYLIST_ID = "extra_playlist_id";

    public static Intent createIntent(Context context, String playlistId) {
        Intent intent = new Intent(context, PlaylistDetailActivity.class);
        intent.putExtra(EXTRA_PLAYLIST_ID, playlistId);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playlist_detail);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(R.string.title_playlist_detail);
        }
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        String playlistId = getIntent().getStringExtra(EXTRA_PLAYLIST_ID);
        if (playlistId == null) {
            finish();
            return;
        }

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.playlist_fragment_container, PlaylistFragment.newInstance(playlistId))
                    .commit();
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
