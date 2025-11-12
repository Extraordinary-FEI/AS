package com.example.cn.helloworld.ui.main;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 轻量模型定义，便于首页模块之间共享。
 */
public final class HomeModels {

    private HomeModels() {
        // no-op
    }

    public static class BannerItem {
        private final String title;
        private final String description;
        private final int backgroundColorResId;

        public BannerItem(String title, String description, int backgroundColorResId) {
            this.title = title;
            this.description = description;
            this.backgroundColorResId = backgroundColorResId;
        }

        public String getTitle() {
            return title;
        }

        public String getDescription() {
            return description;
        }

        public int getBackgroundColorResId() {
            return backgroundColorResId;
        }
    }

    public static class HomeCategory {
        private final String name;

        public HomeCategory(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }

    public static class Playlist {
        private final String id;
        private final String name;
        private final String description;
        private final int coverColorResId;
        private final List<String> tags;
        private final long playCount;
        private final long favoriteCount;
        private final int trackCount;

        public Playlist(String id,
                        String name,
                        String description,
                        int coverColorResId,
                        List<String> tags,
                        long playCount,
                        long favoriteCount,
                        int trackCount) {
            this.id = id;
            this.name = name;
            this.description = description;
            this.coverColorResId = coverColorResId;
            this.tags = tags == null ? Collections.<String>emptyList() : new ArrayList<String>(tags);
            this.playCount = playCount;
            this.favoriteCount = favoriteCount;
            this.trackCount = trackCount;
        }

        public String getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        public String getDescription() {
            return description;
        }

        public int getCoverColorResId() {
            return coverColorResId;
        }

        public List<String> getTags() {
            return new ArrayList<String>(tags);
        }

        public long getPlayCount() {
            return playCount;
        }

        public long getFavoriteCount() {
            return favoriteCount;
        }

        public int getTrackCount() {
            return trackCount;
        }
    }

    public static class SupportTask {
        private final String name;
        private final String deadline;
        private final String description;

        public SupportTask(String name, String deadline, String description) {
            this.name = name;
            this.deadline = deadline;
            this.description = description;
        }

        public String getName() {
            return name;
        }

        public String getDeadline() {
            return deadline;
        }

        public String getDescription() {
            return description;
        }
    }
}
