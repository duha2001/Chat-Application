package com.example.d_five.model;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Date;

public class Call {

    private Long id;
    private String status;
    private LocalDateTime created_at;
    private LocalDateTime end_time;
    private Long conversation_id;
    private Long user_id;
    private String type;
    private Long duration;

    public Call(){};

    public Call(Long id, String status, LocalDateTime created_at, LocalDateTime end_time, Long conversation_id, Long user_id, String type, Long duration) {
        this.id = id;
        this.status = status;
        this.created_at = created_at;
        this.end_time = end_time;
        this.conversation_id = conversation_id;
        this.user_id = user_id;
        this.type = type;
        this.duration = duration;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Long getDuration() {
        return duration;
    }

    public void setDuration(Long duration) {
        this.duration = duration;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getCreated_at() {
        return created_at;
    }

    public void setCreated_at(LocalDateTime created_at) {
        this.created_at = created_at;
    }

    public LocalDateTime getEnd_time() {
        return end_time;
    }

    public void setEnd_time(LocalDateTime end_time) {
        this.end_time = end_time;
    }

    public Long getConversation_id() {
        return conversation_id;
    }

    public void setConversation_id(Long conversation_id) {
        this.conversation_id = conversation_id;
    }

    public Long getUser_id() {
        return user_id;
    }

    public void setUser_id(Long user_id) {
        this.user_id = user_id;
    }
    @Override
    public String toString() {
        return "Call{" +
                "id=" + id +
                ", status='" + status + '\'' +
                ", created_at=" + created_at +
                ", end_time=" + end_time +
                ", conversation_id=" + conversation_id +
                ", user_id=" + user_id +
                '}';
    }
}
