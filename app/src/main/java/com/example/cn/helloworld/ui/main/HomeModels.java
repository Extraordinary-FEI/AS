package com.example.cn.helloworld.ui.main;

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
        private final String name;
        private final String description;
        private final int coverColorResId;

        public Playlist(String name, String description, int coverColorResId) {
            this.name = name;
            this.description = description;
            this.coverColorResId = coverColorResId;
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
