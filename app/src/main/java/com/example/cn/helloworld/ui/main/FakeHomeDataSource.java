package com.example.cn.helloworld.ui.main;

import android.content.Context;

import com.example.cn.helloworld.R;
import com.example.cn.helloworld.data.model.Playlist;
import com.example.cn.helloworld.data.model.Product;
import com.example.cn.helloworld.data.playlist.PlaylistRepository;
import com.example.cn.helloworld.data.repository.ProductRepository;
import com.example.cn.helloworld.data.repository.support.SupportTaskRepository;

import java.util.Arrays;
import java.util.ArrayList;
import java.util.List;

/**
 * 暂时使用的假数据实现，便于后续替换为真实服务。
 */
public class FakeHomeDataSource implements HomeDataSource {

    private final Context context;
    private final PlaylistRepository playlistRepository;
    private final SupportTaskRepository supportTaskRepository = new SupportTaskRepository();
    private final ProductRepository productRepository;

    /**
     * 正确写法：
     * - 必须依赖 Context，否则 PlaylistRepository.getInstance() 会抛 IllegalStateException
     * - 使用 applicationContext 避免 Activity 泄漏
     */
    public FakeHomeDataSource(Context context) {
        if (context == null) {
            throw new IllegalArgumentException("FakeHomeDataSource requires non-null Context");
        }
        this.context = context.getApplicationContext();
        playlistRepository = PlaylistRepository.getInstance(this.context);
        productRepository = new ProductRepository(this.context);
    }
    @Override
    public List<HomeModels.BannerItem> loadBanners() {
        return Arrays.asList(
                new HomeModels.BannerItem("千玺生日月冲刺", "每日打卡累计生贺能量", R.drawable.cover_nishuo),
                new HomeModels.BannerItem("公益舞台回顾", "重温他与山城孩子的约定", R.drawable.cover_baobei),
                new HomeModels.BannerItem("线下巡礼报名", "和小橙灯一起打卡地标应援点", R.drawable.song_cover)
        );
    }

    @Override
    public List<HomeModels.HomeCategory> loadCategories() {
        return Arrays.asList(
                new HomeModels.HomeCategory(
                        context.getString(R.string.home_category_ticket),
                        context.getString(R.string.category_subtitle_ticket),
                        R.drawable.ic_category_ticket,
                        "action_stage_review"
                ),
                new HomeModels.HomeCategory(
                        context.getString(R.string.home_category_merch),
                        context.getString(R.string.category_subtitle_merch),
                        R.drawable.ic_category_merch,
                        "action_new_arrival"
                ),
                new HomeModels.HomeCategory(
                        context.getString(R.string.home_category_support),
                        context.getString(R.string.home_task_subtitle),
                        R.drawable.ic_category_signed,
                        "action_calendar"
                ),
                new HomeModels.HomeCategory(
                        context.getString(R.string.home_category_playlist),
                        context.getString(R.string.home_playlist_subtitle),
                        R.drawable.ic_category_signed,
                        "action_review_wall"
                ),
                new HomeModels.HomeCategory(
                        context.getString(R.string.home_category_cart),
                        context.getString(R.string.category_subtitle_signed),
                        R.drawable.ic_category_ticket,
                        "action_news"
                ),
                new HomeModels.HomeCategory(
                        context.getString(R.string.home_category_profile),
                        context.getString(R.string.home_task_subtitle),
                        R.drawable.ic_category_merch,
                        "action_profile"
                )
        );
    }

    /**
     * ✔ 修复点：使用 PlaylistRepository.getAllPlaylists()
     *   （你当前项目里 PlaylistRepository 只有这个方法）
     */
    @Override
    public List<Playlist> loadPlaylists() {
        return playlistRepository.getAllPlaylists();
    }

    @Override
    public List<HomeModels.SupportTask> loadSupportTasks() {
        return supportTaskRepository.getSupportTasks();
    }

    @Override
    public List<Product> loadFeaturedProducts() {
        List<Product> activeProducts = new ArrayList<Product>();
        List<Product> all = productRepository.getAll();
        for (int i = 0; i < all.size(); i++) {
            Product product = all.get(i);
            if (product != null && product.isActive()) {
                activeProducts.add(product);
            }
            if (activeProducts.size() >= 6) {
                break;
            }
        }
        return activeProducts;
    }
}
