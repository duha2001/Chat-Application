package com.example.d_five.dao.implementDAO;

import com.example.d_five.dao.IContactsDAO;
import com.example.d_five.model.Contacts;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class ContactsDAO implements IContactsDAO {

    private Connection connection;

    public ContactsDAO(Connection connectionDB) {
        connection = connectionDB;
    }


    @Override
    public Contacts insertContact(Contacts contacts) throws Exception {
        try {
            if (connection != null) {
                String SQL = "INSERT INTO contacts(user_id, user_contact_id, status) VALUES (?, ?, ?) RETURNING *";
                PreparedStatement preparedStatement = connection.prepareStatement(SQL);
                preparedStatement.setLong(1, contacts.getUser_id());
                preparedStatement.setLong(2, contacts.getUser_contact_id());
                preparedStatement.setString(3, contacts.getStatus());


                ResultSet rs = preparedStatement.executeQuery();
                while (rs.next()) {
                    contacts.setUser_id(rs.getLong("user_id"));
                    contacts.setUser_contact_id(rs.getLong("user_contact_id"));
                    contacts.setStatus(rs.getString("status"));
                }
                rs.close();
                preparedStatement.close();
                return contacts;
            }
            return null;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public boolean deleteContact (Long user_contact_id) throws Exception {
        try {
            if(connection != null){
                String SQL = "DELETE FROM contacts WHERE user_contact_id = ?";
                PreparedStatement preparedStatement = connection.prepareStatement(SQL);
                preparedStatement.setLong(1, user_contact_id);
                preparedStatement.executeQuery();
                return true;
            }
            return false;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean checkContact(Long user_id, Long user_contact_id) throws Exception {
        try {
            if (connection != null) {
                String SQL = "SELECT * FROM contacts WHERE EXISTS (SELECT * FROM contacts WHERE user_id = ? AND user_contact_id = ?)";
                PreparedStatement preparedStatement = connection.prepareStatement(SQL);
                preparedStatement.setLong(1, user_id);
                preparedStatement.setLong(2, user_contact_id);

                ResultSet rs = preparedStatement.executeQuery();
                if (rs.next()) {
                    return true;
                } else {
                    return false;
                }
//                rs.close();
//                preparedStatement.close();
            }
            return false;
        } catch (SQLException e) {
            e.printStackTrace();
            return  false;
        }
    }

    @Override
    public ArrayList<Contacts> getListContacts(Long user_id) throws Exception {
        ArrayList<Contacts> lst = new ArrayList<>();
        try {
            if(connection != null) {
                String SQL = "SELECT * FROM contacts WHERE user_id = ?";
                PreparedStatement preparedStatement = connection.prepareStatement(SQL);

                preparedStatement.setLong(1, user_id);
                ResultSet rs = preparedStatement.executeQuery();

                while(rs.next()) {
                    Contacts contacts = new Contacts();
                    contacts.setUser_id(rs.getLong("user_id"));
                    contacts.setUser_contact_id(rs.getLong("user_contact_id"));
                    contacts.setStatus(rs.getString("status"));
                    lst.add(contacts);
                }
                rs.close();
                preparedStatement.close();
            }
        } catch (Exception e){
            e.printStackTrace();
        }
        return lst;
    }
}
