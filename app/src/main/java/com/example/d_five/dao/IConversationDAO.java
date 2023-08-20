package com.example.d_five.dao;

import com.example.d_five.model.Conversation;

public interface IConversationDAO {
    Conversation getInfoConversation(Long id) throws Exception;
    Conversation updateConversation(Conversation conversation) throws Exception;

    // check exist conversation 2 people
    Long checkExistConversation2P(Long user_id_1, Long user_id_2) throws Exception;

}
