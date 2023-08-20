package com.example.d_five.dao.implementDAO;

import static com.example.d_five.utilities.Constants.tzUTC;

import com.example.d_five.dao.IUserDAO;
import com.example.d_five.model.Call;
import com.example.d_five.model.Conversation;
import com.example.d_five.model.Message;
import com.example.d_five.model.Participant;
import com.example.d_five.model.User;
import com.example.d_five.utilities.ConnectionDatabaseHelper;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.sql.Types;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Date;

public class UserDAO implements IUserDAO {

    private Connection connection;

    public UserDAO(Connection connectionDB) {
//        try {
//            ConnectionDatabaseHelper connectionDatabaseHelper = new ConnectionDatabaseHelper();
//            connection = connectionDatabaseHelper.connectionClass();
//
//            if(connection != null){
//                System.out.println("Connect Database successfully !!");
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }

        connection = connectionDB;
    }

    @Override
    public void createDatabase() {
        try {
            if (connection != null) {
                System.out.println("Connect ok");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public User getInfoUser(String username) throws Exception {
        User user = new User();
        try {
            if (connection != null) {

                String SQL = "SELECT * FROM users WHERE username = ?";


                PreparedStatement preparedStatement = connection.prepareStatement(SQL);

                // create the preparedstatement and add the criteria
                preparedStatement.setString(1, username);
                // process the results
                ResultSet rs = preparedStatement.executeQuery();
                while (rs.next()) {
                    user.setId(rs.getLong("id"));
                    user.setUsername(rs.getString("username"));
                    user.setAvatar(rs.getString("avatar"));
                    user.setDomain(rs.getString("domain"));
                    user.setNickname(rs.getString("nickname"));
                }
                rs.close();
                preparedStatement.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return user;
    }

    public ArrayList<User> getAll() throws SQLException {
        ArrayList<User> users = new ArrayList<>();
        String SQL = "SELECT  * FROM users";
        PreparedStatement preparedStatement = connection.prepareStatement(SQL);
        ResultSet rs = preparedStatement.executeQuery();
        if (rs.next()) {
            do {
                User user = new User();
                user.setUsername(rs.getString("username"));
                users.add(user);
            } while (rs.next());
        }
        rs.close();
        return users;
    }

    public ArrayList<User> searchUser(String name) throws SQLException {
        ArrayList<User> users = new ArrayList<>();
        String SQL = "SELECT * FROM users WHERE username LIKE '%?%' ";
        PreparedStatement preparedStatement = connection.prepareStatement(SQL);
        preparedStatement.setString(1, name);
        ResultSet rs = preparedStatement.executeQuery();
        if (rs.next()) {
            do {
                User user = new User();
                user.setUsername(rs.getString("username"));
                users.add(user);
            } while (rs.next());
        }
        rs.close();
        return users;
    }

    @Override
    public User getInfoUser(Long user_id) throws Exception {
        User user = new User();

        try {
            if (connection != null) {

                String SQL = "SELECT * FROM users WHERE id = ?";


                PreparedStatement preparedStatement = connection.prepareStatement(SQL);

                // create the preparedstatement and add the criteria
                preparedStatement.setLong(1, user_id);
                // process the results
                ResultSet rs = preparedStatement.executeQuery();
                while (rs.next()) {
                    user.setId(rs.getLong("id"));
                    user.setUsername(rs.getString("username"));
                    user.setAvatar(rs.getString("avatar"));
                    user.setDomain(rs.getString("domain"));
                    user.setNickname(rs.getString("nickname"));
                }
                rs.close();
                preparedStatement.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return user;
    }

    @Override
    public User insertInfoUser(User user) throws Exception {
        try {
            if (connection != null) {
                String SQL = "INSERT INTO users(username, avatar, domain) VALUES (?, ?, ?) RETURNING *";
                PreparedStatement preparedStatement = connection.prepareStatement(SQL);
                preparedStatement.setString(1, user.getUsername());
                preparedStatement.setString(2, user.getAvatar());
                preparedStatement.setString(3, user.getDomain());


                ResultSet rs = preparedStatement.executeQuery();
                while (rs.next()) {
                    user.setId(rs.getLong("id"));
                    user.setUsername(rs.getString("username"));
                    user.setAvatar(rs.getString("avatar"));
                    user.setDomain(rs.getString("domain"));
                }
                rs.close();
                preparedStatement.close();
                return user;
            }
            return null;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public Boolean checkUserExist(String username) {
        try {
            if (connection != null) {
                String SQL = "SELECT * FROM users WHERE username = ?";
                PreparedStatement preparedStatement = connection.prepareStatement(SQL);

                // create the preparedstatement and add the criteria
                preparedStatement.setString(1, username);
                // process the results
                ResultSet rs = preparedStatement.executeQuery();

                boolean isExists = false;
                while (rs.next()) {
                    isExists = true;
                }
                rs.close();
                preparedStatement.close();
                return isExists;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public User updateInfoUser(User user) throws Exception {
        try {
            if (connection != null) {

                String sql = "UPDATE users SET username = ?, avatar = ?, nickname = ? WHERE id = ?";
                PreparedStatement preparedStatement = connection.prepareStatement(sql);

                // create the preparedstatement and add the criteria
                preparedStatement.setString(1, user.getUsername());
                preparedStatement.setString(2, user.getAvatar());
                preparedStatement.setString(3, user.getNickname());
                preparedStatement.setLong(4, user.getId());


                // process the results
                int rowAffects = preparedStatement.executeUpdate();
                System.out.println("row Affects: " + rowAffects);
                preparedStatement.close();
                return getInfoUser(user.getUsername());

            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Conversation newConversation(String lastMessage, Long lastMessage_id, String nameConversation, Boolean group, Long admin_id) throws Exception {
        Conversation conversation = new Conversation();
        try {
            if (connection != null) {
                String SQL = "INSERT INTO conversation(last_message, name_conversation, is_group, admin_id, last_message_id) VALUES (?, ?, ?, ?, ?) RETURNING *";
                PreparedStatement preparedStatement = connection.prepareStatement(SQL);
                preparedStatement.setString(1, lastMessage);
                preparedStatement.setString(2, nameConversation);
                preparedStatement.setBoolean(3, group);
                if (admin_id == null) {
                    preparedStatement.setNull(4, Types.BIGINT);
                } else {
                    preparedStatement.setLong(4, admin_id);
                }
                if (lastMessage_id == null) {
                    preparedStatement.setNull(5, Types.BIGINT);
                } else {
                    preparedStatement.setLong(5, lastMessage_id);
                }


                ResultSet rs = preparedStatement.executeQuery();

                // return conversation
                if (rs.next()) {
                    conversation.setId(rs.getLong("id"));
                    conversation.setLast_message(rs.getString("last_message"));
                    conversation.setName_conversation(rs.getString("name_conversation"));
                    conversation.setGroup(rs.getBoolean("is_group"));
                    conversation.setAdmin_id(rs.getLong("admin_id"));
                    conversation.setLast_message_id(rs.getLong("last_message_id"));

                    return conversation;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return conversation;
    }

    @Override
    public Message insertMessage(Long user_id, Long conversation_id, String message, String isType) throws Exception {
        try {
            if (connection != null) {
                String SQL = "INSERT INTO messages(content, created_at, conversation_id, user_id, is_type) VALUES (?, ?, ?, ?, ?) RETURNING *";
                PreparedStatement preparedStatement = connection.prepareStatement(SQL);
                preparedStatement.setString(1, message);
                preparedStatement.setTimestamp(2, new Timestamp(LocalDateTime.now().toInstant(ZoneOffset.UTC).toEpochMilli()), tzUTC);
                preparedStatement.setLong(3, conversation_id);
                preparedStatement.setLong(4, user_id);
                preparedStatement.setString(5, isType);

                ResultSet rs = preparedStatement.executeQuery();
                Message message1 = new Message();
                while (rs.next()) {
                    message1.setId(rs.getLong("id"));
                    message1.setContent(rs.getString("content"));
                    Timestamp ts = rs.getTimestamp("created_at");
                    LocalDateTime localDt = null;
                    if (ts != null)
                        localDt = LocalDateTime.ofInstant(Instant.ofEpochMilli(ts.getTime()), ZoneOffset.UTC);
                    message1.setCreated_at(localDt);
                    message1.setConversation_id(rs.getLong("conversation_id"));
                    message1.setUser_id(rs.getLong("user_id"));
                    message1.setIsType(rs.getString("is_type"));
                }
                rs.close();
                preparedStatement.close();
                return message1;
            }
            return null;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public Participant insertParticipant(Long user_id, Long conversation_id) throws Exception {
        try {

            if (connection != null) {
                String SQL = "INSERT INTO participants(messages_read_at, user_id, conversation_id, is_read) VALUES (?, ?, ?, ?) RETURNING *";
                PreparedStatement preparedStatement = connection.prepareStatement(SQL);

                preparedStatement.setTimestamp(1, new Timestamp(LocalDateTime.now().toInstant(ZoneOffset.UTC).toEpochMilli()), tzUTC);
                preparedStatement.setLong(2, user_id);
                preparedStatement.setLong(3, conversation_id);
                preparedStatement.setBoolean(4, false);

                ResultSet rs = preparedStatement.executeQuery();
                Participant participant = new Participant();
                while (rs.next()) {
                    participant.setUser_id(rs.getLong("user_id"));
                    participant.setConversation_id(rs.getLong("conversation_id"));
                    Timestamp ts = rs.getTimestamp("messages_read_at");
                    LocalDateTime localDt = null;
                    if (ts != null)
                        localDt = LocalDateTime.ofInstant(Instant.ofEpochMilli(ts.getTime()), ZoneOffset.UTC);
                    participant.setMessages_read_at(localDt);
                }
                rs.close();
                preparedStatement.close();
                return participant;
            }
            return null;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public Call insertCall(Long user_id, Long conversation_id, LocalDateTime created_at, LocalDateTime end_time, String status, String type, Long duration) throws Exception {
        try {
            if (connection != null) {
                String SQL = "INSERT INTO call(status, created_at, end_time, conversation_id, user_id, type, duration) VALUES (?, ?, ?, ?, ?, ?, ?) RETURNING *";
                PreparedStatement preparedStatement = connection.prepareStatement(SQL);
                preparedStatement.setString(1, status);

                preparedStatement.setTimestamp(2, new Timestamp(created_at.toInstant(ZoneOffset.UTC).toEpochMilli()), tzUTC);
                preparedStatement.setTimestamp(3, new Timestamp(end_time.toInstant(ZoneOffset.UTC).toEpochMilli()), tzUTC);
                preparedStatement.setLong(4, conversation_id);
                preparedStatement.setLong(5, user_id);
                preparedStatement.setString(6, type);
                preparedStatement.setLong(7, duration);

                ResultSet rs = preparedStatement.executeQuery();
                Call call = new Call();
                while (rs.next()) {
                    call.setId(rs.getLong("id"));
                    call.setStatus(rs.getString("status"));
                    Timestamp ts = rs.getTimestamp("created_at");
                    LocalDateTime localDt = null;
                    if (ts != null)
                        localDt = LocalDateTime.ofInstant(Instant.ofEpochMilli(ts.getTime()), ZoneOffset.UTC);
                    call.setCreated_at(localDt);
                    Timestamp ts2 = rs.getTimestamp("end_time");
                    LocalDateTime localDt2 = null;
                    if (ts != null)
                        localDt = LocalDateTime.ofInstant(Instant.ofEpochMilli(ts.getTime()), ZoneOffset.UTC);
                    call.setEnd_time(localDt2);
                    call.setUser_id(rs.getLong("user_id"));
                    call.setConversation_id(rs.getLong("conversation_id"));
                    call.setType(rs.getString("type"));
                    call.setDuration(rs.getLong("duration")); // sec
                }
                rs.close();
                preparedStatement.close();
                return call;
            }
            return null;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
}
