package com.example.d_five.model;

import java.time.LocalDateTime;
import java.util.Date;

public class Message {
    private Long id;
    private String content;
    private LocalDateTime created_at;
    private Long conversation_id;
    private Long user_id;
    private String isType;

    public Message(){

    }

    public Message(Long id, String content, LocalDateTime created_at, Long conversation_id, Long user_id, String isType) {
        this.id = id;
        this.content = content;
        this.created_at = created_at;
        this.conversation_id = conversation_id;
        this.user_id = user_id;
        this.isType = isType;
    }

    public String getIsType() {
        return isType;
    }

    public void setIsType(String isType) {
        this.isType = isType;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public LocalDateTime getCreated_at() {
        return created_at;
    }

    public void setCreated_at(LocalDateTime created_at) {
        this.created_at = created_at;
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
        return "Message{" +
                "id=" + id +
                ", content='" + content + '\'' +
                ", created_at=" + created_at +
                ", conversation_id=" + conversation_id +
                ", user_id=" + user_id +
                '}';
    }
}
