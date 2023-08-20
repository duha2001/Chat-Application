package com.example.d_five.utilities;

import android.annotation.SuppressLint;
import android.os.StrictMode;
import android.util.Log;

import java.sql.Connection;
import java.sql.DriverManager;

public class ConnectionDatabaseHelper {

    private static final String username = "dfive";
    private static final String password = "dfive";
    private static final String host = "";
    private static final String port = "5432";
    private static final String database = "postgres";
    private static String Classes = "org.postgresql.Driver";
    private static String url = "jdbc:postgresql://"+host+":"+port+"/"+database;

    //Render Online db
//    private static final String username = "dfive";
//    private static final String password = "RRNyorOt67EYDHzaLBKShVm661wCqp7w";
//    private static final String database = "dfive";
//    private static String Classes = "org.postgresql.Driver";
//    private static String url = "jdbc:postgresql://dpg-cimg6rmnqqldjql4il3g-a.singapore-postgres.render.com/dfive";

    Connection connection;

    @SuppressLint("NewApi")
    public Connection connectionClass(){
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        connection = null;
        try {

            Class.forName(Classes);
            connection = DriverManager.getConnection(url, username, password);
        } catch (Exception e){
            Log.e("Error Connect DB !!", e.getMessage());
        }

        return connection;
    }


}
