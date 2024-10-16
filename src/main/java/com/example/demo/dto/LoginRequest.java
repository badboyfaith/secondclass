package com.example.demo.dto;

public class LoginRequest {

    private String username;
    private String password;

    // 默认构造方法
    public LoginRequest() {}

    // 带参数的构造方法
    public LoginRequest(String username, String password) {
        this.username = username;
        this.password = password;
    }

    // Getter 和 Setter 方法
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}

