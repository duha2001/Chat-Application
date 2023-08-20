package com.example.d_five.dao;

import android.widget.ExpandableListAdapter;

import com.example.d_five.model.Call;
import com.example.d_five.model.Conversation;
import com.example.d_five.model.Message;
import com.example.d_five.model.Participant;
import com.example.d_five.model.User;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

public interface IUserDAO {
    void createDatabase() throws Exception;

    // getInfo User
    User getInfoUser(String username) throws Exception;
    // getInfo User
    User getInfoUser(Long user_id) throws Exception;
    //insert user
    User insertInfoUser(User user) throws Exception;

    //check exist user
    Boolean checkUserExist(String username) throws Exception;

    // update User Info
    User updateInfoUser(User user) throws Exception;

    // add newConversation : return idConversation
    Conversation newConversation(String lastMessage, Long lastMessage_id, String nameConversation, Boolean group, Long admin_id) throws Exception;

    // insertMessage : return new Message
    Message insertMessage(Long user_id, Long conversation_id, String message, String isType) throws Exception;

    //insertParticipant
    Participant insertParticipant(Long user_id, Long conversation_id) throws Exception;

    // insertCall : return new Call history
    Call insertCall(Long user_id, Long conversation_id, LocalDateTime created_at, LocalDateTime end_time, String status, String type, Long duration) throws Exception;



}
