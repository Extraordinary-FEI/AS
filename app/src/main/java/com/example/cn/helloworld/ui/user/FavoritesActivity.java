package com.example.cn.helloworld.ui.user;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.example.cn.helloworld.R;
import com.example.cn.helloworld.data.model.Playlist;
import com.example.cn.helloworld.data.model.Product;
import com.example.cn.helloworld.data.model.Song;
import com.example.cn.helloworld.data.repository.FavoriteRepository;
import com.example.cn.helloworld.data.repository.ProductRepository;
import com.example.cn.helloworld.data.repository.SupportTaskEnrollmentRepository;
import com.example.cn.helloworld.data.repository.SupportTaskRepository;
import com.example.cn.helloworld.data.playlist.PlaylistRepository;
import com.example.cn.helloworld.data.session.SessionManager;
import com.example.cn.helloworld.ui.main.HomeModels;

import java.util.ArrayList;
import java.util.List;

/**
 * 用户收藏汇总页：展示歌曲、商品和应援任务的收藏。
 */
public class FavoritesActivity extends AppCompatActivity {

    private FavoriteRepository favoriteRepository;
    private PlaylistRepository playlistRepository;
    private ProductRepository productRepository;
    private SupportTaskRepository supportTaskRepository;
    private SupportTaskEnrollmentRepository enrollmentRepository;
    private SessionManager sessionManager;

    private RecyclerView songList;
    private RecyclerView productList;
    private RecyclerView taskList;
    private TextView emptySongs;
    private TextView emptyProducts;
    private TextView emptyTasks;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorites);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle(R.string.user_action_favorites);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        favoriteRepository = new FavoriteRepository(this);
        playlistRepository = PlaylistRepository.getInstance(this);
        productRepository = new ProductRepository(this);
        supportTaskRepository = new SupportTaskRepository(this);
        enrollmentRepository = new SupportTaskEnrollmentRepository(this);
        sessionManager = new SessionManager(this);

        songList = (RecyclerView) findViewById(R.id.recycler_fav_songs);
        productList = (RecyclerView) findViewById(R.id.recycler_fav_products);
        taskList = (RecyclerView) findViewById(R.id.recycler_fav_tasks);
        emptySongs = (TextView) findViewById(R.id.text_empty_songs);
        emptyProducts = (TextView) findViewById(R.id.text_empty_products);
        emptyTasks = (TextView) findViewById(R.id.text_empty_tasks);

        songList.setLayoutManager(new LinearLayoutManager(this));
        productList.setLayoutManager(new LinearLayoutManager(this));
        taskList.setLayoutManager(new LinearLayoutManager(this));
    }

    @Override
    protected void onResume() {
        super.onResume();
        bindSongs();
        bindProducts();
        bindTasks();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void bindSongs() {
        List<String> ids = favoriteRepository.getFavoriteSongs();
        List<Song> songs = new ArrayList<Song>();
        List<Playlist> playlists = playlistRepository.getAllPlaylists();
        for (Playlist playlist : playlists) {
            if (playlist != null && playlist.getSongs() != null) {
                for (Song song : playlist.getSongs()) {
                    if (song != null && ids.contains(song.getId())) {
                        songs.add(song);
                    }
                }
            }
        }
        songList.setAdapter(new FavoriteSongAdapter(songs));
        emptySongs.setVisibility(songs.isEmpty() ? View.VISIBLE : View.GONE);
    }

    private void bindProducts() {
        List<String> ids = favoriteRepository.getFavoriteProducts();
        List<Product> products = new ArrayList<Product>();
        List<Product> all = productRepository.getAll();
        for (Product product : all) {
            if (product != null && ids.contains(product.getId())) {
                products.add(product);
            }
        }
        productList.setAdapter(new FavoriteProductAdapter(products));
        emptyProducts.setVisibility(products.isEmpty() ? View.VISIBLE : View.GONE);
    }

    private void bindTasks() {
        List<String> ids = favoriteRepository.getFavoriteTasks();
        List<HomeModels.SupportTask> tasks = new ArrayList<HomeModels.SupportTask>();
        List<com.example.cn.helloworld.data.model.SupportTask> stored = supportTaskRepository.getAll();
        if (stored != null) {
            for (com.example.cn.helloworld.data.model.SupportTask task : stored) {
                if (task != null && ids.contains(task.getTaskId())) {
                    tasks.add(new HomeModels.SupportTask(
                            task.getTaskId(),
                            task.getTitle(),
                            getString(R.string.support_task_type_admin),
                            "",
                            getString(R.string.support_task_location_default),
                            task.getDescription(),
                            getString(R.string.support_task_admin_guide),
                            task.getAssignedAdmin(),
                            task.getDescription(),
                            100,
                            task.getPriority(),
                            HomeModels.SupportTask.TaskStatus.ONGOING,
                            HomeModels.SupportTask.RegistrationStatus.OPEN,
                            mapEnrollment(task.getTaskId())
                    ));
                }
            }
        }
        taskList.setAdapter(new FavoriteTaskAdapter(tasks));
        emptyTasks.setVisibility(tasks.isEmpty() ? View.VISIBLE : View.GONE);
    }

    private HomeModels.SupportTask.EnrollmentState mapEnrollment(String taskId) {
        SupportTaskEnrollmentRepository.EnrollmentStatus stored =
                enrollmentRepository.getEnrollmentStatus(sessionManager.getUserId(), taskId);
        switch (stored) {
            case APPROVED:
                return HomeModels.SupportTask.EnrollmentState.APPROVED;
            case REJECTED:
                return HomeModels.SupportTask.EnrollmentState.REJECTED;
            case PENDING:
                return HomeModels.SupportTask.EnrollmentState.PENDING;
            case NOT_APPLIED:
            default:
                return HomeModels.SupportTask.EnrollmentState.NOT_APPLIED;
        }
    }
}

