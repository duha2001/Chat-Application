package com.example.d_five.dao;

import com.example.d_five.utilities.ConnectionDatabaseHelper;

import java.sql.Connection;

public class ConnectionDB {

    public static Connection connection;

    public ConnectionDB() {
        try {
            ConnectionDatabaseHelper connectionDatabaseHelper = new ConnectionDatabaseHelper();
            connection = connectionDatabaseHelper.connectionClass();

            if(connection != null){
                System.out.println("Connect Database successfully !!");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
