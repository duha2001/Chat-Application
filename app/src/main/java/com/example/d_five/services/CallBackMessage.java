package com.example.d_five.services;

import com.example.d_five.model.LocationMessage;

import org.linphone.core.ChatMessage;

public interface CallBackMessage {
    void onMessageChange(ChatMessage chatMessage, LocationMessage locationMessage);
    Long getConversationId();

}
