package com.example.d_five.dao.implementDAO;


import com.example.d_five.CallHistory;
import com.example.d_five.dao.ICallDAO;
import com.example.d_five.model.Call;
import com.example.d_five.model.Message;
import com.example.d_five.utilities.ConnectionDatabaseHelper;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;

public class CallDAO implements ICallDAO {

    private Connection connection;

    public CallDAO(Connection connectionDB) {
        connection = connectionDB;
    }

    @Override
    public List<Call> getListCalls(Long conversation_id) throws Exception {
        List<Call> lst = new ArrayList<Call>();
        try {
            if(connection != null) {
                String SQL = "SELECT * FROM call WHERE conversation_id = ? ORDER BY created_at ASC";
                PreparedStatement preparedStatement = connection.prepareStatement(SQL);

                preparedStatement.setLong(1, conversation_id);
                ResultSet rs = preparedStatement.executeQuery();

                while(rs.next()) {
                    Call call = new Call();
                    call.setId(rs.getLong("id"));
                    call.setUser_id(rs.getLong("user_id"));
                    call.setStatus(rs.getString("status"));
                    call.setConversation_id(rs.getLong("conversation_id"));
                    Timestamp ts = rs.getTimestamp("created_at");
                    LocalDateTime localDt = null;
                    if( ts != null )
                        localDt =  LocalDateTime.ofInstant(Instant.ofEpochMilli(ts.getTime()), ZoneOffset.UTC);
                    call.setCreated_at(localDt);

                    ts = rs.getTimestamp("end_time");
                    localDt = null;
                    if(ts != null)
                        localDt = LocalDateTime.ofInstant(Instant.ofEpochMilli(ts.getTime()), ZoneOffset.UTC);
                    call.setEnd_time(localDt);
                    call.setType(rs.getString("type"));
                    call.setDuration(rs.getLong("duration")); // sec

                    // add
                    lst.add(call);
                }
                rs.close();
                preparedStatement.close();
            }
        } catch (Exception e){
            e.printStackTrace();
        }
        return lst;
    }

    @Override
    public List<CallHistory> getListHistoryCall(Long user_id) throws Exception {
        List<CallHistory> callHistoryList = new ArrayList<>();
        try {
            if(connection != null) {
                String SQL = "SELECT calltb.id as id_call, u.id, u.username, u.avatar, u.nickname, par.conversation_id, calltb.status, calltb.created_at, calltb.end_time, calltb.type, calltb.duration " +
                        "from participants par " +
                        "join (SELECT * FROM call c " +
                        "WHERE c.user_id = ?) calltb " +
                        "on calltb.conversation_id = par.conversation_id and par.user_id != calltb.user_id " +
                        "join users u " +
                        "on u.id = par.user_id " +
                        "order by calltb.created_at desc";
                PreparedStatement preparedStatement = connection.prepareStatement(SQL);

                preparedStatement.setLong(1, user_id);
                ResultSet rs = preparedStatement.executeQuery();

                while(rs.next()) {
                    CallHistory callHistory = new CallHistory();
                    callHistory.setCallID(rs.getLong("id_call"));
                    callHistory.setUserID(rs.getLong("id"));
                    callHistory.setUsername(rs.getString("username"));
                    callHistory.setAvatar(rs.getString("avatar"));
                    callHistory.setNickname(rs.getString("nickname"));
                    callHistory.setConversationID(rs.getLong("conversation_id"));
                    callHistory.setStatus(rs.getString("status"));
                    Timestamp ts = rs.getTimestamp("created_at");
                    LocalDateTime localDt = null;
                    if( ts != null )
                        localDt =  LocalDateTime.ofInstant(Instant.ofEpochMilli(ts.getTime()), ZoneOffset.UTC);
                    callHistory.setCreatedAt(localDt);
                    ts = rs.getTimestamp("end_time");
                    localDt = null;
                    if(ts != null)
                        localDt = LocalDateTime.ofInstant(Instant.ofEpochMilli(ts.getTime()), ZoneOffset.UTC);
                    callHistory.setEndTime(localDt);
                    callHistory.setType(rs.getString("type"));
                    callHistory.setDuration(rs.getLong("duration")); // sec
                    // add
                    callHistoryList.add(callHistory);
                }
                rs.close();
                preparedStatement.close();
                return callHistoryList;
            }
        } catch (Exception e){
            e.printStackTrace();
        }

        return callHistoryList;
    }

    @Override
    public List<CallHistory> getListHistoryCallContact(Long user_id, Long user_contact_id) throws Exception {
        List<CallHistory> callHistoryList = new ArrayList<>();
        try {
            if(connection != null) {
                String SQL = "SELECT calltb.id as id_call, u.id, u.username, u.avatar, u.nickname, par.conversation_id, calltb.status, calltb.created_at, calltb.end_time, calltb.type, calltb.duration " +
                        "from participants par " +
                        "join (SELECT * FROM call c " +
                        "WHERE c.user_id = ?) calltb " +
                        "on calltb.conversation_id = par.conversation_id and par.user_id != calltb.user_id " +
                        "join users u " +
                        "on u.id = par.user_id " +
                        "WHERE u.id = ? " +
                        "order by calltb.created_at desc";
                PreparedStatement preparedStatement = connection.prepareStatement(SQL);

                preparedStatement.setLong(1, user_id);
                preparedStatement.setLong(2, user_contact_id);

                ResultSet rs = preparedStatement.executeQuery();

                while(rs.next()) {
                    CallHistory callHistory = new CallHistory();
                    callHistory.setCallID(rs.getLong("id_call"));
                    callHistory.setUserID(rs.getLong("id"));
                    callHistory.setUsername(rs.getString("username"));
                    callHistory.setAvatar(rs.getString("avatar"));
                    callHistory.setNickname(rs.getString("nickname"));
                    callHistory.setConversationID(rs.getLong("conversation_id"));
                    callHistory.setStatus(rs.getString("status"));
                    Timestamp ts = rs.getTimestamp("created_at");
                    LocalDateTime localDt = null;
                    if( ts != null )
                        localDt =  LocalDateTime.ofInstant(Instant.ofEpochMilli(ts.getTime()), ZoneOffset.UTC);
                    callHistory.setCreatedAt(localDt);
                    ts = rs.getTimestamp("end_time");
                    localDt = null;
                    if(ts != null)
                        localDt = LocalDateTime.ofInstant(Instant.ofEpochMilli(ts.getTime()), ZoneOffset.UTC);
                    callHistory.setEndTime(localDt);
                    callHistory.setType(rs.getString("type"));
                    callHistory.setDuration(rs.getLong("duration")); // sec
                    // add
                    callHistoryList.add(callHistory);
                }
                rs.close();
                preparedStatement.close();
                return callHistoryList;
            }
        } catch (Exception e){
            e.printStackTrace();
        }

        return callHistoryList;
    }
}
