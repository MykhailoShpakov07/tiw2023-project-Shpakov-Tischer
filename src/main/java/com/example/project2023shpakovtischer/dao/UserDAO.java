package com.example.project2023shpakovtischer.dao;

import java.sql.Connection;

public class UserDAO {

    private Connection connection;

    public UserDAO(Connection connection){
        this.connection = connection;
    }
}
