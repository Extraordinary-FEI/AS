package com.example.cn.helloworld.ui.main;

import java.util.List;

/**
 * 提供首页数据入口，后续可以替换为真实接口或数据库实现。
 */
public interface HomeDataSource {

    List<HomeModels.BannerItem> loadBanners();

    List<HomeModels.HomeCategory> loadCategories();

    List<HomeModels.Playlist> loadPlaylists();

    List<HomeModels.SupportTask> loadSupportTasks();
}
