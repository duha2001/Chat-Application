package com.example.d_five.model;

public class Contacts {
    private Long user_id;
    private Long user_contact_id;
    private String status;

    public Contacts(){};

    public Contacts(Long user_id, Long user_contact_id, String status) {
        this.user_id = user_id;
        this.user_contact_id = user_contact_id;
        this.status = status;
    }

    public Long getUser_id() {
        return user_id;
    }

    public void setUser_id(Long user_id) {
        this.user_id = user_id;
    }

    public Long getUser_contact_id() {
        return user_contact_id;
    }

    public void setUser_contact_id(Long user_contact_id) {
        this.user_contact_id = user_contact_id;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "Contacts{" +
                "user_id=" + user_id +
                ", user_contact_id=" + user_contact_id +
                ", status='" + status + '\'' +
                '}';
    }
}
