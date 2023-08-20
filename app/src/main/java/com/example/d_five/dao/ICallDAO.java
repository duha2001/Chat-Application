package com.example.d_five.dao;


import com.example.d_five.CallHistory;
import com.example.d_five.model.Call;

import java.util.List;

public interface ICallDAO {

    //get list call history
    List<Call> getListCalls(Long conversation_id) throws Exception;

    // get list history call of user
    List<CallHistory> getListHistoryCall(Long user_id) throws Exception;

    // get list history call of user contact
    List<CallHistory> getListHistoryCallContact(Long user_id, Long user_contact_id) throws Exception;
}
