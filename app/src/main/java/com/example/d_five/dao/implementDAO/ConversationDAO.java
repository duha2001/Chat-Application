package com.example.d_five.dao.implementDAO;

import com.example.d_five.model.Conversation;
import com.example.d_five.model.Participant;
import com.example.d_five.model.User;
import com.example.d_five.utilities.ConnectionDatabaseHelper;
import com.example.d_five.dao.IConversationDAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;

public class ConversationDAO implements IConversationDAO {

    private Connection connection;

    public ConversationDAO(Connection connectionDB) {
        connection = connectionDB;
    }

    @Override
    public Conversation getInfoConversation(Long id) throws Exception{
        Conversation conversation = new Conversation();

        try{
            if(connection != null) {

                String SQL = "SELECT * FROM conversation WHERE id = ?";


                PreparedStatement preparedStatement = connection.prepareStatement(SQL);

                // create the preparedstatement and add the criteria
                preparedStatement.setLong(1, id);
                // process the results
                ResultSet rs = preparedStatement.executeQuery();
                while (rs.next()) {
                    conversation.setId(rs.getLong("id"));
                    conversation.setName_conversation(rs.getString("name_conversation"));
                    conversation.setLast_message(rs.getString("last_message"));
                    conversation.setGroup(rs.getBoolean("is_group"));
                    conversation.setAdmin_id(rs.getLong("admin_id"));
                    conversation.setLast_message_id(rs.getLong("last_message_id"));
                }
                rs.close();
                preparedStatement.close();
            }
        }catch (SQLException e){
            e.printStackTrace();
        }
        return conversation;
    }

    @Override
    public Conversation updateConversation(Conversation conversation) throws Exception {
        try {
            if(connection != null){

                String sql = "UPDATE conversation SET last_message = ?, name_conversation = ?, admin_id = ?, last_message_id = ? WHERE id = ?";
                PreparedStatement preparedStatement = connection.prepareStatement(sql);

                // create the preparedstatement and add the criteria
                preparedStatement.setString(1, conversation.getLast_message());
                preparedStatement.setString(2, conversation.getName_conversation());
                preparedStatement.setLong(3, conversation.getAdmin_id());
                if(conversation.getLast_message_id() == null){
                    preparedStatement.setNull(4, Types.BIGINT);
                } else {
                    preparedStatement.setLong(4, conversation.getLast_message_id());
                }
                preparedStatement.setLong(5, conversation.getId());


                // process the results
                int rowAffects = preparedStatement.executeUpdate();
                System.out.println("row Affects: " + rowAffects);
                preparedStatement.close();
                return getInfoConversation(conversation.getId());

            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Long checkExistConversation2P(Long user_id_1, Long user_id_2) throws Exception {
        Long idConversation = null;
        try{
            if(connection != null) {

                String SQL = "select con.id from conversation con\n" +
                        "join\n" +
                        "(select p1.conversation_id\n" +
                        "from participants p1 join participants p2 \n" +
                        "on p1.conversation_id = p2.conversation_id\n" +
                        "where p1.user_id = ? AND p2.user_id = ?) pp\n" +
                        "on pp.conversation_id = con.id\n" +
                        "where con.is_group = false";

                PreparedStatement preparedStatement = connection.prepareStatement(SQL);

                // create the prepared statement and add the criteria
                preparedStatement.setLong(1, user_id_1);
                preparedStatement.setLong(2, user_id_2);


                // process the results
                ResultSet rs = preparedStatement.executeQuery();
                while (rs.next()) {
                    idConversation = rs.getLong("id");
                }
                rs.close();
                preparedStatement.close();
            }
        } catch (SQLException e){
            e.printStackTrace();
        }
        return idConversation;
    }
}
