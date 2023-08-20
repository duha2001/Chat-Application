package com.example.dfive;


import com.example.d_five.dao.implementDAO.UserDAO;
import com.example.d_five.model.User;
import com.example.d_five.utilities.ConnectionDatabaseHelper;

import org.junit.Test;

import java.sql.Connection;

public class DatabaseTest {

    @Test
    public void testDB() throws Exception {

        Connection connection = new ConnectionDatabaseHelper().connectionClass();

        if(connection !=null){
            System.out.println("connect ok");
        }
        //UserDAO userDAO = new UserDAO(connection);

        //User u = userDAO.getInfoUser("hieu");

        //System.out.println(u);

    }
}
