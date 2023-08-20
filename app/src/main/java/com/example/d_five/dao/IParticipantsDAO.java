package com.example.d_five.dao;

import com.example.d_five.model.Participant;

import java.util.List;

public interface IParticipantsDAO {

    // getList Conversation of user
    List<Participant> getListConversationOfUser(Long user_id) throws Exception;

    // get List User of conversation
    List<Participant> getListUserOfConversation(Long conversation_id) throws Exception;

    // update is read
    Participant updateIsRead(Long user_id, Long conversation_id, boolean isRead) throws Exception;

    // get info participant
    Participant getInfoParticipant(Long user_id, Long conversation_id) throws Exception;


    // delete participant in group
    Participant deleteParticipant(Long user_id, Long conversation_id) throws Exception;


    // check user in conversation
    Boolean checkUserInConversation(Long user_id, Long conversation_id) throws Exception;

}
