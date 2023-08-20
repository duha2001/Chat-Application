package com.example.d_five.model;

public class Conversation {
    private Long id;
    private String last_message;
    private String name_conversation;
    private Boolean group = false;
    private Long admin_id;
    private Long last_message_id;

    public Conversation(){};

    public Conversation(String last_message, String name_conversation) {
        this.last_message = last_message;
        this.name_conversation = name_conversation;
    }

    public Conversation(Long id, String last_message, String name_conversation, Boolean group, Long admin_id, Long last_message_id) {
        this.id = id;
        this.last_message = last_message;
        this.name_conversation = name_conversation;
        this.group = group;
        this.admin_id = admin_id;
        this.last_message_id = last_message_id;
    }

    public Long getLast_message_id() {
        return last_message_id;
    }

    public void setLast_message_id(Long last_message_id) {
        this.last_message_id = last_message_id;
    }

    public Boolean isGroup() {
        return group;
    }

    public void setGroup(Boolean group) {
        this.group = group;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getLast_message() {
        return last_message;
    }

    public void setLast_message(String last_message) {
        this.last_message = last_message;
    }

    public String getName_conversation() {
        return name_conversation;
    }

    public void setName_conversation(String name_conversation) {
        this.name_conversation = name_conversation;
    }

    public Long getAdmin_id() {
        return admin_id;
    }

    public void setAdmin_id(Long admin_id) {
        this.admin_id = admin_id;
    }
}
