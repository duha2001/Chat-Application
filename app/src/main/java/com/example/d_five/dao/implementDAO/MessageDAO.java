package com.example.d_five.dao.implementDAO;

import static com.example.d_five.utilities.Constants.tzUTC;

import com.example.d_five.dao.IMessageDAO;
import com.example.d_five.model.Conversation;
import com.example.d_five.model.Message;
import com.example.d_five.model.Participant;
import com.example.d_five.utilities.ConnectionDatabaseHelper;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;

public class MessageDAO implements IMessageDAO {
    private Connection connection;

    public MessageDAO(Connection connectionDB) {
        connection = connectionDB;
    }


    @Override
    public List<Message> getListMessages(Long conversation_id) throws Exception {
        List<Message> lst = new ArrayList<Message>();

        try {
            if(connection != null) {
                String SQL = "SELECT * FROM messages WHERE conversation_id = ? ORDER BY created_at DESC";
                PreparedStatement preparedStatement = connection.prepareStatement(SQL);

                preparedStatement.setLong(1, conversation_id);
                ResultSet rs = preparedStatement.executeQuery();

                while(rs.next()) {
                    Message message = new Message();
                    message.setId(rs.getLong("id"));
                    message.setUser_id(rs.getLong("user_id"));
                    message.setContent(rs.getString("content"));
                    message.setConversation_id(rs.getLong("conversation_id"));
                    Timestamp ts = rs.getTimestamp("created_at");
                    LocalDateTime localDt = null;
                    if( ts != null )
                        localDt =  LocalDateTime.ofInstant(Instant.ofEpochMilli(ts.getTime()), ZoneOffset.UTC);
                    message.setCreated_at(localDt);
                    message.setIsType(rs.getString("is_type"));

                    lst.add(message);
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
    public Message getInfoMessage(Long message_id) throws Exception {
        Message message1 = new Message();

        try{
            if(connection != null) {

                String SQL = "SELECT * FROM messages WHERE id = ?";


                PreparedStatement preparedStatement = connection.prepareStatement(SQL);

                // create the preparedstatement and add the criteria
                preparedStatement.setLong(1, message_id);
                // process the results
                ResultSet rs = preparedStatement.executeQuery();
                while (rs.next()) {
                    message1.setId(rs.getLong("id"));
                    message1.setContent(rs.getString("content"));
                    Timestamp ts = rs.getTimestamp("created_at");
                    LocalDateTime localDt = null;
                    if( ts != null )
                        localDt =  LocalDateTime.ofInstant(Instant.ofEpochMilli(ts.getTime()), ZoneOffset.UTC);
                    message1.setCreated_at(localDt);
                    message1.setConversation_id(rs.getLong("conversation_id"));
                    message1.setUser_id(rs.getLong("user_id"));
                    message1.setIsType(rs.getString("is_type"));

                }
                rs.close();
                preparedStatement.close();
            }
        }catch (SQLException e){
            e.printStackTrace();
        }
        return message1;
    }

    @Override
    public List<Message> getListMessagesLimit(Long conversation_id, Long limit, Long end) throws Exception {
        List<Message> list = new ArrayList<>();

        try {
            if(connection != null) {
                String SQL = "select * from messages " +
                        "WHERE conversation_id = ? and id < ? " +
                        "LIMIT ?";
                PreparedStatement preparedStatement = connection.prepareStatement(SQL);

                preparedStatement.setLong(1, conversation_id);
                preparedStatement.setLong(2, end);
                preparedStatement.setLong(3, limit);
                ResultSet rs = preparedStatement.executeQuery();

                while(rs.next()) {
                    Message message = new Message();
                    message.setId(rs.getLong("id"));
                    message.setUser_id(rs.getLong("user_id"));
                    message.setContent(rs.getString("content"));
                    message.setConversation_id(rs.getLong("conversation_id"));
                    Timestamp ts = rs.getTimestamp("created_at");
                    LocalDateTime localDt = null;
                    if( ts != null )
                        localDt =  LocalDateTime.ofInstant(Instant.ofEpochMilli(ts.getTime()), ZoneOffset.UTC);
                    message.setCreated_at(localDt);
                    message.setIsType(rs.getString("is_type"));

                    list.add(message);
                }
                rs.close();
                preparedStatement.close();
            }
        } catch (Exception e){
            e.printStackTrace();
        }

        return list;
    }

    @Override
    public List<Message> getListMessagesFirst(Long conversation_id, Long limit) throws Exception {
        List<Message> lst = new ArrayList<Message>();

        try {
            if(connection != null) {
                String SQL = "SELECT * FROM messages WHERE conversation_id = ? ORDER BY created_at DESC LIMIT ?";
                PreparedStatement preparedStatement = connection.prepareStatement(SQL);

                preparedStatement.setLong(1, conversation_id);
                preparedStatement.setLong(2, limit);

                ResultSet rs = preparedStatement.executeQuery();

                while(rs.next()) {
                    Message message = new Message();
                    message.setId(rs.getLong("id"));
                    message.setUser_id(rs.getLong("user_id"));
                    message.setContent(rs.getString("content"));
                    message.setConversation_id(rs.getLong("conversation_id"));
                    Timestamp ts = rs.getTimestamp("created_at");
                    LocalDateTime localDt = null;
                    if( ts != null )
                        localDt =  LocalDateTime.ofInstant(Instant.ofEpochMilli(ts.getTime()), ZoneOffset.UTC);
                    message.setCreated_at(localDt);
                    message.setIsType(rs.getString("is_type"));

                    lst.add(message);
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
    public Message deleteMessage(Long message_id) throws Exception {
        Message message = new Message();
        try{
            if(connection != null) {

                String SQL = "DELETE FROM messages WHERE id = ? RETURNING *";

                PreparedStatement preparedStatement = connection.prepareStatement(SQL);

                // create the preparedstatement and add the criteria
                preparedStatement.setLong(1, message_id);

                // process the results
                ResultSet rs = preparedStatement.executeQuery();
                while (rs.next()) {
                    message.setId(rs.getLong("id"));
                    message.setUser_id(rs.getLong("user_id"));
                    message.setContent(rs.getString("content"));
                    message.setConversation_id(rs.getLong("conversation_id"));
                    Timestamp ts = rs.getTimestamp("created_at");
                    LocalDateTime localDt = null;
                    if( ts != null )
                        localDt =  LocalDateTime.ofInstant(Instant.ofEpochMilli(ts.getTime()), ZoneOffset.UTC);
                    message.setCreated_at(localDt);
                    message.setIsType(rs.getString("is_type"));
                }
                rs.close();
                preparedStatement.close();
            }
        }catch (SQLException e){
            e.printStackTrace();
        }
        return message;
    }
}
