package com.example.cn.helloworld.ui.main;

import com.example.cn.helloworld.data.model.Playlist;

import java.util.List;

/**
 * 提供首页数据入口，后续可以替换为真实接口或数据库实现。
 */
public interface HomeDataSource {

    List<HomeModels.BannerItem> loadBanners();

    List<HomeModels.HomeCategory> loadCategories();

    List<Playlist> loadPlaylists();

    List<HomeModels.SupportTask> loadSupportTasks();
}
