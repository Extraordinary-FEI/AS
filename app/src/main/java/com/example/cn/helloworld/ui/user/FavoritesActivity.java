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
    private TextView overviewSubtitle;
    private TextView countSongs;
    private TextView countProducts;
    private TextView countTasks;
    private TextView clearSongsButton;
    private TextView clearProductsButton;
    private TextView clearTasksButton;

    private final List<Song> favoriteSongs = new ArrayList<Song>();
    private final List<Product> favoriteProducts = new ArrayList<Product>();
    private final List<HomeModels.SupportTask> favoriteTasks = new ArrayList<HomeModels.SupportTask>();

    private FavoriteSongAdapter songAdapter;
    private FavoriteProductAdapter productAdapter;
    private FavoriteTaskAdapter taskAdapter;

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
        overviewSubtitle = (TextView) findViewById(R.id.text_overview_subtitle);
        countSongs = (TextView) findViewById(R.id.text_count_songs);
        countProducts = (TextView) findViewById(R.id.text_count_products);
        countTasks = (TextView) findViewById(R.id.text_count_tasks);
        clearSongsButton = (TextView) findViewById(R.id.button_clear_songs);
        clearProductsButton = (TextView) findViewById(R.id.button_clear_products);
        clearTasksButton = (TextView) findViewById(R.id.button_clear_tasks);

        songList.setLayoutManager(new LinearLayoutManager(this));
        productList.setLayoutManager(new LinearLayoutManager(this));
        taskList.setLayoutManager(new LinearLayoutManager(this));

        songAdapter = new FavoriteSongAdapter(favoriteSongs, new FavoriteItemRemover<Song>() {
            @Override
            public void onRemove(Song item, int position) {
                removeSong(item, position);
            }
        });
        productAdapter = new FavoriteProductAdapter(favoriteProducts, new FavoriteItemRemover<Product>() {
            @Override
            public void onRemove(Product item, int position) {
                removeProduct(item, position);
            }
        });
        taskAdapter = new FavoriteTaskAdapter(favoriteTasks, new FavoriteItemRemover<HomeModels.SupportTask>() {
            @Override
            public void onRemove(HomeModels.SupportTask item, int position) {
                removeTask(item, position);
            }
        });

        songList.setAdapter(songAdapter);
        productList.setAdapter(productAdapter);
        taskList.setAdapter(taskAdapter);

        overviewSubtitle.setText(R.string.favorite_overview_subtitle);

        clearSongsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                favoriteRepository.clearSongs();
                favoriteSongs.clear();
                songAdapter.notifyDataSetChanged();
                updateEmptyStates();
            }
        });
        clearProductsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                favoriteRepository.clearProducts();
                favoriteProducts.clear();
                productAdapter.notifyDataSetChanged();
                updateEmptyStates();
            }
        });
        clearTasksButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                favoriteRepository.clearTasks();
                favoriteTasks.clear();
                taskAdapter.notifyDataSetChanged();
                updateEmptyStates();
            }
        });
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
        favoriteSongs.clear();
        List<Playlist> playlists = playlistRepository.getAllPlaylists();
        for (Playlist playlist : playlists) {
            if (playlist != null && playlist.getSongs() != null) {
                for (Song song : playlist.getSongs()) {
                    if (song != null && ids.contains(song.getId())) {
                        favoriteSongs.add(song);
                    }
                }
            }
        }
        songAdapter.notifyDataSetChanged();
        emptySongs.setVisibility(favoriteSongs.isEmpty() ? View.VISIBLE : View.GONE);
        updateCounters();
    }

    private void bindProducts() {
        List<String> ids = favoriteRepository.getFavoriteProducts();
        favoriteProducts.clear();
        List<Product> all = productRepository.getAll();
        for (Product product : all) {
            if (product != null && ids.contains(product.getId())) {
                favoriteProducts.add(product);
            }
        }
        productAdapter.notifyDataSetChanged();
        emptyProducts.setVisibility(favoriteProducts.isEmpty() ? View.VISIBLE : View.GONE);
        updateCounters();
    }

    private void bindTasks() {
        List<String> ids = favoriteRepository.getFavoriteTasks();
        favoriteTasks.clear();
        List<com.example.cn.helloworld.data.model.SupportTask> stored = supportTaskRepository.getAll();
        if (stored != null) {
            for (com.example.cn.helloworld.data.model.SupportTask task : stored) {
                if (task != null && ids.contains(task.getTaskId())) {
                    favoriteTasks.add(new HomeModels.SupportTask(
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
        taskAdapter.notifyDataSetChanged();
        emptyTasks.setVisibility(favoriteTasks.isEmpty() ? View.VISIBLE : View.GONE);
        updateCounters();
    }

    private void removeSong(Song song, int position) {
        favoriteRepository.setSongFavorite(song.getId(), false);
        favoriteSongs.remove(position);
        songAdapter.notifyItemRemoved(position);
        updateEmptyStates();
    }

    private void removeProduct(Product product, int position) {
        favoriteRepository.setProductFavorite(product.getId(), false);
        favoriteProducts.remove(position);
        productAdapter.notifyItemRemoved(position);
        updateEmptyStates();
    }

    private void removeTask(HomeModels.SupportTask task, int position) {
        favoriteRepository.setTaskFavorite(task.getId(), false);
        favoriteTasks.remove(position);
        taskAdapter.notifyItemRemoved(position);
        updateEmptyStates();
    }

    private void updateEmptyStates() {
        emptySongs.setVisibility(favoriteSongs.isEmpty() ? View.VISIBLE : View.GONE);
        emptyProducts.setVisibility(favoriteProducts.isEmpty() ? View.VISIBLE : View.GONE);
        emptyTasks.setVisibility(favoriteTasks.isEmpty() ? View.VISIBLE : View.GONE);
        updateCounters();
    }

    private void updateCounters() {
        countSongs.setText(getString(R.string.favorite_count_format, favoriteSongs.size()));
        countProducts.setText(getString(R.string.favorite_count_format, favoriteProducts.size()));
        countTasks.setText(getString(R.string.favorite_count_format, favoriteTasks.size()));
        int total = favoriteSongs.size() + favoriteProducts.size() + favoriteTasks.size();
        overviewSubtitle.setText(getString(R.string.favorite_overview_subtitle)
                + " · "
                + getString(R.string.favorite_count_format, total));
    }

    private HomeModels.SupportTask.EnrollmentState mapEnrollment(String taskId) {
        SupportTaskEnrollmentRepository.EnrollmentStatus stored =
                enrollmentRepository.getEnrollmentStatus(sessionManager.getUserId(), taskId);
        switch (stored) {
            case APPROVED:
                return HomeModels.SupportTask.EnrollmentState.APPROVED;
            case REJECTED:
                return HomeModels.SupportTask.EnrollmentState.REJECTED;
            case CANCELLED:
                return HomeModels.SupportTask.EnrollmentState.CANCELLED;
            case PENDING:
                return HomeModels.SupportTask.EnrollmentState.PENDING;
            case NOT_APPLIED:
            default:
                return HomeModels.SupportTask.EnrollmentState.NOT_APPLIED;
        }
    }
}

