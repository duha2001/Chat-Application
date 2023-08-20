package com.example.d_five;

import android.net.Uri;

import androidx.annotation.Nullable;

import com.example.d_five.model.LocationMessage;
import com.example.d_five.model.TypeMessage;

public class MessageUtil {

    private String sender;
    private String receiver;

    private TypeMessage typeMessage;
    private String messageID;
    private String status;
    private String message;
    private String timestamp;
    private LocationMessage locationMessage;




    public MessageUtil(String sender, String receiver, String message, String timestamp, LocationMessage locationMessage, String status, String messageId, TypeMessage typeMessage) {
        this.sender = sender;
        this.receiver = receiver;
        this.message = message;
        this.timestamp = timestamp;
        this.locationMessage = locationMessage;
        this.status = status;
        this.messageID = messageId;
        this.typeMessage = typeMessage;
    }


    public TypeMessage getTypeMessage() {
        return typeMessage;
    }

    public void setTypeMessage(TypeMessage typeMessage) {
        this.typeMessage = typeMessage;
    }


    public LocationMessage getLocationMessage() {
        return locationMessage;
    }

    public void setLocationMessage(LocationMessage locationMessage) {
        this.locationMessage = locationMessage;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }




    public String getMessageID() {
        return messageID;
    }

    public void setMessageID(String messageID) {
        this.messageID = messageID;
    }


    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }


    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
       if(this == obj) return true;
       if(obj == null || getClass() != obj.getClass()) return false;
       MessageUtil message = (MessageUtil) obj;
       return messageID.equals(message.messageID);
    }
}
