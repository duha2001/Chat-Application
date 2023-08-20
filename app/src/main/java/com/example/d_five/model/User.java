package com.example.d_five.model;

import java.io.Serializable;

public class User implements Serializable {
    private Long id;
    private String username;
    private String avatar;
    private String domain;
    private String nickname;

    public User(){};
    public User(String username, String avatar) {
        this.username = username;
        this.avatar = avatar;
    }

    public User(String username) {
        this.username = username;
    }

    public User(String username, String avatar, String domain) {
        this.username = username;
        this.avatar = avatar;
        this.domain = domain;
    }

    public User(Long id, String username, String avatar) {
        this.id = id;
        this.username = username;
        this.avatar = avatar;
    }

    public User(Long id, String username, String avatar, String domain, String nickname) {
        this.id = id;
        this.username = username;
        this.avatar = avatar;
        this.domain = domain;
        this.nickname = nickname;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", avatar='" + avatar + '\'' +
                '}';
    }

}
