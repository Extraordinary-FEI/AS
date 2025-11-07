package com.example.cn.helloworld.data.model;

public class LoginResult {

    private final boolean success;
    private final String username;
    private final String token;
    private final String role;
    private final String permissions;
    private final String message;

    public LoginResult(boolean success, String username, String token, String role, String permissions, String message) {
        this.success = success;
        this.username = username;
        this.token = token;
        this.role = role;
        this.permissions = permissions;
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

    public String getRole() {
        return role;
    }

    public String getPermissions() {
        return permissions;
    }

    public String getMessage() {
        return message;
    }
}
