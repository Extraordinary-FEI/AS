package com.example.cn.helloworld.ui.user;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.example.cn.helloworld.R;
import com.example.cn.helloworld.ui.admin.SupportTaskManagementActivity;
import com.example.cn.helloworld.ui.main.PlaylistOverviewActivity;

/**
 * Model for a user configurable quick action entry.
 */
public class QuickAction {

    public enum Type {
        PLAYLISTS("playlists", R.string.quick_action_type_playlists, R.drawable.ic_category_music) {
            @Override
            public Intent buildIntent(Context context) {
                return new Intent(context, PlaylistOverviewActivity.class);
            }
        },
        PROFILE("profile", R.string.quick_action_type_profile, R.drawable.ic_user_default) {
            @Override
            public Intent buildIntent(Context context) {
                return new Intent(context, UserProfileActivity.class);
            }
        },
        SUPPORT("support", R.string.quick_action_type_support, R.drawable.ic_category_task) {
            @Override
            public Intent buildIntent(Context context) {
                return new Intent(context, SupportTaskManagementActivity.class);
            }
        },
        NONE("none", R.string.quick_action_type_none, R.drawable.ic_category_signed) {
            @Override
            public Intent buildIntent(Context context) {
                return null;
            }
        };

        private final String id;
        private final int displayNameRes;
        private final int iconRes;

        Type(String id, int displayNameRes, int iconRes) {
            this.id = id;
            this.displayNameRes = displayNameRes;
            this.iconRes = iconRes;
        }

        public String getId() {
            return id;
        }

        public int getDisplayNameRes() {
            return displayNameRes;
        }

        @DrawableRes
        public int getIconRes() {
            return iconRes;
        }

        @Nullable
        public Intent buildIntent(Context context) {
            return null;
        }

        @NonNull
        public static Type fromId(String id) {
            for (Type type : values()) {
                if (type.id.equals(id)) {
                    return type;
                }
            }
            return NONE;
        }
    }

    private final String id;
    private final Type type;
    private final String title;
    private final String description;
    private final int iconRes;

    public QuickAction(String id, Type type, String title, String description, int iconRes) {
        this.id = id;
        this.type = type;
        this.title = title;
        this.description = description;
        this.iconRes = iconRes;
    }

    public String getId() {
        return id;
    }

    public Type getType() {
        return type;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public int getIconRes() {
        return iconRes;
    }

    public QuickAction withTitle(String newTitle) {
        return new QuickAction(id, type, newTitle, description, iconRes);
    }

    public QuickAction withDescription(String newDescription) {
        return new QuickAction(id, type, title, newDescription, iconRes);
    }

    public QuickAction withType(Type newType) {
        return new QuickAction(id, newType, title, description, newType.getIconRes());
    }

    public QuickAction withContent(String newTitle, String newDescription, Type newType) {
        return new QuickAction(id, newType, newTitle, newDescription, newType.getIconRes());
    }
}
