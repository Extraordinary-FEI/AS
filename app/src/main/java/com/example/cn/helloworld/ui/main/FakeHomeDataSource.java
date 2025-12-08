package com.example.cn.helloworld.ui.main;

import android.content.Context;

import com.example.cn.helloworld.R;
import com.example.cn.helloworld.data.model.Banner;
import com.example.cn.helloworld.data.model.Playlist;
import com.example.cn.helloworld.data.model.Product;
import com.example.cn.helloworld.data.model.SupportTask;
import com.example.cn.helloworld.data.playlist.PlaylistRepository;
import com.example.cn.helloworld.data.repository.BannerRepository;
import com.example.cn.helloworld.data.repository.ProductRepository;
import com.example.cn.helloworld.data.repository.SupportTaskEnrollmentRepository;
import com.example.cn.helloworld.data.repository.support.LocalSupportTaskDataSource;
import com.example.cn.helloworld.data.session.SessionManager;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * 暂时使用的假数据实现，便于后续替换为真实服务。
 */
public class FakeHomeDataSource implements HomeDataSource {

    private final Context context;
    private final PlaylistRepository playlistRepository;
    private final com.example.cn.helloworld.data.repository.SupportTaskRepository adminSupportTaskRepository;
    private final ProductRepository productRepository;
    private final BannerRepository bannerRepository;
    private final SupportTaskEnrollmentRepository enrollmentRepository;
    private final SessionManager sessionManager;
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("MM-dd HH:mm", Locale.getDefault());

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
        adminSupportTaskRepository = new com.example.cn.helloworld.data.repository.SupportTaskRepository(this.context);
        productRepository = new ProductRepository(this.context);
        bannerRepository = new BannerRepository(this.context);
        enrollmentRepository = new SupportTaskEnrollmentRepository(this.context);
        sessionManager = new SessionManager(this.context);
    }
    @Override
    public List<HomeModels.BannerItem> loadBanners() {
        List<HomeModels.BannerItem> bannerItems = new ArrayList<HomeModels.BannerItem>();
        List<Banner> storedBanners = bannerRepository.getAllBanners();
        if (storedBanners != null) {
            for (int i = 0; i < storedBanners.size(); i++) {
                Banner banner = storedBanners.get(i);
                if (banner != null) {
                    bannerItems.add(new HomeModels.BannerItem(
                            banner.getTitle(),
                            banner.getDescription(),
                            banner.getImageResId()
                    ));
                }
            }
        }
        return bannerItems;
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
                        R.drawable.ic_category_task,
                        "action_calendar"
                ),
                new HomeModels.HomeCategory(
                        context.getString(R.string.home_category_playlist),
                        context.getString(R.string.home_playlist_subtitle),
                        R.drawable.ic_category_music,
                        "action_review_wall"
                ),
                new HomeModels.HomeCategory(
                        context.getString(R.string.home_category_cart),
                        context.getString(R.string.category_subtitle_signed),
                        R.drawable.ic_category_signed,
                        "action_news"
                ),
                new HomeModels.HomeCategory(
                        context.getString(R.string.home_category_profile),
                        context.getString(R.string.home_task_subtitle),
                        R.drawable.ic_category_task,
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
        return adaptSupportTasks();
    }

    @Override
    public List<Product> loadFeaturedProducts() {
        List<Product> activeProducts = new ArrayList<Product>();
        List<Product> all = productRepository.getAll();
        for (int i = 0; i < all.size(); i++) {
            Product product = all.get(i);
            if (product != null && product.isActive() && product.isFeaturedOnHome()) {
                activeProducts.add(product);
            }
            if (activeProducts.size() >= 6) {
                break;
            }
        }
        return activeProducts;
    }

    private List<HomeModels.SupportTask> adaptSupportTasks() {
        List<HomeModels.SupportTask> tasks = new ArrayList<HomeModels.SupportTask>();

        List<SupportTask> adminTasks = adminSupportTaskRepository.getAll();
        if (adminTasks != null) {
            for (int i = 0; i < adminTasks.size(); i++) {
                SupportTask task = adminTasks.get(i);
                if (task != null) {
                    tasks.add(toHomeTask(task));
                }
            }
        }

        // 如果管理员端暂时没有数据，继续显示内置示例，避免用户界面为空
        if (tasks.isEmpty()) {
            LocalSupportTaskDataSource local = new LocalSupportTaskDataSource();
            tasks.addAll(local.getSupportTasks());
        }

        return tasks;
    }

    private HomeModels.SupportTask toHomeTask(SupportTask task) {
        int maxParticipants = 100;
        int enrolled = task.getPriority();
        if (enrolled < 0) {
            enrolled = 0;
        }
        if (enrolled > maxParticipants) {
            enrolled = maxParticipants;
        }

        return new HomeModels.SupportTask(
                task.getTaskId(),
                task.getTitle(),
                context.getString(R.string.support_task_type_admin),
                formatTimeRange(task.getCreatedAt(), task.getUpdatedAt()),
                context.getString(R.string.support_task_location_default),
                task.getDescription(),
                context.getString(R.string.support_task_admin_guide),
                buildContact(task),
                buildProgressNote(task),
                maxParticipants,
                enrolled,
                mapTaskStatus(task.getStatus()),
                mapRegistrationStatus(task.getStatus()),
                mapEnrollmentState(task.getTaskId())
        );
    }

    private String buildContact(SupportTask task) {
        if (task.getAssignedAdmin() == null || task.getAssignedAdmin().trim().isEmpty()) {
            return context.getString(R.string.support_task_unassigned);
        }
        return context.getString(R.string.support_task_assigned_format, task.getAssignedAdmin());
    }

    private String buildProgressNote(SupportTask task) {
        return context.getString(R.string.support_task_progress_note_format,
                getReadableStatus(task.getStatus()),
                task.getPriority());
    }

    private String formatTimeRange(long createdAt, long updatedAt) {
        return dateFormat.format(new Date(createdAt)) + " - " + dateFormat.format(new Date(updatedAt));
    }

    private String getReadableStatus(String status) {
        if (com.example.cn.helloworld.data.repository.SupportTaskRepository.STATUS_APPROVED.equals(status)) {
            return context.getString(R.string.support_task_status_approved);
        }
        if (com.example.cn.helloworld.data.repository.SupportTaskRepository.STATUS_REJECTED.equals(status)) {
            return context.getString(R.string.support_task_status_rejected);
        }
        return context.getString(R.string.support_task_status_pending);
    }

    private HomeModels.SupportTask.TaskStatus mapTaskStatus(String status) {
        if (com.example.cn.helloworld.data.repository.SupportTaskRepository.STATUS_APPROVED.equals(status)) {
            return HomeModels.SupportTask.TaskStatus.ONGOING;
        }
        if (com.example.cn.helloworld.data.repository.SupportTaskRepository.STATUS_REJECTED.equals(status)) {
            return HomeModels.SupportTask.TaskStatus.COMPLETED;
        }
        return HomeModels.SupportTask.TaskStatus.UPCOMING;
    }

    private HomeModels.SupportTask.RegistrationStatus mapRegistrationStatus(String status) {
        if (com.example.cn.helloworld.data.repository.SupportTaskRepository.STATUS_REJECTED.equals(status)) {
            return HomeModels.SupportTask.RegistrationStatus.CLOSED;
        }
        return HomeModels.SupportTask.RegistrationStatus.OPEN;
    }

    private HomeModels.SupportTask.EnrollmentState mapEnrollmentState(String taskId) {
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
