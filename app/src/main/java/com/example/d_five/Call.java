package com.example.d_five;

public class Call {
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getMore() {
        return more;
    }

    public void setMore(int more) {
        this.more = more;
    }

    public int getAvatar() {
        return avatar;
    }

    public void setAvatar(int avatar) {
        this.avatar = avatar;
    }

    public Call(int avatar, String name, String status, int more){
        this.avatar = avatar;
        this.name = name;
        this.status = status;
        this.more = more;
    }
    private String name;
    private String status;
    private int more;
    private int avatar;

}
