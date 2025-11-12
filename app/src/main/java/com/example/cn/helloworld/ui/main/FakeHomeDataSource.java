package com.example.cn.helloworld.ui.main;

import com.example.cn.helloworld.R;
import com.example.cn.helloworld.data.playlist.PlaylistRepository;
import com.example.cn.helloworld.data.repository.support.SupportTaskRepository;

import java.util.Arrays;
import java.util.List;

/**
 * 暂时使用的假数据实现，便于后续替换为真实服务。
 */
public class FakeHomeDataSource implements HomeDataSource {

    private final PlaylistRepository playlistRepository = PlaylistRepository.getInstance();
    private final SupportTaskRepository supportTaskRepository = new SupportTaskRepository();

    @Override
    public List<HomeModels.BannerItem> loadBanners() {
        return Arrays.asList(
                new HomeModels.BannerItem("千玺生日月冲刺", "每日打卡累计生贺能量", R.color.homeBannerColor1),
                new HomeModels.BannerItem("公益舞台回顾", "重温他与山城孩子的约定", R.color.homeBannerColor2),
                new HomeModels.BannerItem("线下巡礼报名", "和小橙灯一起打卡地标应援点", R.color.homeBannerColor3)
        );
    }

    @Override
    public List<HomeModels.HomeCategory> loadCategories() {
        return Arrays.asList(
                new HomeModels.HomeCategory("粉籍卡"),
                new HomeModels.HomeCategory("数据站"),
                new HomeModels.HomeCategory("打卡墙"),
                new HomeModels.HomeCategory("应援商城"),
                new HomeModels.HomeCategory("易烊千玺歌单"),
                new HomeModels.HomeCategory("日程表")
        );
    }

    @Override
    public List<HomeModels.Playlist> loadPlaylists() {
        return playlistRepository.getHomeSummaries();
    }

    @Override
    public List<HomeModels.SupportTask> loadSupportTasks() {
        return supportTaskRepository.getSupportTasks();
    }
}
