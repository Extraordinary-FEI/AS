package com.example.cn.helloworld.data.model;

import java.util.Collections;
import java.util.EnumSet;
import java.util.Set;

public class LoginResult {

    private final boolean success;
    private final String username;
    private final String token;
    private final UserRole role;
    private final Set<Permission> permissions;
    private final String message;

    public LoginResult(boolean success,
                       String username,
                       String token,
                       UserRole role,
                       Set<Permission> permissions,
                       String message) {
        this.success = success;
        this.username = username;
        this.token = token;
        this.role = role == null ? UserRole.USER : role;
        this.permissions = permissions == null
                ? EnumSet.noneOf(Permission.class)
                : EnumSet.copyOf(permissions);
        this.message = message;
    }

    public boolean isSuccess() {
        return success;
    }

    public String getUsername() {
        return username;
    }

    public String getToken() {
        return token;
    }

    public UserRole getRole() {
        return role;
    }

    public Set<Permission> getPermissions() {
        return Collections.unmodifiableSet(permissions);
    }

    public String getMessage() {
        return message;
    }
}
