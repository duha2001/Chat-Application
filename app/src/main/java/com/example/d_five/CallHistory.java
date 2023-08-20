package com.example.d_five;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.TimeUnit;

public class CallHistory {

    private Long CallID;
    private Long userID;
    private String username;
    private String avatar;
    private String nickname;
    private Long conversationID;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime endTime;
    private String type;
    private Long duration;
    private boolean visible;


    public CallHistory(){

    }

    public CallHistory(Long callID, Long userID, String username, String avatar, String nickname, Long conversationID, String status, LocalDateTime createdAt, LocalDateTime endTime, String type, Long duration) {
        CallID = callID;
        this.userID = userID;
        this.username = username;
        this.avatar = avatar;
        this.nickname = nickname;
        this.conversationID = conversationID;
        this.status = status;
        this.createdAt = createdAt;
        this.endTime = endTime;
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

    public String getDurationHMS() {
        String hms = "";
        long hours = TimeUnit.MILLISECONDS.toHours(duration);
        long minutes = TimeUnit.MILLISECONDS.toMinutes(duration) % TimeUnit.HOURS.toMinutes(1);
        long seconds = TimeUnit.MILLISECONDS.toSeconds(duration) % TimeUnit.MINUTES.toSeconds(1);

        if(hours == 0) {
            if(minutes == 0) {
                hms = String.format("%ds", seconds);
            } else {
                hms = String.format("%dm %ds", minutes, seconds);
            }
        } else {
            hms = String.format("%dh %dm %ds", hours, minutes, seconds);
        }
        return hms;
    }

    public void setDuration(Long duration) {
        this.duration = duration;
    }

    public Long getCallID() {
        return CallID;
    }

    public void setCallID(Long callID) {
        CallID = callID;
    }

    public Long getUserID() {
        return userID;
    }

    public void setUserID(Long userID) {
        this.userID = userID;
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

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public Long getConversationID() {
        return conversationID;
    }

    public void setConversationID(Long conversationID) {
        this.conversationID = conversationID;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public String getTime() {
        String result = "";
        result = result + createdAt.getHour() + ":" + createdAt.getMinute();
        return result;
    }

    public String getTimeWithSlash() {
        String result = "";
        LocalDateTime now = LocalDateTime.now();
        LocalDate nowDate = now.toLocalDate();
        LocalDate startDate = createdAt.toLocalDate();

        if(nowDate.compareTo(startDate) == 0) {
            result = String.format("%02d:%02d", createdAt.getHour(), createdAt.getMinute());
//            result = createdAt.getHour() + ":" + createdAt.getMinute(); // in today
        } else {
            result = DateTimeFormatter.ofPattern("dd/MM/yy").format(createdAt);
        }
        return result;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    public boolean isExpandable(){
        return visible;
    }

    public void setExpandable(boolean isExpandable){
        this.visible = isExpandable;
    }
}
