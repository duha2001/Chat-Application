package com.example.d_five.model;

import java.time.LocalDateTime;
import java.util.Date;

public class Participant {
    private LocalDateTime messages_read_at;
    private Long user_id;
    private Long conversation_id;
    private Boolean is_read;


    public Participant(){};

    public Participant(LocalDateTime messages_read_at, Long user_id, Long conversation_id, Boolean is_read) {
        this.messages_read_at = messages_read_at;
        this.user_id = user_id;
        this.conversation_id = conversation_id;
        this.is_read = is_read;
    }

    public Boolean getIs_read() {
        return is_read;
    }

    public void setIs_read(Boolean is_read) {
        this.is_read = is_read;
    }

    public LocalDateTime getMessages_read_at() {
        return messages_read_at;
    }

    public void setMessages_read_at(LocalDateTime messages_read_at) {
        this.messages_read_at = messages_read_at;
    }

    public Long getUser_id() {
        return user_id;
    }

    public void setUser_id(Long user_id) {
        this.user_id = user_id;
    }

    public Long getConversation_id() {
        return conversation_id;
    }

    public void setConversation_id(Long conversation_id) {
        this.conversation_id = conversation_id;
    }

    @Override
    public String toString() {
        return "Participant{" +
                "messages_read_at=" + messages_read_at +
                ", user_id=" + user_id +
                ", conversation_id=" + conversation_id +
                ", is_read=" + is_read +
                '}';
    }
}
