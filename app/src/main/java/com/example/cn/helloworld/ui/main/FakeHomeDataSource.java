package com.example.cn.helloworld.ui.main;

import com.example.cn.helloworld.R;

import java.util.Arrays;
import java.util.List;

/**
 * 暂时使用的假数据实现，便于后续替换为真实服务。
 */
public class FakeHomeDataSource implements HomeDataSource {

    @Override
    public List<HomeModels.BannerItem> loadBanners() {
        return Arrays.asList(
                new HomeModels.BannerItem("应援应景周", "每日签到领专属语音", R.color.homeBannerColor1),
                new HomeModels.BannerItem("新曲试听", "抢先体验未公开demo", R.color.homeBannerColor2),
                new HomeModels.BannerItem("线下活动报名", "一起去看演唱会直播", R.color.homeBannerColor3)
        );
    }

    @Override
    public List<HomeModels.HomeCategory> loadCategories() {
        return Arrays.asList(
                new HomeModels.HomeCategory("粉籍卡"),
                new HomeModels.HomeCategory("数据站"),
                new HomeModels.HomeCategory("打卡墙"),
                new HomeModels.HomeCategory("应援商城"),
                new HomeModels.HomeCategory("日程表"),
                new HomeModels.HomeCategory("更多")
        );
    }

    @Override
    public List<HomeModels.Playlist> loadPlaylists() {
        return Arrays.asList(
                new HomeModels.Playlist("舞台热力", "高燃舞台循环不止", R.color.homePlaylistPlaceholder),
                new HomeModels.Playlist("治愈轻声", "收藏他的温柔呢喃", R.color.homePlaylistColor1),
                new HomeModels.Playlist("巡演现场", "沉浸式回放应援瞬间", R.color.homePlaylistColor2)
        );
    }

    @Override
    public List<HomeModels.SupportTask> loadSupportTasks() {
        return Arrays.asList(
                new HomeModels.SupportTask("微博控评", "截至 20:00", "集合队伍，守护主话题热度。"),
                new HomeModels.SupportTask("QQ 音乐打榜", "本周目标：Top3", "集中打卡提高日播放量。"),
                new HomeModels.SupportTask("线下广告位", "报名截止：周五", "招募同城伙伴一起筹备生日灯箱。")
        );
    }
}
