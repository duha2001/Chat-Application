package com.example.d_five.dao.implementDAO;

import static com.example.d_five.utilities.Constants.tzUTC;

import com.example.d_five.dao.IParticipantsDAO;
import com.example.d_five.model.Participant;

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

public class ParticipantsDAO implements IParticipantsDAO {
    private Connection connection;

    public ParticipantsDAO(Connection connectionDB) {
        connection = connectionDB;
    }


    @Override
    public List<Participant> getListConversationOfUser(Long user_id) throws Exception {
        List<Participant> lst = new ArrayList<Participant>();

        try {
            if(connection != null) {
                String SQL = "SELECT * FROM participants WHERE user_id = ?";
                PreparedStatement preparedStatement = connection.prepareStatement(SQL);

                preparedStatement.setLong(1, user_id);
                ResultSet rs = preparedStatement.executeQuery();

                while(rs.next()) {
                    Participant participant = new Participant();
                    participant.setUser_id(rs.getLong("user_id"));
                    participant.setConversation_id(rs.getLong("conversation_id"));
                    Timestamp ts = rs.getTimestamp("messages_read_at");
                    LocalDateTime localDt = null;
                    if( ts != null )
                        localDt =  LocalDateTime.ofInstant(Instant.ofEpochMilli(ts.getTime()), ZoneOffset.UTC);
                    participant.setMessages_read_at(localDt);
                    participant.setIs_read(rs.getBoolean("is_read"));
                    lst.add(participant);
                }
                rs.close();
                preparedStatement.close();
                return lst;
            }
            return lst;
        } catch (Exception e){
            e.printStackTrace();
            return lst;
        }
    }

    @Override
    public List<Participant> getListUserOfConversation(Long conversation_id) throws Exception {
        List<Participant> lst = new ArrayList<Participant>();

        try {
            if(connection != null) {
                String SQL = "SELECT * FROM participants WHERE conversation_id = ?";
                PreparedStatement preparedStatement = connection.prepareStatement(SQL);

                preparedStatement.setLong(1, conversation_id);
                ResultSet rs = preparedStatement.executeQuery();

                while(rs.next()) {
                    Participant participant = new Participant();
                    participant.setUser_id(rs.getLong("user_id"));
                    participant.setConversation_id(rs.getLong("conversation_id"));
                    Timestamp ts = rs.getTimestamp("messages_read_at");
                    LocalDateTime localDt = null;
                    if( ts != null )
                        localDt =  LocalDateTime.ofInstant(Instant.ofEpochMilli(ts.getTime()), ZoneOffset.UTC);
                    participant.setMessages_read_at(localDt);
                    participant.setIs_read(rs.getBoolean("is_read"));
                    lst.add(participant);
                }
                rs.close();
                preparedStatement.close();
                return lst;
            }
            return lst;
        } catch (Exception e){
            e.printStackTrace();
            return lst;
        }
    }

    @Override
    public Participant updateIsRead(Long user_id, Long conversation_id, boolean isRead) throws Exception {
        Participant participant = new Participant();
        try {
            if(connection != null){

                String sql = "UPDATE participants SET messages_read_at = ?, is_read = ? WHERE user_id = ? and conversation_id = ?";
                PreparedStatement preparedStatement = connection.prepareStatement(sql);

                // create the preparedstatement and add the criteria
                preparedStatement.setTimestamp(1, new Timestamp(LocalDateTime.now().toInstant(ZoneOffset.UTC).toEpochMilli()), tzUTC);
                preparedStatement.setBoolean(2, isRead);
                preparedStatement.setLong(3, user_id);
                preparedStatement.setLong(4, conversation_id);


                // process the results
                int rowAffects = preparedStatement.executeUpdate();
                System.out.println("row Affects: " + rowAffects);
                preparedStatement.close();

                return getInfoParticipant(user_id, conversation_id);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return participant;
    }

    @Override
    public Participant getInfoParticipant(Long user_id, Long conversation_id) throws Exception {
        Participant participant = new Participant();
        try{
            if(connection != null) {

                String SQL = "SELECT * FROM participants WHERE user_id = ? and conversation_id = ?";

                PreparedStatement preparedStatement = connection.prepareStatement(SQL);

                // create the preparedstatement and add the criteria
                preparedStatement.setLong(1, user_id);
                preparedStatement.setLong(2, conversation_id);

                // process the results
                ResultSet rs = preparedStatement.executeQuery();
                while (rs.next()) {
                    participant.setUser_id(rs.getLong("user_id"));
                    participant.setConversation_id(rs.getLong("conversation_id"));
                    Timestamp ts = rs.getTimestamp("messages_read_at");
                    LocalDateTime localDt = null;
                    if( ts != null )
                        localDt =  LocalDateTime.ofInstant(Instant.ofEpochMilli(ts.getTime()), ZoneOffset.UTC);
                    participant.setMessages_read_at(localDt);
                    participant.setIs_read(rs.getBoolean("is_read"));
                }
                rs.close();
                preparedStatement.close();
            }
        }catch (SQLException e){
            e.printStackTrace();
        }
        return participant;
    }

    @Override
    public Participant deleteParticipant(Long user_id, Long conversation_id) throws Exception {
        Participant participant = new Participant();
        try{
            if(connection != null) {

                String SQL = "DELETE FROM participants WHERE user_id = ? and conversation_id = ? RETURNING *";

                PreparedStatement preparedStatement = connection.prepareStatement(SQL);

                // create the preparedstatement and add the criteria
                preparedStatement.setLong(1, user_id);
                preparedStatement.setLong(2, conversation_id);

                // process the results
                ResultSet rs = preparedStatement.executeQuery();
                while (rs.next()) {
                    participant.setUser_id(rs.getLong("user_id"));
                    participant.setConversation_id(rs.getLong("conversation_id"));
                    Timestamp ts = rs.getTimestamp("messages_read_at");
                    LocalDateTime localDt = null;
                    if( ts != null )
                        localDt =  LocalDateTime.ofInstant(Instant.ofEpochMilli(ts.getTime()), ZoneOffset.UTC);
                    participant.setMessages_read_at(localDt);
                    participant.setIs_read(rs.getBoolean("is_read"));
                }
                rs.close();
                preparedStatement.close();
            }
        }catch (SQLException e){
            e.printStackTrace();
        }
        return participant;
    }

    @Override
    public Boolean checkUserInConversation(Long user_id, Long conversation_id) throws Exception {
        Boolean check = false;
        try {

            if(connection != null) {

                String SQL = "SELECT * FROM participants WHERE user_id = ? AND conversation_id = ?";

                PreparedStatement preparedStatement = connection.prepareStatement(SQL);

                // create the preparedstatement and add the criteria
                preparedStatement.setLong(1, user_id);
                preparedStatement.setLong(2, conversation_id);

                // process the results
                ResultSet rs = preparedStatement.executeQuery();
                while (rs.next()) {
                    check = true;
                }
                rs.close();
                preparedStatement.close();
            }

        }catch (Exception e){
            e.printStackTrace();
        }

        return check;
    }
}
