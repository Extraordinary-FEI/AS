package com.example.cn.helloworld.data.model;

/**
 * 定义后台可用的权限点。
 */
public enum Permission {
    MANAGE_PRODUCTS,
    MANAGE_PLAYLISTS,
    APPROVE_SUPPORT_TASKS,
    VIEW_ANALYTICS;

    public static Permission fromValue(String value) {
        if (value == null) {
            return null;
        }
        try {
            return Permission.valueOf(value.toUpperCase());
        } catch (IllegalArgumentException exception) {
            return null;
        }
    }
}
