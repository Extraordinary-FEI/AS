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
        private final String tag;
        private final String cta;
        private final int imageResId;

        public BannerItem(String title, String description, int imageResId, String tag, String cta) {
            this.title = title;
            this.description = description;
            this.imageResId = imageResId;
            this.tag = tag;
            this.cta = cta;
        }

        public String getTitle() {
            return title;
        }

        public String getDescription() {
            return description;
        }

        public int getImageResId() {
            return imageResId;
        }

        public String getTag() {
            return tag;
        }

        public String getCta() {
            return cta;
        }
    }

    public static class HomeCategory {
        private final String name;
        private final String subtitle;
        private final int iconResId;
        private final String action;

        public HomeCategory(String name, String subtitle, int iconResId, String action) {
            this.name = name;
            this.subtitle = subtitle;
            this.iconResId = iconResId;
            this.action = action;
        }

        public String getName() {
            return name;
        }

        public String getSubtitle() {
            return subtitle;
        }

        public int getIconResId() {
            return iconResId;
        }

        public String getAction() {
            return action;
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

    public static class SupportTask implements java.io.Serializable {

        private static final long serialVersionUID = 1L;

        public enum TaskStatus {
            UPCOMING,
            ONGOING,
            COMPLETED
        }

        public enum RegistrationStatus {
            NOT_OPEN,
            OPEN,
            FULL,
            CHECK_IN,
            CLOSED
        }

        public enum EnrollmentState {
            NOT_APPLIED,
            PENDING,
            APPROVED,
            REJECTED
        }

        private final String id;
        private final String name;
        private final String taskType;
        private final String timeRange;
        private final String location;
        private final String description;
        private final String guide;
        private final String contact;
        private final String progressNote;
        private final int maxParticipants;
        private final int enrolledCount;
        private final TaskStatus status;
        private final RegistrationStatus registrationStatus;
        private final EnrollmentState enrollmentState;

        public SupportTask(String id,
                           String name,
                           String taskType,
                           String timeRange,
                           String location,
                           String description,
                           String guide,
                           String contact,
                           String progressNote,
                           int maxParticipants,
                           int enrolledCount,
                           TaskStatus status,
                           RegistrationStatus registrationStatus) {
            this(id, name, taskType, timeRange, location, description, guide, contact,
                    progressNote, maxParticipants, enrolledCount, status, registrationStatus,
                    EnrollmentState.NOT_APPLIED);
        }

        public SupportTask(String id,
                           String name,
                           String taskType,
                           String timeRange,
                           String location,
                           String description,
                           String guide,
                           String contact,
                           String progressNote,
                           int maxParticipants,
                           int enrolledCount,
                           TaskStatus status,
                           RegistrationStatus registrationStatus,
                           EnrollmentState enrollmentState) {
            this.id = id;
            this.name = name;
            this.taskType = taskType;
            this.timeRange = timeRange;
            this.location = location;
            this.description = description;
            this.guide = guide;
            this.contact = contact;
            this.progressNote = progressNote;
            this.maxParticipants = maxParticipants;
            this.enrolledCount = enrolledCount;
            this.status = status;
            this.registrationStatus = registrationStatus;
            this.enrollmentState = enrollmentState == null
                    ? EnrollmentState.NOT_APPLIED
                    : enrollmentState;
        }

        public String getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        public String getTaskType() {
            return taskType;
        }

        public String getTimeRange() {
            return timeRange;
        }

        public String getLocation() {
            return location;
        }

        public String getDescription() {
            return description;
        }

        public String getGuide() {
            return guide;
        }

        public String getContact() {
            return contact;
        }

        public String getProgressNote() {
            return progressNote;
        }

        public int getMaxParticipants() {
            return maxParticipants;
        }

        public int getEnrolledCount() {
            return enrolledCount;
        }

        public TaskStatus getStatus() {
            return status;
        }

        public RegistrationStatus getRegistrationStatus() {
            return registrationStatus;
        }

        public EnrollmentState getEnrollmentState() {
            return enrollmentState;
        }

        public int getProgressPercent() {
            if (maxParticipants <= 0) {
                return 0;
            }
            return (int) Math.min(100, Math.round((enrolledCount * 100f) / maxParticipants));
        }

        public boolean isRegistrationOpen() {
            return registrationStatus == RegistrationStatus.OPEN;
        }

        public boolean isCheckInAvailable() {
            return registrationStatus == RegistrationStatus.CHECK_IN;
        }

        public boolean isActionEnabled() {
            return status != TaskStatus.COMPLETED && (isRegistrationOpen() || isCheckInAvailable());
        }
    }
}
