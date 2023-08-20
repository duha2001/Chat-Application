package com.example.d_five.dao;

import com.example.d_five.model.Message;

import java.util.List;

public interface IMessageDAO {
    // get list messages of conversation
    List<Message> getListMessages(Long conversation_id) throws Exception;

    // get InfoMessage
    Message getInfoMessage(Long message_id) throws Exception;


    // get list messages of conversation
    List<Message> getListMessagesLimit(Long conversation_id, Long limit, Long endIDMessage) throws Exception;

    List<Message> getListMessagesFirst(Long conversation_id, Long limit) throws Exception;


    // delete message
    Message deleteMessage(Long message_id) throws Exception;
}
