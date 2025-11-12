package com.example.cn.helloworld.data.model;

/**
 * 系统支持的用户角色。
 */
public enum UserRole {
    ADMIN,
    USER;

    public static UserRole fromValue(String value) {
        if (value == null) {
            return USER;
        }
        try {
            return UserRole.valueOf(value.toUpperCase());
        } catch (IllegalArgumentException exception) {
            return USER;
        }
    }
}
