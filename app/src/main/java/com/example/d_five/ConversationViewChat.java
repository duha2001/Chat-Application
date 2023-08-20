package com.example.d_five;

import java.time.LocalDateTime;

public class ConversationViewChat {
    private String avatar;
    private String name;
    private String last_message;
    private LocalDateTime time;
    private Long conversation_id;
    private boolean isRead;

    public ConversationViewChat(String avatar, String name, String last_message, LocalDateTime time, Long conversation_id, boolean isRead) {
        this.avatar = avatar;
        this.name = name;
        this.last_message = last_message;
        this.time = time;
        this.conversation_id = conversation_id;
    }

    public ConversationViewChat() {
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLast_message() {
        return last_message;
    }

    public void setLast_message(String last_message) {
        this.last_message = last_message;
    }

//    public LocalDateTime getTime() {
//        return time;
//    }

    public String getTime() {
        String result = "";
        if (time != null) {
            String hour = String.valueOf(time.getHour());
            String minute = String.valueOf(time.getMinute());

            if (hour.length() < 2) {
                hour = "0" + hour;
            }

            if (minute.length() < 2) {
                minute = "0" + minute;
            }

            result = result + hour + ":" + minute;
        }

        return result;
    }

    public LocalDateTime getTimeTime() {
        if (time != null) {
            return  time;
        } else {
            return null;
        }
    }



    public void setTime(LocalDateTime time) {
        this.time = time;
    }

    public Long getConversation_id() {
        return conversation_id;
    }

    public void setConversation_id(Long conversation_id) {
        this.conversation_id = conversation_id;
    }

    public boolean isRead() {
        return isRead;
    }

    public void setRead(boolean read) {
        isRead = read;
    }
}
